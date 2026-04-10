-- MySQL dump 10.13  Distrib 5.7.24, for osx11.1 (x86_64)
--
-- Host: 127.0.0.1    Database: ershouchejiaoyi
-- ------------------------------------------------------
-- Server version	5.7.30-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cheliang`
--

DROP TABLE IF EXISTS `cheliang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cheliang` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键 ',
  `shangjia_id` int(11) DEFAULT NULL COMMENT '商家',
  `cheliang_name` varchar(200) DEFAULT NULL COMMENT '汽车名称  Search111 ',
  `cheliang_photo` varchar(200) DEFAULT NULL COMMENT '汽车照片',
  `cheliang_types` int(11) DEFAULT NULL COMMENT '汽车品牌 Search111',
  `cheliang_new_money` decimal(10,2) DEFAULT NULL COMMENT '价格 ',
  `cheliang_clicknum` int(11) DEFAULT NULL COMMENT '点击次数',
  `zan_number` int(11) DEFAULT NULL COMMENT '赞',
  `cai_number` int(11) DEFAULT NULL COMMENT '踩',
  `cheliang_content` text COMMENT '汽车介绍 ',
  `shangxia_types` int(11) DEFAULT NULL COMMENT '是否上架 ',
  `cheliang_delete` int(11) DEFAULT NULL COMMENT '逻辑删除',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间  show1 show2 photoShow',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='汽车';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cheliang`
--

