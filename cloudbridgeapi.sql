/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80033
 Source Host           : localhost:3306
 Source Schema         : cloudbridgeapi

 Target Server Type    : MySQL
 Target Server Version : 80033
 File Encoding         : 65001

 Date: 18/12/2025 22:52:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for interface_info
-- ----------------------------
DROP TABLE IF EXISTS `interface_info`;
CREATE TABLE `interface_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接口地址',
  `requestParams` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数',
  `requestHeader` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求头',
  `responseHeader` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '响应头',
  `status` int NOT NULL DEFAULT 1 COMMENT '接口状态（0-关闭，1-开启）',
  `method` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求类型',
  `userId` bigint NOT NULL COMMENT '创建人',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
  `sdkId` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1847129235228549122 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '接口信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of interface_info
-- ----------------------------
INSERT INTO `interface_info` VALUES (38, '获取API密钥列表', '获取用户所有API密钥', '/api/v1/apikeys', '{\"userId\":\"Long\"}', '{\"Authorization\":\"Bearer token\"}', '{\"Content-Type\":\"application/json\"}', 1, 'GET', 1, '2024-09-14 10:43:08', '2024-09-14 10:43:08', 0, NULL);
INSERT INTO `interface_info` VALUES (39, '删除API密钥', '通过密钥ID删除指定API密钥', '/api/v1/apikeys/{apiKeyId}', '{\"apiKeyId\":\"Long\"}', '{\"Authorization\":\"Bearer token\"}', NULL, 1, 'DELETE', 1, '2024-09-14 10:43:08', '2024-09-14 10:43:08', 0, NULL);
INSERT INTO `interface_info` VALUES (40, '获取平台状态', '获取API平台的当前状态信息', '/api/v1/status', NULL, NULL, '{\"Content-Type\":\"application/json\"}', 1, 'GET', 1, '2024-09-14 10:43:08', '2024-09-14 10:43:08', 0, NULL);
INSERT INTO `interface_info` VALUES (1847129235228549122, 'getUsernameByPost', '获取用户名', '/api/getUsernameByPost', '{\n  \"userName\": \"String\"\n}', '{\n  \"Authorization\": \"Bearer token\"\n}\n', '{\n  \"Content-Type\": \"application/json\"\n}', 1, 'POST', 1846020498879258625, '2024-10-18 12:15:02', '2024-10-22 10:09:11', 0, 1847129235010445314);

-- ----------------------------
-- Table structure for sdk_info
-- ----------------------------
DROP TABLE IF EXISTS `sdk_info`;
CREATE TABLE `sdk_info`  (
  `id` bigint NOT NULL COMMENT 'id',
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'SDK名称',
  `path` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'SDK存储路径',
  `status` int NULL DEFAULT 0 COMMENT '状态，0启用1禁用',
  `userId` bigint NOT NULL COMMENT '创建人',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'SDK相关信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sdk_info
-- ----------------------------
INSERT INTO `sdk_info` VALUES (1847129235010445314, 'CloudBridge-Client-SDK.zip', 'D:\\Data\\SDK\\746c1906-b05b-4aae-81af-a7449b61a966.jar', 0, 1834782677128556546, '2024-10-18 12:15:02', '2024-10-21 19:00:12', 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `userAccount` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
  `userAvatar` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户头像',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别',
  `userRole` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'user' COMMENT '用户角色：user / admin',
  `userPassword` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `accessKey` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'accessKey',
  `secretKey` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'secretKey',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  `unionId` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'WeiXinOpenPlatformId',
  `mpOpenId` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'GongZhongOpenId',
  `userProfile` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户简介',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uni_userAccount`(`userAccount` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1846020498879258625 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1834782677128556546, 'admin', 'admin', NULL, NULL, 'admin', '9dcafb4c95a32b948919ee8d0e166334', 'c33cc5bc0b60e4ca1890cca7647cc98e', 'ef2c9c1ef0a4da3fe8fb90b3f2626ca3', '2024-09-14 10:34:13', '2024-10-21 16:59:48', 0, NULL, NULL, NULL);
INSERT INTO `user` VALUES (1846020498879258625, 'zhuangshuo', 'zhuangshuo', NULL, NULL, 'user', '9dcafb4c95a32b948919ee8d0e166334', '4e305ce42ca5385d91bb9694124dd135', '2d14fc3aa485a19868eddf091352fa62', '2024-10-15 10:49:19', '2024-10-21 16:59:48', 0, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for user_interface_info
-- ----------------------------
DROP TABLE IF EXISTS `user_interface_info`;
CREATE TABLE `user_interface_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userId` bigint NOT NULL COMMENT '调用用户 id',
  `interfaceInfoId` bigint NOT NULL COMMENT '接口 id',
  `totalNum` int NOT NULL DEFAULT 0 COMMENT '总调用次数',
  `leftNum` int NOT NULL DEFAULT 0 COMMENT '剩余调用次数',
  `status` int NOT NULL DEFAULT 0 COMMENT '0-正常，1-禁用',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1848208515735842818 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户调用接口关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_interface_info
-- ----------------------------
INSERT INTO `user_interface_info` VALUES (1848208515735842818, 1846020498879258625, 1847129235228549122, 9, 991, 0, '2024-10-21 11:43:42', '2024-11-16 13:05:56', 0);

SET FOREIGN_KEY_CHECKS = 1;
