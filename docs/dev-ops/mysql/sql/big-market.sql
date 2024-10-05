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

 Date: 05/10/2024 16:53:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE database if NOT EXISTS `big-market` default character set utf8mb4;
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
INSERT INTO `award` VALUES (1, 101, 'user_point_random', '200,400', 'user point', '2024-07-08 21:05:15', '2024-10-05 16:52:55');
INSERT INTO `award` VALUES (2, 102, 'voucher', '5', 'voucher', '2024-07-08 21:06:04', '2024-10-05 14:52:31');
INSERT INTO `award` VALUES (3, 103, 'voucher', '10', 'voucher', '2024-07-08 21:07:10', '2024-10-05 14:52:32');
INSERT INTO `award` VALUES (4, 104, 'voucher', '20', 'voucher', '2024-07-08 21:07:27', '2024-10-05 14:52:33');
INSERT INTO `award` VALUES (5, 105, 'epic_golden_beans', '100', 'epic golden beans', '2024-07-08 21:08:10', '2024-10-05 14:54:02');
INSERT INTO `award` VALUES (6, 106, 'epic_golden_beans', '500', 'epic golden beans', '2024-07-14 16:15:09', '2024-10-05 14:53:53');
INSERT INTO `award` VALUES (7, 107, 'epic_golden_beans', '1000', 'epic golden beans', '2024-07-14 16:15:28', '2024-10-05 14:53:52');
INSERT INTO `award` VALUES (8, 108, 'ultimate_reward', '', 'ultimate reward', '2024-07-31 21:16:52', '2024-10-05 15:23:48');

-- ----------------------------
-- Table structure for daily_behavior_rebate
-- ----------------------------
DROP TABLE IF EXISTS `daily_behavior_rebate`;
CREATE TABLE `daily_behavior_rebate`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `behavior_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '行为类型（sign 签到、openai_pay 支付）',
  `rebate_desc` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '返利描述',
  `rebate_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '返利类型（sku 活动库存充值商品、integral 用户活动积分）',
  `rebate_config` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '返利配置',
  `status` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（open 开启、close 关闭）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_behavior_type`(`behavior_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '日常行为返利活动配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of daily_behavior_rebate
-- ----------------------------
INSERT INTO `daily_behavior_rebate` VALUES (1, 'sign', '签到返利-sku额度', 'sku', '9011', 'open', '2024-04-30 09:32:46', '2024-04-30 18:05:23');
INSERT INTO `daily_behavior_rebate` VALUES (2, 'sign', '签到返利-积分', 'point', '30', 'open', '2024-04-30 09:32:46', '2024-09-28 22:28:21');

