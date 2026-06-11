#!/bin/bash
# Yuni3 服务器端部署脚本
# 由 webhook 触发，或手动执行
# 用法: ./deploy.sh [image_tag]

set -euo pipefail

DEPLOY_DIR="$(cd "$(dirname "$0")" && pwd)"
IMAGE="${1:-yuier/yuni3:latest}"
BACKUP_IMAGE="${2:-ghcr.io/liyuier/yuni3:latest}"
PLUGINS_JSON="$DEPLOY_DIR/plugins.json"

# GitHub 文件代理列表（按优先级尝试，腾讯云等国内服务器直连 GitHub 可能超时）
GHPROXIES=(
  "https://ghproxy.com/"
  "https://mirror.ghproxy.com/"
  ""
)

# ── 可选: 更新 plugins.json ──────────────────────────────────────────
# 如果需要每次部署都拉取最新的 plugins.json（推荐），解开下面这行：
# curl -fsSL "https://raw.githubusercontent.com/liyuier/Yuni3/master/plugins.json" -o "$PLUGINS_JSON"

# ── 检查依赖 ─────────────────────────────────────────────────────────
command -v jq   >/dev/null 2>&1 || { echo "需要安装 jq: apt install jq";   exit 1; }
command -v curl >/dev/null 2>&1 || { echo "需要安装 curl";                 exit 1; }

# ── 准备目录 ─────────────────────────────────────────────────────────
mkdir -p "$DEPLOY_DIR/plugins" "$DEPLOY_DIR/data" "$DEPLOY_DIR/logs" "$DEPLOY_DIR/config"
chmod -R 755 "$DEPLOY_DIR/plugins"

# ── 下载插件 ─────────────────────────────────────────────────────────
echo "[deploy] 下载插件..."
# 写入临时文件避免管道中的 subshell 问题（& + wait 需要在同一 shell）
jq -r '.packages[] | "\(.id) \(.repo)"' "$PLUGINS_JSON" > /tmp/plugin-list.txt

while read -r id repo; do
  (
    base="https://github.com/${repo}/releases/download/${id}/${id}.zip"
    ok=0
    for proxy in "${GHPROXIES[@]}"; do
      url="${proxy}${base}"
      if curl -fsSL --connect-timeout 5 --max-time 120 "$url" -o "/tmp/${id}.zip"; then
        mkdir -p "$DEPLOY_DIR/plugins/$id"
        unzip -o "/tmp/${id}.zip" -d "$DEPLOY_DIR/plugins/" >/dev/null 2>&1
        rm "/tmp/${id}.zip"
        echo "  $id OK"
        ok=1
        break
      fi
    done
    [ "$ok" -eq 0 ] && echo "  $id FAIL (所有代理均不可达)"
  ) &
done < /tmp/plugin-list.txt

wait
rm /tmp/plugin-list.txt
echo "[deploy] 插件下载完成"

# ── 启动 / 更新容器 ──────────────────────────────────────────────────
echo "[deploy] 拉取镜像 $IMAGE ..."
if ! docker pull "$IMAGE"; then
  echo "[deploy] Docker Hub 拉取失败，尝试备份镜像 $BACKUP_IMAGE ..."
  docker pull "$BACKUP_IMAGE"
  docker tag "$BACKUP_IMAGE" "$IMAGE"
fi

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
  -v "/etc/localtime:/etc/localtime:ro" \
  -v "/etc/timezone:/etc/timezone:ro" \
  "$IMAGE"

echo "[deploy] 完成。日志: docker logs -f yuni3"
