/*
 Navicat Premium Data Transfer

 Source Server         : localMySQL
 Source Server Type    : MySQL
 Source Server Version : 50744
 Source Host           : localhost:3306
 Source Schema         : big-market

 Target Server Type    : MySQL
 Target Server Version : 50744
 File Encoding         : 65001

 Date: 27/08/2024 21:15:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE database if NOT EXISTS `big-market`
    default character set utf8mb4
    collate utf8mb4_general_ci;

use `big-market`;

-- ----------------------------
-- Table structure for award
-- ----------------------------
DROP TABLE IF EXISTS `award`;
CREATE TABLE `award`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'auto increasing id',
  `award_id` int(11) NOT NULL COMMENT 'award id',
  `award_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'award key',
  `award_config` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'award config',
  `award_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'award describe',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of award
-- ----------------------------
INSERT INTO `award` VALUES (1, 101, 'user_point_random', '1,100', 'user point', '2024-07-08 21:05:15', '2024-07-08 21:05:15');
INSERT INTO `award` VALUES (2, 102, 'openai_use_amount', '5', 'OpenAi using amount', '2024-07-08 21:06:04', '2024-07-08 21:06:04');
INSERT INTO `award` VALUES (3, 103, 'openai_use_amount', '10', 'OpenAi using amount', '2024-07-08 21:07:10', '2024-07-08 21:07:10');
INSERT INTO `award` VALUES (4, 104, 'openai_use_amount', '20', 'OpenAi using amount', '2024-07-08 21:07:27', '2024-07-08 21:07:27');
INSERT INTO `award` VALUES (5, 105, 'openai_model', 'gpt-3.5', 'OpenAi model', '2024-07-08 21:08:10', '2024-07-14 16:14:53');
INSERT INTO `award` VALUES (6, 106, 'openai_model', 'gpt-4', 'OpenAi model', '2024-07-14 16:15:09', '2024-07-14 16:15:09');
INSERT INTO `award` VALUES (7, 107, 'openai_model', 'gpt-4O', 'OpenAi model', '2024-07-14 16:15:28', '2024-07-14 16:15:28');
INSERT INTO `award` VALUES (8, 100, 'user_point_random_blacklist', '1,5', 'user point particularly for blacklist user', '2024-07-31 21:16:52', '2024-07-31 21:16:52');

-- ----------------------------
-- Table structure for rule_tree
-- ----------------------------
DROP TABLE IF EXISTS `rule_tree`;
CREATE TABLE `rule_tree`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tree_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `tree_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `tree_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `tree_root_node_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rule_tree
-- ----------------------------
INSERT INTO `rule_tree` VALUES (1, 'tree_lock', 'rule tree', 'rule tree', 'rule_lock', '2024-08-06 22:25:48', '2024-08-06 22:25:48');

-- ----------------------------
-- Table structure for rule_tree_node
-- ----------------------------
DROP TABLE IF EXISTS `rule_tree_node`;
CREATE TABLE `rule_tree_node`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `tree_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `rule_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `rule_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `rule_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rule_tree_node
-- ----------------------------
INSERT INTO `rule_tree_node` VALUES (1, 'tree_lock', 'rule_lock', 'unlock after N user raffle times', '5', '2024-08-06 22:27:18', '2024-08-23 19:55:56');
INSERT INTO `rule_tree_node` VALUES (2, 'tree_lock', 'rule_lucky', 'lucky award', '101:1,100', '2024-08-06 22:27:50', '2024-08-14 21:21:58');
INSERT INTO `rule_tree_node` VALUES (3, 'tree_lock', 'rule_stock', 'stock control rule', NULL, '2024-08-06 22:28:36', '2024-08-06 22:28:36');

-- ----------------------------
-- Table structure for rule_tree_node_line
-- ----------------------------
DROP TABLE IF EXISTS `rule_tree_node_line`;
CREATE TABLE `rule_tree_node_line`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `tree_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `rule_node_from` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `rule_node_to` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `rule_limit_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `rule_limit_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rule_tree_node_line
-- ----------------------------
INSERT INTO `rule_tree_node_line` VALUES (1, 'tree_lock', 'rule_lock', 'rule_stock', 'EQUAL', 'ALLOW', '2024-08-06 22:29:33', '2024-08-06 22:29:33');
INSERT INTO `rule_tree_node_line` VALUES (2, 'tree_lock', 'rule_lock', 'rule_lucky', 'EQUAL', 'TAKE_OVER', '2024-08-06 22:30:15', '2024-08-06 22:30:15');
INSERT INTO `rule_tree_node_line` VALUES (3, 'tree_lock', 'rule_stock', 'rule_lucky', 'EQUAL', 'ALLOW', '2024-08-06 22:30:54', '2024-08-15 22:23:42');
INSERT INTO `rule_tree_node_line` VALUES (4, 'tree_lock', 'rule_stock', '', 'EQUAL', 'TAKE_OVER', '2024-08-15 22:40:49', '2024-08-15 22:40:49');

-- ----------------------------
-- Table structure for strategy
-- ----------------------------
DROP TABLE IF EXISTS `strategy`;
CREATE TABLE `strategy`  (
  `strategy_id` int(8) NOT NULL COMMENT 'raffle strategy',
  `id` int(255) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'auto increase id',
  `strategy_desc` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'describe of raffle strategy',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
  `rule_models` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'rule models',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of strategy
-- ----------------------------
INSERT INTO `strategy` VALUES (10001, 1, 'raffle strategy', '2024-07-14 14:44:50', '2024-08-02 22:36:04', 'rule_blacklist,rule_weight');
INSERT INTO `strategy` VALUES (10002, 2, 'test_rule_raffleTimesLock', '2024-08-01 22:44:54', '2024-08-01 22:55:01', '');
INSERT INTO `strategy` VALUES (10003, 3, 'test_raffle_rate_sum_not_equal_to_1', '2024-08-02 19:27:56', '2024-08-02 19:27:56', NULL);
INSERT INTO `strategy` VALUES (10004, 4, 'test_rule_tree', '2024-08-08 16:19:29', '2024-08-08 17:04:52', 'rule_blacklist,rule_weight');

-- ----------------------------
-- Table structure for strategy_award
-- ----------------------------
DROP TABLE IF EXISTS `strategy_award`;
CREATE TABLE `strategy_award`  (
  `id` int(255) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'auto increase key',
  `strategy_id` int(8) NOT NULL COMMENT 'raffle strategy id',
  `award_id` int(11) NOT NULL COMMENT 'award id',
  `award_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'award title',
  `award_subtitle` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'award subtitle',
  `award_amount` int(11) NOT NULL COMMENT 'award amount',
  `award_remain` int(11) NOT NULL COMMENT 'award remain amount',
  `award_rate` decimal(64, 4) NOT NULL COMMENT 'award rate',
  `sort` int(11) NOT NULL COMMENT 'sort',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
  `rule_model` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'rule model',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of strategy_award
-- ----------------------------
INSERT INTO `strategy_award` VALUES (1, 10001, 101, 'random point', NULL, 80000, 80000, 0.8000, 1, '2024-07-03 21:04:54', '2024-08-22 22:07:02', 'rule_random');
INSERT INTO `strategy_award` VALUES (2, 10001, 102, '5 times use', NULL, 10000, 10000, 0.1000, 2, '2024-07-03 21:06:05', '2024-08-01 20:17:19', 'rule_lucky');
INSERT INTO `strategy_award` VALUES (3, 10001, 103, '10 times use', NULL, 5000, 5000, 0.0500, 3, '2024-07-03 21:06:35', '2024-08-01 20:17:20', 'rule_lucky');
INSERT INTO `strategy_award` VALUES (4, 10001, 104, '20 times use', NULL, 4000, 4000, 0.0400, 4, '2024-07-03 21:07:22', '2024-08-01 20:17:21', 'rule_lucky');
INSERT INTO `strategy_award` VALUES (5, 10001, 105, 'gpt-3.5 using chance', NULL, 600, 600, 0.0050, 5, '2024-07-03 21:08:22', '2024-08-14 21:09:35', 'rule_lock,rule_lucky');
INSERT INTO `strategy_award` VALUES (6, 10001, 106, 'gpt-4 using chance', NULL, 600, 600, 0.0040, 6, '2024-07-14 16:11:45', '2024-08-23 19:29:52', 'rule_lock,rule_lucky');
INSERT INTO `strategy_award` VALUES (7, 10001, 107, 'gpt-4o using chance', NULL, 400, 400, 0.0010, 7, '2024-07-14 16:12:39', '2024-08-14 21:09:42', 'rule_lock,rule_lucky');
INSERT INTO `strategy_award` VALUES (8, 10002, 105, 'test', NULL, 400, 400, 0.5000, 8, '2024-08-01 22:42:44', '2024-08-01 22:42:52', 'rule_lock');
INSERT INTO `strategy_award` VALUES (9, 10002, 106, 'test', NULL, 400, 400, 0.5000, 9, '2024-08-01 22:44:10', '2024-08-01 22:44:10', 'rule_lock');
INSERT INTO `strategy_award` VALUES (10, 10003, 105, 'test', NULL, 400, 400, 0.5500, 10, '2024-08-02 19:28:43', '2024-08-02 19:28:43', 'rule_lock');
INSERT INTO `strategy_award` VALUES (11, 10003, 106, 'test', NULL, 400, 400, 0.1000, 11, '2024-08-02 19:29:10', '2024-08-02 19:29:10', 'rule_lock');
INSERT INTO `strategy_award` VALUES (12, 10004, 105, 'gpt-3.5 using chance', NULL, 400, 400, 0.0050, 4, '2024-08-08 16:17:44', '2024-08-23 22:44:35', 'tree_lock');
INSERT INTO `strategy_award` VALUES (13, 10004, 106, 'gpt-4 using chance', NULL, 400, 400, 0.0040, 5, '2024-08-08 16:18:31', '2024-08-23 22:44:36', 'tree_lock');
INSERT INTO `strategy_award` VALUES (14, 10004, 101, 'random point', NULL, 80000, 80000, 0.8000, 0, '2024-07-03 21:04:54', '2024-08-27 19:57:13', 'tree_lock');
INSERT INTO `strategy_award` VALUES (15, 10004, 102, '5 times use', NULL, 10000, 10000, 0.1000, 1, '2024-07-03 21:06:05', '2024-08-23 23:09:40', 'tree_lock');
INSERT INTO `strategy_award` VALUES (16, 10004, 103, '10 times use', NULL, 5000, 5000, 0.0500, 2, '2024-07-03 21:06:35', '2024-08-23 22:44:18', 'tree_lock');
INSERT INTO `strategy_award` VALUES (17, 10004, 104, '20 times use', NULL, 4000, 4000, 0.0400, 3, '2024-07-03 21:07:22', '2024-08-26 22:33:19', 'tree_lock');
INSERT INTO `strategy_award` VALUES (18, 10004, 107, 'gpt-4o using chance', NULL, 400, 400, 0.0009, 6, '2024-07-14 16:12:39', '2024-08-23 22:44:39', 'tree_lock');
INSERT INTO `strategy_award` VALUES (19, 10004, 100, 'special award', NULL, 400, 400, 0.0001, 7, '2024-08-23 22:12:41', '2024-08-23 22:44:51', 'tree_lock');

-- ----------------------------
-- Table structure for strategy_rule
-- ----------------------------
DROP TABLE IF EXISTS `strategy_rule`;
CREATE TABLE `strategy_rule`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'auto increase id',
  `strategy_id` int(8) NOT NULL COMMENT 'raflfe strategy id',
  `award_id` int(11) NULL DEFAULT NULL COMMENT 'award id',
  `rule_type` int(11) NOT NULL COMMENT 'rule type(1. raffle rule 2. award rule)',
  `rule_model` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'rule type (rule_lock)',
  `rule_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'proportion of rule',
  `rule_desc` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'rule describe',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of strategy_rule
-- ----------------------------
INSERT INTO `strategy_rule` VALUES (1, 10001, 101, 2, 'rule_random', '1,1000', 'random point strategy', '2024-07-03 22:02:20', '2024-07-03 22:02:20');
INSERT INTO `strategy_rule` VALUES (2, 10001, 105, 2, 'rule_lock', '1', 'unlock after 1 raffle', '2024-07-03 22:07:10', '2024-08-14 21:11:31');
INSERT INTO `strategy_rule` VALUES (4, 10001, NULL, 1, 'rule_weight', '4000:102,103,104,105 5000:102,103,104,105,106 6000:102,103,104,105,106,107', 'base on raffle times to assmble award', '2024-07-03 22:17:22', '2024-08-08 17:07:40');
INSERT INTO `strategy_rule` VALUES (5, 10001, NULL, 1, 'rule_blacklist', '101:user001,user002,user003', 'blacklist, only draw award id = 100', '2024-07-03 22:18:21', '2024-08-22 22:07:11');
INSERT INTO `strategy_rule` VALUES (6, 10001, 106, 2, 'rule_lock', '2', 'unlock after 2 raffle', '2024-07-29 21:16:41', '2024-08-14 21:11:33');
INSERT INTO `strategy_rule` VALUES (7, 10001, 107, 2, 'rule_lock', '6', 'unlock after 6 raffle', '2024-07-29 21:18:09', '2024-08-14 21:11:36');
INSERT INTO `strategy_rule` VALUES (9, 10002, 105, 2, 'rule_lock', '10', 'unlock after 10 raffle', '2024-08-01 22:41:22', '2024-08-01 22:41:22');
INSERT INTO `strategy_rule` VALUES (10, 10002, 106, 2, 'rule_lock', '20', 'unlock after 20 raffle', '2024-08-01 22:42:14', '2024-08-01 22:42:14');
INSERT INTO `strategy_rule` VALUES (11, 10003, 105, 2, 'rule_lock', '10', 'unlock after 10 raffle', '2024-08-02 19:29:47', '2024-08-02 19:29:47');
INSERT INTO `strategy_rule` VALUES (12, 10003, 106, 2, 'rule_lock', '20', 'unlock after 20 raffle', '2024-08-02 19:30:08', '2024-08-02 19:30:08');
INSERT INTO `strategy_rule` VALUES (13, 10004, 105, 2, 'tree_lock', '10', 'unlock after 10 raffle', '2024-08-08 16:46:24', '2024-08-08 16:46:24');
INSERT INTO `strategy_rule` VALUES (14, 10004, 106, 2, 'tree_lock', '20', 'unlock after 20 raffle', '2024-08-08 16:46:46', '2024-08-08 16:46:46');
INSERT INTO `strategy_rule` VALUES (15, 10004, NULL, 1, 'rule_weight', '4000:102,103,104,105 5000:102,103,104,105,106 6000:102,103,104,105,106,107', 'base on raffle times to assmble award', '2024-08-08 17:07:34', '2024-08-08 17:07:34');
INSERT INTO `strategy_rule` VALUES (16, 10004, NULL, 1, 'rule_blacklist', '101:user001,user002,user003', 'blacklist, only draw award id = 100', '2024-08-08 17:08:13', '2024-08-22 22:07:14');
INSERT INTO `strategy_rule` VALUES (17, 10001, 105, 2, 'rule_lucky', '1,40', 'lucky award, random point 1:50', '2024-08-14 21:12:19', '2024-08-23 19:41:27');
INSERT INTO `strategy_rule` VALUES (18, 10001, 106, 2, 'rule_lucky', '1,50', 'lucky award, random point 1:60', '2024-08-14 21:15:44', '2024-08-23 19:41:30');
INSERT INTO `strategy_rule` VALUES (19, 10001, 107, 2, 'rule_lucky', '1,60', 'lucky award, random point 1:70', '2024-08-14 21:16:13', '2024-08-23 19:41:33');
INSERT INTO `strategy_rule` VALUES (20, 10001, 101, 2, 'rule_lucky', '1,10', 'lucky award, random point 1:10', '2024-08-14 21:17:03', '2024-08-14 21:17:06');
INSERT INTO `strategy_rule` VALUES (21, 10001, 102, 2, 'rule_lucky', '1,20', 'lucky award, random point 1:20', '2024-08-14 21:17:23', '2024-08-14 21:17:23');
INSERT INTO `strategy_rule` VALUES (22, 10001, 103, 2, 'rule_lucky', '1,30', 'lucky award, random point 1:30', '2024-08-14 21:17:35', '2024-08-14 21:17:39');
INSERT INTO `strategy_rule` VALUES (23, 10001, 104, 2, 'rule_lucky', '1,40', 'lucky award, random point 1:40', '2024-08-14 21:17:56', '2024-08-14 21:17:56');
INSERT INTO `strategy_rule` VALUES (24, 10004, 107, 2, 'rule_lucky', '1,70', 'lucky award, random point 1:70', '2024-08-14 21:16:13', '2024-08-14 21:16:13');
INSERT INTO `strategy_rule` VALUES (25, 10004, 101, 2, 'rule_lucky', '1,10', 'lucky award, random point 1:10', '2024-08-14 21:17:03', '2024-08-23 19:42:00');
INSERT INTO `strategy_rule` VALUES (26, 10004, 102, 2, 'rule_lucky', '1,10', 'lucky award, random point 1:20', '2024-08-14 21:17:23', '2024-08-23 19:41:18');
INSERT INTO `strategy_rule` VALUES (27, 10004, 103, 2, 'rule_lucky', '1,20', 'lucky award, random point 1:30', '2024-08-14 21:17:35', '2024-08-23 19:41:20');
INSERT INTO `strategy_rule` VALUES (28, 10004, 104, 2, 'rule_lucky', '1,30', 'lucky award, random point 1:40', '2024-08-14 21:17:56', '2024-08-23 19:41:23');
INSERT INTO `strategy_rule` VALUES (29, 10004, 105, 2, 'rule_lucky', '1,40', 'lucky award, random point 1:70', '2024-08-14 21:16:13', '2024-08-23 19:43:48');
INSERT INTO `strategy_rule` VALUES (30, 10004, 106, 2, 'rule_lucky', '1,50', 'lucky award, random point 1:10', '2024-08-14 21:17:03', '2024-08-23 19:43:51');
INSERT INTO `strategy_rule` VALUES (31, 10004, 107, 2, 'rule_lock', '30', 'unlock after 30 raffle', '2024-08-14 21:16:13', '2024-08-23 19:44:37');

SET FOREIGN_KEY_CHECKS = 1;
