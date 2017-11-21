/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50624
Source Host           : localhost:3306
Source Database       : fdi

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2017-11-13 18:09:03
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_permission
-- ----------------------------

CREATE TABLE IF NOT EXISTS `t_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `nick_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_permission
-- ----------------------------
INSERT INTO `t_permission` VALUES ('1', 'FACE_PEOPLE', '人员信息');
INSERT INTO `t_permission` VALUES ('2', 'FACE_MEETING', '会议信息');
INSERT INTO `t_permission` VALUES ('3', 'DEV_EDIT', '修改');
INSERT INTO `t_permission` VALUES ('4', 'DEV_QUERY', '查询');
INSERT INTO `t_permission` VALUES ('5', 'STAT_QUERY', '查询');
INSERT INTO `t_permission` VALUES ('6', 'STAT_EXPORT', '导出');
INSERT INTO `t_permission` VALUES ('7', 'FACE_LOG_QUERY', '查询');
INSERT INTO `t_permission` VALUES ('8', 'FACE_LOG_EXPORT', '导出');
INSERT INTO `t_permission` VALUES ('9', 'MG_ROLE_QUERY', '查询');
INSERT INTO `t_permission` VALUES ('10', 'MG_ROLE_EDIT', '修改');
