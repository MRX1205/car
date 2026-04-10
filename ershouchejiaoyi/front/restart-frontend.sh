#!/bin/bash

# 杀死所有前端进程
echo "🛑 停止前端服务..."
pkill -f "npm run dev" || true
pkill -f "vite" || true
sleep 2

# 清理缓存
echo "🧹 清理缓存..."
cd /Users/lyhm/Study/Projects/ershouchejiaoyi/front
rm -rf node_modules/.vite || true

# 重新启动前端
echo "🚀 启动前端服务..."
npm run dev

echo ""
echo "✅ 前端服务已启动"
echo "📱 访问: http://localhost:5173"
