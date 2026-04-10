#!/bin/bash

# 二手车交易平台 - 完整启动和测试脚本

echo "================================"
echo "二手车交易平台 - 启动测试"
echo "================================"
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 1. 检查后端
echo -e "${YELLOW}1️⃣  检查后端服务...${NC}"
if lsof -i :8080 2>/dev/null | grep -q java; then
    echo -e "${GREEN}✅ 后端服务运行中${NC}"
    BACKEND_RUNNING=1
else
    echo -e "${RED}❌ 后端服务未运行${NC}"
    echo "请在新终端执行:"
    echo "  cd /Users/lyhm/Study/Projects/ershouchejiaoyi"
    echo "  mvn spring-boot:run"
    echo ""
    BACKEND_RUNNING=0
fi

echo ""

# 2. 检查前端
echo -e "${YELLOW}2️⃣  检查前端服务...${NC}"
if curl -s http://localhost:5173 2>/dev/null | grep -q "app"; then
    echo -e "${GREEN}✅ 前端服务运行中${NC}"
    FRONTEND_RUNNING=1
else
    echo -e "${RED}❌ 前端服务未运行${NC}"
    echo "请在新终端执行:"
    echo "  cd /Users/lyhm/Study/Projects/ershouchejiaoyi/front"
    echo "  npm run dev"
    echo ""
    FRONTEND_RUNNING=0
fi

echo ""

# 3. 如果后端运行，测试登录API
if [ $BACKEND_RUNNING -eq 1 ]; then
    echo -e "${YELLOW}3️⃣  测试后端登录API...${NC}"
    RESPONSE=$(curl -s 'http://localhost:8080/ershouchejiaoyi/yonghu/login?username=a1&password=123456' 2>&1)
    
    if echo "$RESPONSE" | grep -q '"code"'; then
        echo -e "${GREEN}✅ 后端API响应正常${NC}"
        echo "响应: $RESPONSE" | head -c 100
        echo ""
    else
        echo -e "${RED}❌ 后端API无响应${NC}"
        echo "响应: $RESPONSE"
    fi
else
    echo -e "${YELLOW}3️⃣  跳过API测试（后端未运行）${NC}"
fi

echo ""

# 4. 总结
echo "================================"
echo "启动状态总结"
echo "================================"

if [ $BACKEND_RUNNING -eq 1 ] && [ $FRONTEND_RUNNING -eq 1 ]; then
    echo -e "${GREEN}✅ 所有服务运行正常！${NC}"
    echo ""
    echo "现在可以登录了："
    echo "  📱 访问: http://localhost:5173"
    echo "  👤 账号: a1"
    echo "  🔑 密码: 123456"
    echo "  👥 角色: 普通用户 或 商家"
else
    echo -e "${RED}❌ 有服务未运行${NC}"
    echo ""
    if [ $BACKEND_RUNNING -eq 0 ]; then
        echo -e "${YELLOW}需要启动后端：${NC}"
        echo "  cd /Users/lyhm/Study/Projects/ershouchejiaoyi"
        echo "  mvn spring-boot:run"
    fi
    echo ""
    if [ $FRONTEND_RUNNING -eq 0 ]; then
        echo -e "${YELLOW}需要启动前端：${NC}"
        echo "  cd /Users/lyhm/Study/Projects/ershouchejiaoyi/front"
        echo "  npm run dev"
    fi
fi

echo ""
echo "================================"
