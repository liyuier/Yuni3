# ===== Stage 1: Build =====
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# 分层复制 pom，利用 Docker 缓存依赖
COPY pom.xml .
COPY yuni-dependency/pom.xml yuni-dependency/
COPY yuni-core/pom.xml yuni-core/
COPY yuni-event/pom.xml yuni-event/
COPY yuni-adapter/pom.xml yuni-adapter/
COPY yuni-plugin/pom.xml yuni-plugin/
COPY yuni-permission/pom.xml yuni-permission/
COPY yuni-engine/pom.xml yuni-engine/
COPY yuni-webapi/pom.xml yuni-webapi/
COPY yuni-application/pom.xml yuni-application/

RUN mvn dependency:go-offline -pl yuni-application -am -q

# 复制源码并构建
COPY . .
RUN mvn clean package -pl yuni-application -am -DskipTests -q

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jre-alpine

# 安装 Redis、supervisor、sqlite
RUN apk add --no-cache redis supervisor sqlite bash

WORKDIR /app

# 创建目录结构
RUN mkdir -p /app/config /app/data /app/logs /app/plugins /app/log

# 复制构建产物
COPY --from=builder /build/yuni-application/target/yuni-app.jar /app/yuni-app.jar

# 复制建表 SQL 脚本
COPY sql/ /app/sql/

# Redis 配置 — 监听 11452 端口
RUN echo 'port 11452'           > /etc/redis.conf \
 && echo 'bind 0.0.0.0'        >> /etc/redis.conf \
 && echo 'protected-mode no'    >> /etc/redis.conf \
 && echo 'daemonize no'         >> /etc/redis.conf \
 && echo 'loglevel notice'      >> /etc/redis.conf

# supervisord 配置
# supervisord 自身日志写文件（/dev/stdout 无法 seek，supervisord 不支持）
# 守护程序的 stdout/stderr 直接输出到 docker logs
RUN printf '[supervisord]\nnodaemon=true\nlogfile=/app/logs/supervisord.log\nuser=root\n\n'       > /etc/supervisord.conf \
 && printf '[program:redis]\ncommand=redis-server /etc/redis.conf\nstdout_logfile=/dev/stdout\nstdout_logfile_maxbytes=0\nstderr_logfile=/dev/stderr\nstderr_logfile_maxbytes=0\n\n' >> /etc/supervisord.conf \
 && printf '[program:yuni]\ncommand=java -jar /app/yuni-app.jar --spring.profiles.active=prod --spring.config.location=./config/application-prod.yml\nstdout_logfile=/dev/stdout\nstdout_logfile_maxbytes=0\nstderr_logfile=/dev/stderr\nstderr_logfile_maxbytes=0\n' >> /etc/supervisord.conf

# 数据库初始化 + 启动脚本
RUN printf '#!/bin/bash\n\
set -e\n\
DB_FILE=/app/data/yuni3.db\n\
echo "[init] 检查数据库文件..."\n\
if [ ! -f "$DB_FILE" ]; then\n\
  echo "[init] 创建数据库文件..."\n\
  touch "$DB_FILE"\n\
fi\n\
echo "[init] 执行建表 SQL..."\n\
for sql in /app/sql/*.sql; do\n\
  echo "[init]   $(basename $sql)"\n\
  sqlite3 "$DB_FILE" < "$sql"\n\
done\n\
echo "[init] 数据库初始化完成"\n\
echo "[init] 启动服务..."\n\
exec supervisord -c /etc/supervisord.conf\n\
' > /app/entrypoint.sh && chmod +x /app/entrypoint.sh

EXPOSE 11451 11452

ENTRYPOINT ["/app/entrypoint.sh"]
