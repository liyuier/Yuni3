#!/bin/bash
# Yuni3 服务器端部署脚本
# 由 webhook 触发，或手动执行
# 用法: ./deploy.sh [image_tag]

set -euo pipefail

DEPLOY_DIR="$(cd "$(dirname "$0")" && pwd)"
IMAGE="${1:-ghcr.io/liyuier/yuni3:latest}"
PLUGINS_JSON="$DEPLOY_DIR/plugins.json"

# ── 可选: 更新 plugins.json ──────────────────────────────────────────
# 如果需要每次部署都拉取最新的 plugins.json（推荐），解开下面这行：
# curl -fsSL "https://raw.githubusercontent.com/liyuier/Yuni3/master/plugins.json" -o "$PLUGINS_JSON"

# ── 检查依赖 ─────────────────────────────────────────────────────────
command -v jq   >/dev/null 2>&1 || { echo "需要安装 jq: apt install jq";   exit 1; }
command -v curl >/dev/null 2>&1 || { echo "需要安装 curl";                 exit 1; }

# ── 准备目录 ─────────────────────────────────────────────────────────
mkdir -p "$DEPLOY_DIR/plugins" "$DEPLOY_DIR/data" "$DEPLOY_DIR/logs" "$DEPLOY_DIR/config"
chmod -R 755 "$DEPLOY_DIR/plugins"

# ── 下载插件 JAR ─────────────────────────────────────────────────────
echo "[deploy] 下载插件..."
jq -r '.packages[] | "\(.id) \(.repo)"' "$PLUGINS_JSON" | while read -r id repo; do
  url="https://github.com/${repo}/releases/download/${id}/${id}.jar"
  dir="$DEPLOY_DIR/plugins/$id"
  mkdir -p "$dir"
  echo "  $id <- $url"
  curl -fsSL "$url" -o "$dir/${id}.jar" || echo "  [WARN] $id 下载失败，保留旧版本"
done

echo "[deploy] 插件下载完成"

# ── 启动 / 更新容器 ──────────────────────────────────────────────────
echo "[deploy] 拉取镜像 $IMAGE ..."
docker pull "$IMAGE"

if docker ps -q -f name=yuni3 | grep -q .; then
  echo "[deploy] 停止旧容器..."
  docker stop yuni3
  docker rm yuni3
fi

echo "[deploy] 启动容器..."
docker run -d \
  --name yuni3 \
  --restart unless-stopped \
  -p 11451:11451 \
  -p 11452:11452 \
  -v "$DEPLOY_DIR/config:/app/config:ro" \
  -v "$DEPLOY_DIR/data:/app/data" \
  -v "$DEPLOY_DIR/logs:/app/logs" \
  -v "$DEPLOY_DIR/plugins:/app/plugins" \
  "$IMAGE"

echo "[deploy] 完成。日志: docker logs -f yuni3"
