-- ========================================
-- 二手车交易系统数据库更新脚本
-- 更新日期: 2024年
-- 功能说明:
--   1. 为 users 表添加注册码字段 register_code
--   2. 为 yonghu 表添加支付密码字段 pay_password
--   3. 为现有数据设置默认值
-- ========================================

USE ershouchejiaoyi;

-- ========================================
-- 1. 更新 users 表 - 添加注册码字段
-- ========================================

-- 检查字段是否存在，如果不存在则添加
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'ershouchejiaoyi'
    AND TABLE_NAME = 'users'
    AND COLUMN_NAME = 'register_code'
);

-- 如果字段不存在，则添加
SET @sql_add_register_code = IF(
    @column_exists = 0,
    'ALTER TABLE `users` ADD COLUMN `register_code` varchar(100) DEFAULT NULL COMMENT ''注册码'' AFTER `role`',
    'SELECT ''字段 register_code 已存在'' AS message'
);

PREPARE stmt FROM @sql_add_register_code;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 为现有管理员账号设置注册码
UPDATE `users` SET `register_code` = 'admin' WHERE `username` = 'admin' AND `register_code` IS NULL;

-- ========================================
-- 2. 更新 yonghu 表 - 添加支付密码字段
-- ========================================

-- 检查字段是否存在，如果不存在则添加
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'ershouchejiaoyi'
    AND TABLE_NAME = 'yonghu'
    AND COLUMN_NAME = 'pay_password'
);

-- 如果字段不存在，则添加
SET @sql_add_pay_password = IF(
    @column_exists = 0,
    'ALTER TABLE `yonghu` ADD COLUMN `pay_password` varchar(200) DEFAULT NULL COMMENT ''支付密码'' AFTER `password`',
    'SELECT ''字段 pay_password 已存在'' AS message'
);

PREPARE stmt FROM @sql_add_pay_password;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 为现有用户设置默认支付密码（与登录密码相同）
UPDATE `yonghu` SET `pay_password` = `password` WHERE `pay_password` IS NULL OR `pay_password` = '';

-- ========================================
-- 3. 验证更新结果
-- ========================================

-- 查看 users 表结构
SELECT
    '验证 users 表更新' AS 说明,
    COUNT(*) AS 注册码字段数量
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'ershouchejiaoyi'
AND TABLE_NAME = 'users'
AND COLUMN_NAME = 'register_code';

-- 查看 yonghu 表结构
SELECT
    '验证 yonghu 表更新' AS 说明,
    COUNT(*) AS 支付密码字段数量
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'ershouchejiaoyi'
AND TABLE_NAME = 'yonghu'
AND COLUMN_NAME = 'pay_password';

-- 查看更新后的数据统计
SELECT
    '用户支付密码设置情况' AS 统计项,
    COUNT(*) AS 总用户数,
    SUM(CASE WHEN pay_password IS NOT NULL AND pay_password != '' THEN 1 ELSE 0 END) AS 已设置支付密码数,
    SUM(CASE WHEN pay_password IS NULL OR pay_password = '' THEN 1 ELSE 0 END) AS 未设置支付密码数
FROM yonghu;

SELECT
    '管理员注册码设置情况' AS 统计项,
    COUNT(*) AS 总管理员数,
    SUM(CASE WHEN register_code IS NOT NULL AND register_code != '' THEN 1 ELSE 0 END) AS 已设置注册码数,
    SUM(CASE WHEN register_code IS NULL OR register_code = '' THEN 1 ELSE 0 END) AS 未设置注册码数
FROM users;

-- ========================================
-- 更新完成说明
-- ========================================

SELECT
    '数据库更新完成' AS 状态,
    '已添加 users.register_code 字段' AS 更新1,
    '已添加 yonghu.pay_password 字段' AS 更新2,
    '已为现有数据设置默认值' AS 更新3,
    '管理员注册码默认为 admin' AS 说明1,
    '用户支付密码默认与登录密码相同' AS 说明2;