-- ----------------------------
-- Table structure for raffle_activity
-- ----------------------------
DROP TABLE IF EXISTS `raffle_activity`;
CREATE TABLE `raffle_activity`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `activity_id` bigint(12) NOT NULL COMMENT '活动ID',
  `activity_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '活动名称',
  `activity_desc` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '活动描述',
  `begin_date_time` datetime NOT NULL COMMENT '开始时间',
  `end_date_time` datetime NOT NULL COMMENT '结束时间',
  `strategy_id` bigint(8) NOT NULL COMMENT '抽奖策略ID',
  `status` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'create' COMMENT '活动状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_activity_id`(`activity_id`) USING BTREE,
  INDEX `idx_begin_date_time`(`begin_date_time`) USING BTREE,
  INDEX `idx_end_date_time`(`end_date_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '抽奖活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of raffle_activity
-- ----------------------------
INSERT INTO `raffle_activity` VALUES (1, 100301, 'test', 'test', '2024-08-01 10:15:10', '2034-09-30 10:15:10', 10004, 'open', '2024-03-09 10:15:10', '2024-09-04 21:05:04');

-- ----------------------------
-- Table structure for raffle_activity_amount
-- ----------------------------
DROP TABLE IF EXISTS `raffle_activity_amount`;
CREATE TABLE `raffle_activity_amount`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'auto increasing id',
  `activity_amount_id` bigint(12) NOT NULL COMMENT 'id for times of attending activity',
  `total_amount` int(8) NOT NULL COMMENT 'total count',
  `day_amount` int(8) NOT NULL COMMENT 'day count',
  `month_amount` int(8) NOT NULL COMMENT 'month count',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_activity_count_id`(`activity_amount_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'raffle activity count table' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of raffle_activity_amount
-- ----------------------------
INSERT INTO `raffle_activity_amount` VALUES (1, 11101, 10, 10, 10, '2024-08-29 19:14:30', '2024-09-29 14:54:10');
INSERT INTO `raffle_activity_amount` VALUES (2, 11102, 30, 30, 30, '2024-09-29 14:54:00', '2024-09-29 14:54:00');
INSERT INTO `raffle_activity_amount` VALUES (3, 11103, 1, 1, 1, '2024-09-29 15:17:36', '2024-09-29 15:17:36');
INSERT INTO `raffle_activity_amount` VALUES (4, 11104, 5, 5, 5, '2024-09-29 15:17:42', '2024-09-29 15:17:42');

-- ----------------------------
-- Table structure for raffle_activity_sku
-- ----------------------------
DROP TABLE IF EXISTS `raffle_activity_sku`;
CREATE TABLE `raffle_activity_sku`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `sku` bigint(12) NOT NULL COMMENT '商品sku - 把每一个组合当做一个商品',
  `activity_id` bigint(12) NOT NULL COMMENT '活动ID',
  `activity_amount_id` bigint(12) NOT NULL COMMENT '活动个人参与次数ID',
  `stock_amount` int(11) NOT NULL COMMENT '商品库存',
  `stock_remain` int(11) NOT NULL COMMENT '剩余库存',
  `point_amount` decimal(11, 0) NOT NULL COMMENT '兑换所需积分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_sku`(`sku`) USING BTREE,
  INDEX `idx_activity_id_activity_amount_id`(`activity_id`, `activity_amount_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of raffle_activity_sku
-- ----------------------------
INSERT INTO `raffle_activity_sku` VALUES (1, 9011, 100301, 11101, 50000, 50000, 100, '2024-03-16 11:41:09', '2024-10-05 15:12:52');
INSERT INTO `raffle_activity_sku` VALUES (3, 9012, 100301, 11102, 50000, 50000, 300, '2024-09-29 14:52:55', '2024-10-02 20:22:35');
INSERT INTO `raffle_activity_sku` VALUES (4, 9013, 100301, 11103, 50000, 50000, 10, '2024-09-29 15:13:14', '2024-10-05 15:12:55');
INSERT INTO `raffle_activity_sku` VALUES (5, 9014, 100301, 11104, 50000, 50000, 50, '2024-09-29 15:13:37', '2024-10-02 20:22:44');

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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rule_tree
-- ----------------------------
INSERT INTO `rule_tree` VALUES (1, 'tree_01', 'rule tree 01', 'rule tree', 'rule_lock', '2024-08-06 22:25:48', '2024-09-12 22:00:28');
INSERT INTO `rule_tree` VALUES (2, 'tree_02', 'rule tree 02', 'rule tree', 'rule_lock', '2024-08-06 22:25:48', '2024-09-12 22:00:42');
INSERT INTO `rule_tree` VALUES (3, 'tree_03', 'rule tree 03', 'rule tree', 'rule_lock', '2024-08-06 22:25:48', '2024-09-12 21:27:12');

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
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rule_tree_node
-- ----------------------------
INSERT INTO `rule_tree_node` VALUES (1, 'tree_01', 'rule_lock', 'unlock after N user raffle times', '0', '2024-08-06 22:27:18', '2024-09-16 23:16:39');
INSERT INTO `rule_tree_node` VALUES (2, 'tree_01', 'rule_lucky', 'lucky award', '101:100,250', '2024-08-06 22:27:50', '2024-10-05 15:13:57');
INSERT INTO `rule_tree_node` VALUES (3, 'tree_01', 'rule_stock', 'stock control rule', NULL, '2024-08-06 22:28:36', '2024-09-12 21:23:01');
INSERT INTO `rule_tree_node` VALUES (4, 'tree_02', 'rule_lock', 'unlock after N user raffle times', '5', '2024-01-27 10:03:09', '2024-09-16 23:16:53');
INSERT INTO `rule_tree_node` VALUES (5, 'tree_02', 'rule_lucky', 'lucky award', '101:1,100', '2024-01-27 10:03:09', '2024-09-12 21:59:16');
INSERT INTO `rule_tree_node` VALUES (6, 'tree_02', 'rule_stock', 'stock control rule', NULL, '2024-01-27 10:04:43', '2024-09-12 21:59:20');
INSERT INTO `rule_tree_node` VALUES (7, 'tree_03', 'rule_lock', 'unlock after N user raffle times', '10', '2024-01-27 10:03:09', '2024-09-16 23:16:55');
INSERT INTO `rule_tree_node` VALUES (8, 'tree_03', 'rule_lucky', 'lucky award', '101:1,100', '2024-01-27 10:03:09', '2024-09-12 21:59:49');
INSERT INTO `rule_tree_node` VALUES (9, 'tree_03', 'rule_stock', 'stock control rule', NULL, '2024-01-27 10:04:43', '2024-09-12 21:59:49');

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
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rule_tree_node_line
-- ----------------------------
INSERT INTO `rule_tree_node_line` VALUES (1, 'tree_01', 'rule_lock', 'rule_stock', 'EQUAL', 'ALLOW', '2024-08-06 22:29:33', '2024-09-12 21:22:56');
INSERT INTO `rule_tree_node_line` VALUES (2, 'tree_01', 'rule_lock', 'rule_lucky', 'EQUAL', 'TAKE_OVER', '2024-08-06 22:30:15', '2024-09-12 21:22:56');
INSERT INTO `rule_tree_node_line` VALUES (3, 'tree_01', 'rule_stock', 'rule_lucky', 'EQUAL', 'ALLOW', '2024-08-06 22:30:54', '2024-09-12 21:22:57');
INSERT INTO `rule_tree_node_line` VALUES (4, 'tree_01', 'rule_stock', '', 'EQUAL', 'TAKE_OVER', '2024-08-15 22:40:49', '2024-09-12 21:22:59');
INSERT INTO `rule_tree_node_line` VALUES (5, 'tree_02', 'rule_lock', 'rule_stock', 'EQUAL', 'ALLOW', '2024-08-06 22:29:33', '2024-08-06 22:29:33');
INSERT INTO `rule_tree_node_line` VALUES (6, 'tree_02', 'rule_lock', 'rule_lucky', 'EQUAL', 'TAKE_OVER', '2024-08-06 22:30:15', '2024-08-06 22:30:15');
INSERT INTO `rule_tree_node_line` VALUES (7, 'tree_02', 'rule_stock', 'rule_lucky', 'EQUAL', 'ALLOW', '2024-08-06 22:30:54', '2024-08-15 22:23:42');
INSERT INTO `rule_tree_node_line` VALUES (8, 'tree_02', 'rule_stock', '', 'EQUAL', 'TAKE_OVER', '2024-08-15 22:40:49', '2024-08-15 22:40:49');
INSERT INTO `rule_tree_node_line` VALUES (9, 'tree_03', 'rule_lock', 'rule_stock', 'EQUAL', 'ALLOW', '2024-08-06 22:29:33', '2024-08-06 22:29:33');
INSERT INTO `rule_tree_node_line` VALUES (10, 'tree_03', 'rule_lock', 'rule_lucky', 'EQUAL', 'TAKE_OVER', '2024-08-06 22:30:15', '2024-08-06 22:30:15');
INSERT INTO `rule_tree_node_line` VALUES (11, 'tree_03', 'rule_stock', 'rule_lucky', 'EQUAL', 'ALLOW', '2024-08-06 22:30:54', '2024-08-15 22:23:42');
INSERT INTO `rule_tree_node_line` VALUES (12, 'tree_03', 'rule_stock', '', 'EQUAL', 'TAKE_OVER', '2024-08-15 22:40:49', '2024-08-15 22:40:49');

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
INSERT INTO `strategy_award` VALUES (12, 10004, 105, '100 epic golden beans', NULL, 10000, 10000, 0.0050, 4, '2024-08-08 16:17:44', '2024-10-05 14:51:24', 'tree_02');
INSERT INTO `strategy_award` VALUES (13, 10004, 106, '500 epic golden beans', NULL, 10000, 10000, 0.0040, 5, '2024-08-08 16:18:31', '2024-10-05 14:51:27', 'tree_02');
INSERT INTO `strategy_award` VALUES (14, 10004, 101, 'random point', NULL, 80000, 80000, 0.8000, 0, '2024-07-03 21:04:54', '2024-10-05 13:03:52', 'tree_01');
INSERT INTO `strategy_award` VALUES (15, 10004, 102, '$5 voucher', NULL, 50000, 50000, 0.1000, 1, '2024-07-03 21:06:05', '2024-10-05 14:57:59', 'tree_01');
INSERT INTO `strategy_award` VALUES (16, 10004, 103, '$10 voucher', NULL, 20000, 20000, 0.0500, 2, '2024-07-03 21:06:35', '2024-10-05 14:45:18', 'tree_01');
INSERT INTO `strategy_award` VALUES (17, 10004, 104, '$20 voucher', NULL, 20000, 20000, 0.0400, 3, '2024-07-03 21:07:22', '2024-10-05 14:45:22', 'tree_01');
INSERT INTO `strategy_award` VALUES (18, 10004, 107, '1000 epic golden beans', NULL, 10000, 10000, 0.0009, 6, '2024-07-14 16:12:39', '2024-10-05 14:51:28', 'tree_03');
INSERT INTO `strategy_award` VALUES (19, 10004, 108, 'ultimate reward', NULL, 10000, 10000, 0.0001, 7, '2024-08-23 22:12:41', '2024-10-05 15:23:56', 'tree_03');

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
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

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
INSERT INTO `strategy_rule` VALUES (15, 10004, NULL, 1, 'rule_weight', '5:101,102,103,104,105 10:102,103,104,105,106 15:104,105,106,107,108', 'base on raffle times to assmble award', '2024-08-08 17:07:34', '2024-10-05 15:24:02');
INSERT INTO `strategy_rule` VALUES (16, 10004, NULL, 1, 'rule_blacklist', '101:user001,user002,user003', 'blacklist, only draw award id = 100', '2024-08-08 17:08:13', '2024-09-24 23:15:33');
INSERT INTO `strategy_rule` VALUES (17, 10001, 105, 2, 'rule_lucky', '1,40', 'lucky award, random point 1:50', '2024-08-14 21:12:19', '2024-08-23 19:41:27');
INSERT INTO `strategy_rule` VALUES (18, 10001, 106, 2, 'rule_lucky', '1,50', 'lucky award, random point 1:60', '2024-08-14 21:15:44', '2024-08-23 19:41:30');
INSERT INTO `strategy_rule` VALUES (19, 10001, 107, 2, 'rule_lucky', '1,60', 'lucky award, random point 1:70', '2024-08-14 21:16:13', '2024-08-23 19:41:33');
INSERT INTO `strategy_rule` VALUES (20, 10001, 101, 2, 'rule_lucky', '1,10', 'lucky award, random point 1:10', '2024-08-14 21:17:03', '2024-08-14 21:17:06');
INSERT INTO `strategy_rule` VALUES (21, 10001, 102, 2, 'rule_lucky', '1,20', 'lucky award, random point 1:20', '2024-08-14 21:17:23', '2024-08-14 21:17:23');
INSERT INTO `strategy_rule` VALUES (22, 10001, 103, 2, 'rule_lucky', '1,30', 'lucky award, random point 1:30', '2024-08-14 21:17:35', '2024-08-14 21:17:39');
INSERT INTO `strategy_rule` VALUES (23, 10001, 104, 2, 'rule_lucky', '1,40', 'lucky award, random point 1:40', '2024-08-14 21:17:56', '2024-08-14 21:17:56');

SET FOREIGN_KEY_CHECKS = 1;