LOCK TABLES `cheliang` WRITE;
/*!40000 ALTER TABLE `cheliang` DISABLE KEYS */;
INSERT INTO `cheliang` VALUES (1,2,'汽车名称1','http://localhost:8080/ershouchejiaoyi/upload/cheliang1.jpg',2,174.44,444,451,199,'汽车介绍1',1,1,'2022-04-25 03:59:30'),(2,1,'汽车名称2','http://localhost:8080/ershouchejiaoyi/upload/cheliang2.jpg',2,27.59,215,484,231,'汽车介绍2',1,1,'2022-04-25 03:59:30'),(3,2,'汽车名称3','http://localhost:8080/ershouchejiaoyi/upload/cheliang3.jpg',1,203.85,58,99,34,'汽车介绍3',1,1,'2022-04-25 03:59:30'),(4,3,'汽车名称4','http://localhost:8080/ershouchejiaoyi/upload/cheliang4.jpg',2,217.38,346,63,177,'汽车介绍4',1,1,'2022-04-25 03:59:30'),(5,2,'汽车名称5','http://localhost:8080/ershouchejiaoyi/upload/cheliang5.jpg',1,308.96,147,154,377,'汽车介绍5',1,1,'2022-04-25 03:59:30'),(6,1,'小米su7','http://localhost:8080/ershouchejiaoyi/upload/1773977984786.png',1,21.99,6,1,0,'<p>小米</p>',2,1,'2026-03-20 03:39:54'),(7,5,'精选高品质代步好车','car1.jpeg',3,3.50,19,2,0,'<p>车况极佳，原版原漆，按时保养，是日常代步的绝佳选择。没有泡水没有火烧，支持第三方检测！</p>',2,1,'2026-03-21 20:14:20'),(8,5,'大气豪华商务座驾','car2.jpeg',4,12.80,42,5,1,'<p>这是一辆非常奢华的商务型用车，内饰几乎保持九成新，空间宽敞无比。老板一手车，手续齐全，随时准备过户。</p>',1,1,'2026-03-21 20:14:20'),(9,5,'非常适合全家出游的旅行车','car3.jpeg',5,8.60,23,1,0,'<p>宽敞的大空间，舒适的座椅，极为平顺的底盘悬挂，极其适合二胎家庭周末出游，性能依然非常稳定。</p>',2,1,'2026-03-21 20:14:20'),(10,5,'充满肌肉感的城市运动小钢炮','car4.jpeg',3,6.20,90,12,1,'<p>动力随叫随到，百公里加速表现亮眼，操控极其灵活！非常适合喜欢驾驶乐趣的年轻人，底盘紧致如新。</p>',1,1,'2026-03-21 20:14:20'),(11,5,'超凡性价比实用优选','car5.jpeg',4,4.50,33,4,0,'<p>极度省油耐造，无论是刚拿驾照练手还是日常上下班通勤，这款车都表现得相当完美，综合性价比全场最高。</p>',1,1,'2026-03-21 20:14:20');
/*!40000 ALTER TABLE `cheliang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cheliang_cart`
--

DROP TABLE IF EXISTS `cheliang_cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cheliang_cart` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cheliang_id` int(11) DEFAULT NULL COMMENT '汽车',
  `yonghu_id` int(11) DEFAULT NULL COMMENT '用户',
  `insert_time` timestamp NULL DEFAULT NULL COMMENT '加入时间',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='购物车';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cheliang_cart`
--

LOCK TABLES `cheliang_cart` WRITE;
/*!40000 ALTER TABLE `cheliang_cart` DISABLE KEYS */;
INSERT INTO `cheliang_cart` VALUES (2,2,1,'2022-04-25 04:35:00','2022-04-25 04:35:00'),(3,3,1,'2026-03-20 03:35:29','2026-03-20 03:35:29'),(4,6,1,'2026-03-20 17:53:47','2026-03-20 17:53:47'),(5,5,NULL,'2026-03-20 19:34:45','2026-03-20 19:34:45'),(6,6,NULL,'2026-03-21 20:06:10','2026-03-21 20:06:10'),(7,7,NULL,'2026-03-21 21:00:03','2026-03-21 21:00:03'),(8,7,NULL,'2026-03-21 21:00:13','2026-03-21 21:00:13');
/*!40000 ALTER TABLE `cheliang_cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cheliang_collection`
--

DROP TABLE IF EXISTS `cheliang_collection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cheliang_collection` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cheliang_id` int(11) DEFAULT NULL COMMENT '汽车',
  `yonghu_id` int(11) DEFAULT NULL COMMENT '用户',
  `cheliang_collection_types` int(11) DEFAULT NULL COMMENT '类型',
  `insert_time` timestamp NULL DEFAULT NULL COMMENT '收藏时间',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间 show3 photoShow',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT='汽车收藏';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cheliang_collection`
--

LOCK TABLES `cheliang_collection` WRITE;
/*!40000 ALTER TABLE `cheliang_collection` DISABLE KEYS */;
INSERT INTO `cheliang_collection` VALUES (2,2,3,1,'2022-04-25 03:59:30','2022-04-25 03:59:30'),(3,3,2,1,'2022-04-25 03:59:30','2022-04-25 03:59:30'),(4,4,3,1,'2022-04-25 03:59:30','2022-04-25 03:59:30'),(5,5,3,1,'2022-04-25 03:59:30','2022-04-25 03:59:30'),(6,1,1,2,'2022-04-25 06:57:03','2022-04-25 06:57:03'),(8,2,1,1,'2022-04-25 06:57:15','2022-04-25 06:57:15'),(9,2,1,2,'2022-04-25 06:57:29','2022-04-25 06:57:29'),(10,2,4,1,'2026-03-21 08:15:15','2026-03-21 08:15:15'),(11,6,NULL,1,'2026-03-21 20:08:26','2026-03-21 20:08:26'),(12,6,NULL,1,'2026-03-21 20:08:33','2026-03-21 20:08:33'),(13,5,NULL,1,'2026-03-21 20:14:08','2026-03-21 20:14:08');
/*!40000 ALTER TABLE `cheliang_collection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cheliang_liuyan`
--

DROP TABLE IF EXISTS `cheliang_liuyan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cheliang_liuyan` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cheliang_id` int(11) DEFAULT NULL COMMENT '汽车',
  `yonghu_id` int(11) DEFAULT NULL COMMENT '用户',
  `cheliang_liuyan_text` text COMMENT '留言内容',
  `insert_time` timestamp NULL DEFAULT NULL COMMENT '留言时间',
  `reply_text` text COMMENT '回复内容',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '回复时间',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='汽车留言';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cheliang_liuyan`
--

LOCK TABLES `cheliang_liuyan` WRITE;
/*!40000 ALTER TABLE `cheliang_liuyan` DISABLE KEYS */;
INSERT INTO `cheliang_liuyan` VALUES (1,1,1,'留言内容1','2022-04-25 03:59:30','回复信息1','2022-04-25 03:59:30','2022-04-25 03:59:30'),(2,2,2,'留言内容2','2022-04-25 03:59:30','回复信息2','2022-04-25 03:59:30','2022-04-25 03:59:30'),(3,3,2,'留言内容3','2022-04-25 03:59:30','回复信息3','2022-04-25 03:59:30','2022-04-25 03:59:30'),(4,4,2,'留言内容4','2022-04-25 03:59:30','回复信息4','2022-04-25 03:59:30','2022-04-25 03:59:30'),(5,5,2,'留言内容5','2022-04-25 03:59:30','回复信息5','2022-04-25 03:59:30','2022-04-25 03:59:30'),(6,2,1,'留言123','2022-04-25 06:57:25','123','2022-04-25 07:00:25','2022-04-25 06:57:25'),(7,6,4,'可以吖\n','2026-03-21 08:27:16','是的','2026-03-21 08:56:12','2026-03-21 08:27:16'),(8,9,4,'我买的\n','2026-03-21 20:36:59',NULL,NULL,'2026-03-21 20:36:59');
/*!40000 ALTER TABLE `cheliang_liuyan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cheliang_order`
--

DROP TABLE IF EXISTS `cheliang_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cheliang_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cheliang_order_uuid_number` varchar(200) DEFAULT NULL COMMENT '订单号',
  `cheliang_id` int(11) DEFAULT NULL COMMENT '汽车',
  `yonghu_id` int(11) DEFAULT NULL COMMENT '用户',
  `shangjia_id` int(11) DEFAULT NULL COMMENT '商家',
  `cheliang_order_price` decimal(10,2) DEFAULT NULL COMMENT '订单金额',
  `cheliang_order_status_types` int(11) DEFAULT NULL COMMENT '订单状态',
  `insert_time` timestamp NULL DEFAULT NULL COMMENT '下单时间',
  `pay_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
  `finish_time` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `cancel_reason` varchar(255) DEFAULT NULL COMMENT '取消原因',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='汽车订单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cheliang_order`
--

LOCK TABLES `cheliang_order` WRITE;
/*!40000 ALTER TABLE `cheliang_order` DISABLE KEYS */;
INSERT INTO `cheliang_order` VALUES (1,'20220425120000001',1,1,2,174.44,4,'2022-04-25 04:00:00','2022-04-25 04:10:00','2022-04-25 05:00:00',NULL,'2022-04-25 04:00:00'),(2,'20260320113530128453',3,1,2,203.85,2,'2026-03-20 03:35:30','2026-03-20 03:35:57',NULL,NULL,'2026-03-20 03:35:30'),(3,'20260320113544229636',1,1,2,174.44,2,'2026-03-20 03:35:44','2026-03-20 03:35:50',NULL,NULL,'2026-03-20 03:35:44'),(4,'20260320114052519933',6,1,1,21.99,4,'2026-03-20 03:40:53','2026-03-20 03:40:59','2026-03-20 08:04:42',NULL,'2026-03-20 03:40:53'),(5,'20260321034341479237',4,4,3,217.38,2,'2026-03-20 19:43:41','2026-03-20 19:45:08',NULL,NULL,'2026-03-20 19:43:41'),(6,'20260321034414232201',2,4,1,27.59,2,'2026-03-20 19:44:14','2026-03-20 19:44:21',NULL,NULL,'2026-03-20 19:44:14'),(7,'20260322040613600544',6,4,1,21.99,2,'2026-03-21 20:06:14','2026-03-21 20:06:19',NULL,NULL,'2026-03-21 20:06:14'),(8,'20260322043046509783',9,4,5,8.60,4,'2026-03-21 20:30:47','2026-03-21 20:31:12','2026-03-21 20:44:12',NULL,'2026-03-21 20:30:47'),(9,'20260322052227473326',7,4,5,3.50,2,'2026-03-21 21:22:27','2026-03-21 21:22:32',NULL,NULL,'2026-03-21 21:22:27');
/*!40000 ALTER TABLE `cheliang_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config`
--

DROP TABLE IF EXISTS `config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '配置参数名称',
  `value` varchar(100) DEFAULT NULL COMMENT '配置参数值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='配置文件';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config`
--

LOCK TABLES `config` WRITE;
/*!40000 ALTER TABLE `config` DISABLE KEYS */;
INSERT INTO `config` VALUES (1,'轮播图1','http://localhost:8080/ershouchejiaoyi/upload/1650869539389.jpg'),(2,'轮播图2','http://localhost:8080/ershouchejiaoyi/upload/1650869549689.jpg'),(3,'轮播图3','http://localhost:8080/ershouchejiaoyi/upload/1650869561982.jpg');
/*!40000 ALTER TABLE `config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dictionary`
--

DROP TABLE IF EXISTS `dictionary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dictionary` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dic_code` varchar(200) DEFAULT NULL COMMENT '字段',
  `dic_name` varchar(200) DEFAULT NULL COMMENT '字段名',
  `code_index` int(11) DEFAULT NULL COMMENT '编码',
  `index_name` varchar(200) DEFAULT NULL COMMENT '编码名字  Search111 ',
  `super_id` int(11) DEFAULT NULL COMMENT '父字段id',
  `beizhu` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 COMMENT='字典';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dictionary`
--

LOCK TABLES `dictionary` WRITE;
/*!40000 ALTER TABLE `dictionary` DISABLE KEYS */;
INSERT INTO `dictionary` VALUES (1,'shangxia_types','上下架',1,'上架',NULL,NULL,'2022-04-25 03:59:13'),(2,'shangxia_types','上下架',2,'下架',NULL,NULL,'2022-04-25 03:59:13'),(3,'cheliang_types','汽车品牌',1,'汽车品牌1',NULL,NULL,'2022-04-25 03:59:13'),(4,'cheliang_types','汽车品牌',2,'汽车品牌2',NULL,NULL,'2022-04-25 03:59:13'),(5,'cheliang_types','汽车品牌',3,'汽车品牌3',NULL,NULL,'2022-04-25 03:59:13'),(6,'cheliang_collection_types','收藏表类型',1,'收藏',NULL,NULL,'2022-04-25 03:59:13'),(7,'cheliang_collection_types','收藏表类型',2,'赞',NULL,NULL,'2022-04-25 03:59:13'),(8,'cheliang_collection_types','收藏表类型',3,'踩',NULL,NULL,'2022-04-25 03:59:13'),(9,'sex_types','性别类型',1,'男',NULL,NULL,'2022-04-25 03:59:13'),(10,'sex_types','性别类型',2,'女',NULL,NULL,'2022-04-25 03:59:13'),(11,'news_types','公告类型',1,'公告类型1',NULL,NULL,'2022-04-25 03:59:13'),(12,'news_types','公告类型',2,'公告类型2',NULL,NULL,'2022-04-25 03:59:13'),(13,'news_types','公告类型',3,'活动信息',NULL,NULL,'2022-04-25 03:59:13'),(14,'forum_state_types','帖子状态',1,'发帖',NULL,NULL,'2022-04-25 03:59:13'),(15,'forum_state_types','帖子状态',2,'回帖',NULL,NULL,'2022-04-25 03:59:13'),(16,'cheliang_order_status_types','订单状态',1,'待支付',NULL,NULL,'2022-04-25 03:59:13'),(17,'cheliang_order_status_types','订单状态',2,'已支付待发货',NULL,NULL,'2022-04-25 03:59:13'),(18,'cheliang_order_status_types','订单状态',3,'已发货待完成',NULL,NULL,'2022-04-25 03:59:13'),(19,'cheliang_order_status_types','订单状态',4,'已完成',NULL,NULL,'2022-04-25 03:59:13'),(20,'cheliang_order_status_types','订单状态',5,'已取消',NULL,NULL,'2022-04-25 03:59:13'),(21,'cheliang_order_status_types','订单状态',6,'已拒绝',NULL,NULL,'2022-04-25 03:59:13');
/*!40000 ALTER TABLE `dictionary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum`
--

DROP TABLE IF EXISTS `forum`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `forum` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `forum_name` varchar(200) DEFAULT NULL COMMENT '帖子标题  Search111 ',
  `yonghu_id` int(11) DEFAULT NULL COMMENT '用户',
  `shangjia_id` int(11) DEFAULT NULL COMMENT '商家',
  `users_id` int(11) DEFAULT NULL COMMENT '管理员',
  `forum_content` text COMMENT '发布内容',
  `super_ids` int(11) DEFAULT NULL COMMENT '父id',
  `forum_state_types` int(11) DEFAULT NULL COMMENT '帖子状态',
  `insert_time` timestamp NULL DEFAULT NULL COMMENT '发帖时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间 show2',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='论坛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum`
--

LOCK TABLES `forum` WRITE;
/*!40000 ALTER TABLE `forum` DISABLE KEYS */;
INSERT INTO `forum` VALUES (1,'帖子标题1',NULL,2,NULL,'发布内容1',NULL,1,'2022-04-25 03:59:30','2022-04-25 03:59:30','2022-04-25 03:59:30'),(2,'帖子标题2',NULL,1,NULL,'发布内容2',NULL,1,'2022-04-25 03:59:30','2022-04-25 03:59:30','2022-04-25 03:59:30'),(3,'帖子标题3',NULL,3,NULL,'发布内容3',NULL,1,'2022-04-25 03:59:30','2022-04-25 03:59:30','2022-04-25 03:59:30'),(4,'帖子标题4',NULL,3,NULL,'发布内容4',NULL,1,'2022-04-25 03:59:30','2022-04-25 03:59:30','2022-04-25 03:59:30'),(5,'帖子标题5',NULL,2,NULL,'发布内容5',NULL,1,'2022-04-25 03:59:30','2022-04-25 03:59:30','2022-04-25 03:59:30'),(6,NULL,1,NULL,NULL,'评论内容12',5,2,'2022-04-25 06:57:42',NULL,'2022-04-25 06:57:42'),(7,NULL,NULL,NULL,1,'管理112',5,2,'2022-04-25 06:58:32',NULL,'2022-04-25 06:58:32'),(8,NULL,NULL,1,NULL,'123',5,2,'2022-04-25 07:00:36',NULL,'2022-04-25 07:00:36'),(9,'1',NULL,NULL,NULL,'213',NULL,NULL,'2026-03-21 08:27:58',NULL,'2026-03-21 08:27:58');
/*!40000 ALTER TABLE `forum` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `news` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `news_name` varchar(200) DEFAULT NULL COMMENT '公告标题  Search111 ',
  `news_types` int(11) DEFAULT NULL COMMENT '公告类型  Search111 ',
  `news_photo` varchar(200) DEFAULT NULL COMMENT '公告图片',
  `insert_time` timestamp NULL DEFAULT NULL COMMENT '添加时间',
  `news_content` text COMMENT '公告详情',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间 show1 show2 nameShow',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='公告信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--

LOCK TABLES `news` WRITE;
/*!40000 ALTER TABLE `news` DISABLE KEYS */;
INSERT INTO `news` VALUES (1,'公告标题1',1,'http://localhost:8080/ershouchejiaoyi/upload/news1.jpg','2022-04-25 03:59:30','公告详情1','2022-04-25 03:59:30'),(2,'公告标题2',2,'http://localhost:8080/ershouchejiaoyi/upload/news2.jpg','2022-04-25 03:59:30','公告详情2','2022-04-25 03:59:30'),(3,'公告标题3',3,'http://localhost:8080/ershouchejiaoyi/upload/news3.jpg','2022-04-25 03:59:30','公告详情3','2022-04-25 03:59:30'),(4,'公告标题4',1,'http://localhost:8080/ershouchejiaoyi/upload/news4.jpg','2022-04-25 03:59:30','公告详情4','2022-04-25 03:59:30'),(5,'公告标题5',3,'http://localhost:8080/ershouchejiaoyi/upload/news5.jpg','2022-04-25 03:59:30','公告详情5','2022-04-25 03:59:30');
/*!40000 ALTER TABLE `news` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shangjia`
--

DROP TABLE IF EXISTS `shangjia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `shangjia` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键 ',
  `username` varchar(200) DEFAULT NULL COMMENT '账户 ',
  `password` varchar(200) DEFAULT NULL COMMENT '密码 ',
  `shangjia_name` varchar(200) DEFAULT NULL COMMENT '商家名称 Search111 ',
  `shangjia_phone` varchar(200) DEFAULT NULL COMMENT '联系方式',
  `shangjia_email` varchar(200) DEFAULT NULL COMMENT '邮箱',
  `shangjia_content` text COMMENT '商家简介 ',
  `shangjia_delete` int(11) DEFAULT NULL COMMENT '逻辑删除',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `shangjia_photo` varchar(200) DEFAULT NULL COMMENT '商家头像',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='商家';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shangjia`
--

LOCK TABLES `shangjia` WRITE;
/*!40000 ALTER TABLE `shangjia` DISABLE KEYS */;
INSERT INTO `shangjia` VALUES (1,'a1','123456','商家名称1','17703786901','1@qq.com','商家简介1',1,'2022-04-25 03:59:30',NULL),(2,'a2','123456','商家名称2','17703786902','2@qq.com','商家简介2',1,'2022-04-25 03:59:30',NULL),(3,'a3','123456','商家名称3','17703786903','3@qq.com','商家简介3',1,'2022-04-25 03:59:30',NULL),(4,'hyc','123456','财','15976542461',NULL,NULL,1,'2026-03-20 19:15:53',NULL),(5,'hycbuy','123456','财',NULL,NULL,NULL,1,'2026-03-20 19:32:01',NULL);
/*!40000 ALTER TABLE `shangjia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `token`
--

DROP TABLE IF EXISTS `token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userid` bigint(20) NOT NULL COMMENT '用户id',
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `tablename` varchar(100) DEFAULT NULL COMMENT '表名',
  `role` varchar(100) DEFAULT NULL COMMENT '角色',
  `token` varchar(200) NOT NULL COMMENT '密码',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `expiratedtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='token表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `token`
--

LOCK TABLES `token` WRITE;
/*!40000 ALTER TABLE `token` DISABLE KEYS */;
INSERT INTO `token` VALUES (1,1,'admin','users','管理员','k7u0nyq2cp3gmf0iv3zq0he7lit7lgjb','2022-04-25 05:33:20','2026-03-20 05:58:35'),(2,1,'a1','yonghu','用户','5m7uvg6vj09dspro68psqn72gkuwf5q2','2022-04-25 06:50:22','2026-03-21 13:29:36'),(3,1,'a1','shangjia','商家','cyl0bp59n46ko1pdgh5e9vi0gkn6p358','2022-04-25 07:00:14','2026-03-21 10:49:21'),(4,4,'hyc','shangjia','商家','emouvs32zz4nd6uq4bg52yc7ntly953z','2026-03-20 19:16:03','2026-03-20 20:16:04'),(5,4,'hycuser','yonghu','用户','fsyzf6trhaqylgnfj0v0lm8kuzcyuk5j','2026-03-20 19:29:11','2026-03-28 21:21:54'),(6,5,'hycbuy','shangjia','商家','zew3rw97gsokbiqjqueqgtuyse6a5a3s','2026-03-20 19:32:08','2026-03-28 21:21:18');
/*!40000 ALTER TABLE `token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `role` varchar(100) DEFAULT '管理员' COMMENT '角色',
  `register_code` varchar(100) DEFAULT NULL COMMENT '注册码',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','admin','管理员','admin','2022-04-30 16:00:00');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `yonghu`
--

DROP TABLE IF EXISTS `yonghu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `yonghu` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(200) DEFAULT NULL COMMENT '账户',
  `password` varchar(200) DEFAULT NULL COMMENT '密码',
  `pay_password` varchar(200) DEFAULT NULL COMMENT '支付密码',
  `yonghu_name` varchar(200) DEFAULT NULL COMMENT '用户姓名 Search111 ',
  `yonghu_phone` varchar(200) DEFAULT NULL COMMENT '用户手机号',
  `yonghu_id_number` varchar(200) DEFAULT NULL COMMENT '用户身份证号',
  `yonghu_photo` varchar(200) DEFAULT NULL COMMENT '用户头像',
  `sex_types` int(11) DEFAULT NULL COMMENT '性别',
  `yonghu_email` varchar(200) DEFAULT NULL COMMENT '电子邮箱',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `yonghu`
--

LOCK TABLES `yonghu` WRITE;
/*!40000 ALTER TABLE `yonghu` DISABLE KEYS */;
INSERT INTO `yonghu` VALUES (1,'a1','123456','123456','用户姓名1','17703786901','410224199610232001','http://localhost:8080/ershouchejiaoyi/upload/yonghu1.jpg',1,'1@qq.com','2022-04-25 03:59:30'),(2,'a2','123456','123456','用户姓名2','17703786902','410224199610232002','http://localhost:8080/ershouchejiaoyi/upload/yonghu2.jpg',2,'2@qq.com','2022-04-25 03:59:30'),(3,'a3','123456','123456','用户姓名3','17703786903','410224199610232003','http://localhost:8080/ershouchejiaoyi/upload/yonghu3.jpg',1,'3@qq.com','2022-04-25 03:59:30'),(4,'hycuser','123456','123456','hcy','15976542461',NULL,'/ershouchejiaoyi/upload/1774126562737.jpeg',NULL,NULL,'2026-03-20 19:29:05');
/*!40000 ALTER TABLE `yonghu` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-22  6:20:35
