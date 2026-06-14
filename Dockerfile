# ===== Stage 1: Build Frontend =====
FROM node:22-alpine AS frontend-builder
WORKDIR /frontend
COPY Yuni-front/package.json Yuni-front/package-lock.json* ./
RUN npm ci --silent
COPY Yuni-front/ .
RUN npx vite build --outDir dist

# ===== Stage 2: Build Backend =====
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

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

COPY . .
RUN mvn clean package -pl yuni-application -am -DskipTests -q

# ===== Stage 3: Runtime =====
FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache redis nginx supervisor sqlite bash

WORKDIR /app
RUN mkdir -p /app/config /app/data /app/logs /app/plugins /app/log

# ---- 复制构建产物 ----
COPY --from=builder /build/yuni-application/target/yuni-app.jar /app/yuni-app.jar
COPY --from=frontend-builder /frontend/dist/ /app/static/
COPY sql/ /app/sql/

# ---- Nginx 配置 ----
RUN printf 'server {\n\
    listen 80;\n\
    server_name _;\n\
\n\
    # Vue 前端\n\
    root /app/static;\n\
    index index.html;\n\
\n\
    # API 代理到 Spring Boot\n\
    location /admin/ {\n\
        proxy_pass http://127.0.0.1:11451;\n\
        proxy_set_header Host $host;\n\
        proxy_set_header X-Real-IP $remote_addr;\n\
    }\n\
\n\
    # WebSocket 代理\n\
    location /ws/ {\n\
        proxy_pass http://127.0.0.1:11451;\n\
        proxy_http_version 1.1;\n\
        proxy_set_header Upgrade $http_upgrade;\n\
        proxy_set_header Connection "upgrade";\n\
        proxy_set_header Host $host;\n\
        proxy_set_header X-Real-IP $remote_addr;\n\
    }\n\
\n\
    # SPA fallback：非 API 路径返回 index.html\n\
    location / {\n\
        try_files $uri $uri/ /index.html;\n\
    }\n\
}\n' > /etc/nginx/http.d/default.conf \
 && mkdir -p /run/nginx \
 && sed -i 's/user nginx;//' /etc/nginx/nginx.conf

# ---- Redis 配置 ----
RUN echo 'port 11452'           > /etc/redis.conf \
 && echo 'bind 0.0.0.0'        >> /etc/redis.conf \
 && echo 'protected-mode no'    >> /etc/redis.conf \
 && echo 'daemonize no'         >> /etc/redis.conf \
 && echo 'loglevel notice'      >> /etc/redis.conf

# ---- supervisord ----
RUN printf '[supervisord]\nnodaemon=true\nlogfile=/app/logs/supervisord.log\nuser=root\n\n'       > /etc/supervisord.conf \
 && printf '[program:redis]\ncommand=redis-server /etc/redis.conf\nstdout_logfile=/dev/stdout\nstdout_logfile_maxbytes=0\nstderr_logfile=/dev/stderr\nstderr_logfile_maxbytes=0\n\n' >> /etc/supervisord.conf \
 && printf '[program:nginx]\ncommand=nginx -g "daemon off;"\nstdout_logfile=/dev/stdout\nstdout_logfile_maxbytes=0\nstderr_logfile=/dev/stderr\nstderr_logfile_maxbytes=0\n\n' >> /etc/supervisord.conf \
 && printf '[program:yuni]\ncommand=java -jar /app/yuni-app.jar --spring.profiles.active=prod --spring.config.location=./config/application-prod.yml\nstdout_logfile=/dev/stdout\nstdout_logfile_maxbytes=0\nstderr_logfile=/dev/stderr\nstderr_logfile_maxbytes=0\n' >> /etc/supervisord.conf

# ---- entrypoint ----
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

EXPOSE 80 11452

ENTRYPOINT ["/app/entrypoint.sh"]
