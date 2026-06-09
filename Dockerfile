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

# 安装 Redis 和 supervisor
RUN apk add --no-cache redis supervisor bash

WORKDIR /app

# 创建目录结构
RUN mkdir -p /app/config /app/data /app/logs /app/plugins /app/log

# 复制构建产物
COPY --from=builder /build/yuni-application/target/yuni-app.jar /app/yuni-app.jar

# Redis 配置 — 监听 11452 端口
RUN echo 'port 11452'           > /etc/redis.conf \
 && echo 'bind 0.0.0.0'        >> /etc/redis.conf \
 && echo 'protected-mode no'    >> /etc/redis.conf \
 && echo 'daemonize no'         >> /etc/redis.conf \
 && echo 'loglevel notice'      >> /etc/redis.conf

# supervisord 配置 — 所有输出到 stdout/stderr，通过 docker logs 查看
RUN printf '[supervisord]\nnodaemon=true\nlogfile=/dev/stdout\n\n'                                 > /etc/supervisord.conf \
 && printf '[program:redis]\ncommand=redis-server /etc/redis.conf\nstdout_logfile=/dev/stdout\nstderr_logfile=/dev/stderr\n\n' >> /etc/supervisord.conf \
 && printf '[program:yuni]\ncommand=java -jar /app/yuni-app.jar --spring.profiles.active=prod --spring.config.location=./config/application-prod.yml\nstdout_logfile=/dev/stdout\nstderr_logfile=/dev/stderr\n' >> /etc/supervisord.conf

EXPOSE 11451 11452

CMD ["supervisord", "-c", "/etc/supervisord.conf"]
