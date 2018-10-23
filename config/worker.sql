/*
 Navicat Premium Data Transfer

 Source Server         : MS
 Source Server Type    : MySQL
 Source Server Version : 80012
 Source Host           : localhost:3306
 Source Schema         : worker

 Target Server Type    : MySQL
 Target Server Version : 80012
 File Encoding         : 65001

 Date: 23/10/2018 16:51:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for kline_15min
-- ----------------------------
DROP TABLE IF EXISTS `kline_15min`;
CREATE TABLE `kline_15min`  (
  `time` bigint(20) NOT NULL,
  `low` double(20, 8) NOT NULL,
  `high` double(20, 8) NOT NULL,
  `open` double(20, 8) NOT NULL,
  `close` double(20, 8) NOT NULL,
  `volume` bigint(20) NOT NULL,
  `currency_volume` double(20, 8) NOT NULL,
  PRIMARY KEY (`time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for kline_1min
-- ----------------------------
DROP TABLE IF EXISTS `kline_1min`;
CREATE TABLE `kline_1min`  (
  `time` bigint(20) NOT NULL,
  `low` double(20, 8) NOT NULL,
  `high` double(20, 8) NOT NULL,
  `open` double(20, 8) NOT NULL,
  `close` double(20, 8) NOT NULL,
  `volume` bigint(20) NOT NULL,
  `currency_volume` double(20, 8) NOT NULL,
  PRIMARY KEY (`time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for trade_history
-- ----------------------------
DROP TABLE IF EXISTS `trade_history`;
CREATE TABLE `trade_history`  (
  `trade_id` bigint(20) NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  `price` double(20, 8) NOT NULL,
  `qty` bigint(20) NOT NULL,
  `side` char(5) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`trade_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
