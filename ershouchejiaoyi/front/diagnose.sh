#!/bin/bash

# 二手车交易平台 - 登录问题诊断脚本

echo "================================"
echo "登录问题诊断工具"
echo "================================"
echo ""

# 1. 检查后端是否运行
echo "1️⃣  检查后端服务..."
if curl -s http://localhost:8080/ershouchejiaoyi/yonghu/login?username=test 2>/dev/null | grep -q "code"; then
    echo "✅ 后端服务运行中"
else
    echo "❌ 后端服务未运行"
    echo "   请执行: cd /Users/lyhm/Study/Projects/ershouchejiaoyi && mvn spring-boot:run"
fi
echo ""

# 2. 检查前端是否运行
echo "2️⃣  检查前端服务..."
if curl -s http://localhost:5173 2>/dev/null | grep -q "app"; then
    echo "✅ 前端服务运行中"
else
    echo "❌ 前端服务未运行"
    echo "   请执行: cd /Users/lyhm/Study/Projects/ershouchejiaoyi/front && npm run dev"
fi
echo ""

# 3. 检查数据库连接
echo "3️⃣  检查数据库..."
if mysql -u root -p123456 -e "SELECT 1 FROM ershouchejiaoyi.yonghu LIMIT 1;" 2>/dev/null; then
    echo "✅ 数据库连接正常"
    
    # 检查用户是否存在
    echo ""
    echo "   检查测试用户..."
    USER_COUNT=$(mysql -u root -p123456 -e "SELECT COUNT(*) FROM ershouchejiaoyi.yonghu WHERE username='a1';" 2>/dev/null | tail -1)
    if [ "$USER_COUNT" -gt 0 ]; then
        echo "   ✅ 用户 a1 存在"
    else
        echo "   ❌ 用户 a1 不存在"
        echo "   请执行以下SQL插入测试用户:"
        echo "   INSERT INTO yonghu (username, password, yonghu_name, yonghu_phone, create_time)"
        echo "   VALUES ('a1', '123456', '测试用户', '13800138000', NOW());"
    fi
else
    echo "❌ 数据库连接失败"
    echo "   请检查:"
    echo "   1. MySQL是否运行"
    echo "   2. 用户名密码是否正确"
    echo "   3. 数据库是否存在"
fi
echo ""

# 4. 检查API配置
echo "4️⃣  检查API配置..."
if grep -q "baseURL.*ershouchejiaoyi" /Users/lyhm/Study/Projects/ershouchejiaoyi/front/src/utils/request.js; then
    echo "✅ API地址配置正确"
else
    echo "❌ API地址配置可能有问题"
    echo "   请检查: src/utils/request.js"
fi
echo ""

# 5. 总结
echo "================================"
echo "诊断完成"
echo "================================"
echo ""
echo "如果所有检查都通过，请:"
echo "1. 打开浏览器访问 http://localhost:5173"
echo "2. 输入账号: a1"
echo "3. 输入密码: 123456"
echo "4. 选择角色: 普通用户"
echo "5. 点击登录"
echo ""
echo "如果仍然无法登录，请查看:"
echo "📖 /Users/lyhm/Study/Projects/ershouchejiaoyi/front/LOGIN_TROUBLESHOOTING.md"
