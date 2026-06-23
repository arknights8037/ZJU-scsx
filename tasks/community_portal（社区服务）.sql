/*
 Navicat Premium Data Transfer

 Source Server         : mazh.neuedu.share
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 192.168.137.100:3306
 Source Schema         : community_portal

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 07/06/2023 10:03:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for charge
-- ----------------------------
DROP TABLE IF EXISTS `charge`;
CREATE TABLE `charge`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `charge_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账单编号',
  `user_id` int(10) NULL DEFAULT NULL COMMENT '用户id',
  `charge_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账单名称',
  `charge_status` int(1) NULL DEFAULT NULL COMMENT '账单状态',
  `total_price` double NULL DEFAULT NULL COMMENT '账单总价',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `charge_no`(`charge_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of charge
-- ----------------------------
INSERT INTO `charge` VALUES (1, 'C20230512092112645992833', 1, '2023-01至2023-06物业账单', 1, 600, '2023-05-12 09:22:09', '2023-05-12 11:00:08');
INSERT INTO `charge` VALUES (2, 'C20230512092359816160383', 1, '2022-06至2022-12物业账单', 0, 600, '2023-05-12 09:22:09', '2023-05-12 10:56:44');

-- ----------------------------
-- Table structure for charge_detail
-- ----------------------------
DROP TABLE IF EXISTS `charge_detail`;
CREATE TABLE `charge_detail`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `charge_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账单编号',
  `charge_content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账单内容',
  `charge_price` double NULL DEFAULT NULL COMMENT '账单价格',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `charge_chargedetail`(`charge_no`) USING BTREE,
  CONSTRAINT `charge_chargedetail` FOREIGN KEY (`charge_no`) REFERENCES `charge` (`charge_no`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of charge_detail
-- ----------------------------
INSERT INTO `charge_detail` VALUES (1, 'C20230512092112645992833', '2023-01', 100, '2023-05-12 09:22:24', NULL);
INSERT INTO `charge_detail` VALUES (2, 'C20230512092112645992833', '2023-02', 100, '2023-05-12 09:22:24', NULL);
INSERT INTO `charge_detail` VALUES (3, 'C20230512092112645992833', '2023-03', 100, '2023-05-12 09:22:24', NULL);
INSERT INTO `charge_detail` VALUES (4, 'C20230512092112645992833', '2023-04', 100, '2023-05-12 09:22:24', NULL);
INSERT INTO `charge_detail` VALUES (5, 'C20230512092112645992833', '2023-05', 100, '2023-05-12 09:22:24', NULL);
INSERT INTO `charge_detail` VALUES (6, 'C20230512092112645992833', '2023-06', 100, '2023-05-12 09:22:24', NULL);
INSERT INTO `charge_detail` VALUES (7, 'C20230512092359816160383', '2022-06', 100, '2023-05-12 09:22:24', NULL);
INSERT INTO `charge_detail` VALUES (8, 'C20230512092359816160383', '2022-07', 100, '2023-05-12 09:22:24', NULL);
INSERT INTO `charge_detail` VALUES (9, 'C20230512092359816160383', '2022-08', 100, '2023-05-12 09:22:24', NULL);
INSERT INTO `charge_detail` VALUES (10, 'C20230512092359816160383', '2022-09', 100, '2023-05-12 09:22:24', NULL);
INSERT INTO `charge_detail` VALUES (11, 'C20230512092359816160383', '2022-10', 100, '2023-05-12 09:22:24', NULL);
INSERT INTO `charge_detail` VALUES (12, 'C20230512092359816160383', '2022-11', 100, '2023-05-12 09:22:24', NULL);
INSERT INTO `charge_detail` VALUES (13, 'C20230512092359816160383', '2022-12', 100, '2023-05-12 09:25:50', NULL);

-- ----------------------------
-- Table structure for complaint
-- ----------------------------
DROP TABLE IF EXISTS `complaint`;
CREATE TABLE `complaint`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NULL DEFAULT NULL COMMENT '用户id',
  `complaint_content` varchar(4000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '投诉内容',
  `complaint_status` int(1) NULL DEFAULT NULL COMMENT '投诉状态',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of complaint
-- ----------------------------
INSERT INTO `complaint` VALUES (1, 1, '<p>测试投诉</p>', 1, '2023-05-10 10:24:11', NULL);
INSERT INTO `complaint` VALUES (2, 1, '<p><img src=\"/complaint/f233f8b889944bc1bd4b79263e774519.jpg\" alt=\"\" data-href=\"\" style=\"width: 193.00px;height: 193.00px;\"/></p>', 0, '2023-05-10 10:27:47', NULL);
INSERT INTO `complaint` VALUES (3, 1, '<p>123</p><p><img src=\"/complaint/6d1c0b7be8094edbada36b0be84e5e6a.jpg\" alt=\"\" data-href=\"\" style=\"\"/></p>', 1, '2023-05-10 10:30:28', '2023-05-12 14:44:29');

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `notice_title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '公告标题',
  `notice_content` varchar(4000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '公告内容',
  `notice_status` int(1) NOT NULL COMMENT '公告状态',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '社区停水通知', '尊敬的业主，由于本小区自来水公司进行管线维修，预计本周三（5月10日）早上9点至下午4点，将暂停供水。请业主提前做好储水准备。', 1, '2023-05-08 12:00:00', '2023-05-15 16:59:38');
INSERT INTO `notice` VALUES (2, '小区绿化维护通知', '为了美化小区环境，加强绿化管理，现对小区绿化进行维护。在维护期间，请各业主配合，不要随意破坏、践踏草坪和花坛，谢谢配合。', 1, '2023-05-06 09:00:00', '2023-05-07 18:00:00');
INSERT INTO `notice` VALUES (3, '小区门禁升级通知', '尊敬的业主，为了加强小区安保管理，现对小区门禁系统进行升级。升级期间，门禁功能将暂停使用，具体时间为本周五（5月12日）上午10点至下午2点。给业主带来的不便，敬请谅解。', 1, '2023-05-04 16:30:00', '2023-05-08 09:00:00');
INSERT INTO `notice` VALUES (4, '社区开展志愿者招募', '尊敬的业主，为了更好地服务社区，增强社区凝聚力，现面向全体业主招募志愿者。有意者请到物业管理中心报名，谢谢您的支持。', 1, '2023-05-03 14:00:00', '2023-05-03 14:00:00');
INSERT INTO `notice` VALUES (5, '小区游泳池清洁通知', '为确保小区游泳池水质清洁卫生，现对游泳池进行全面清洁。清洁期间，游泳池将暂停使用，具体时间为本周日（5月14日）上午9点至下午2点。给业主带来的不便，敬请谅解。', 1, '2023-05-02 10:00:00', '2023-05-08 15:00:00');
INSERT INTO `notice` VALUES (6, '小区垃圾分类倡议', '为了保护环境、创建美好社区，现倡议全体业主积极参与垃圾分类。请业主按照指定分类要求投放垃圾，并协助管理人员做好分类回收工作，谢谢合作。', 1, '2023-05-01 09:30:00', '2023-05-01 09:30:00');
INSERT INTO `notice` VALUES (7, '小区电梯维护通知', '为确保小区电梯正常运行，现对电梯进行全面检修和维护。维护期间，电梯将暂停使用，具体时间为本周二（5月9日）上午8点至下午5点。给业主带来的不便，敬请谅解。', 0, '2023-05-07 16:00:00', '2023-05-15 16:56:02');
INSERT INTO `notice` VALUES (8, '小区停车位管理规定', '为了更好地管理小区停车位，现制定以下规定：业主使用小区停车位须登记车牌号码，未登记者不得使用；非业主车辆禁止使用小区停车位；严禁占用消防通道和人行通道停车。请业主自觉遵守，共同维护小区秩序。', 0, '2023-05-06 10:30:00', '2023-05-15 16:58:19');
INSERT INTO `notice` VALUES (9, '测试公告', '<p>测试公告</p>', 1, '2023-05-15 17:20:58', NULL);
INSERT INTO `notice` VALUES (10, '111', '<p><img src=\"/notice/e827b2ff95424851a6c2dea0707e19bd.jpg\" alt=\"\" data-href=\"\" style=\"\"/></p>', 1, '2023-05-15 17:22:39', '2023-05-15 17:25:16');

-- ----------------------------
-- Table structure for notice_record
-- ----------------------------
DROP TABLE IF EXISTS `notice_record`;
CREATE TABLE `notice_record`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `notice_id` int(10) NOT NULL COMMENT '公告id',
  `user_id` int(10) NOT NULL COMMENT '用户id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `notice_noticerecord_idx`(`notice_id`) USING BTREE,
  INDEX `user_noticeuser__idx`(`user_id`) USING BTREE,
  CONSTRAINT `notice_noticerecord` FOREIGN KEY (`notice_id`) REFERENCES `notice` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice_record
-- ----------------------------
INSERT INTO `notice_record` VALUES (1, 1, 1, '2023-05-08 11:03:21', NULL);
INSERT INTO `notice_record` VALUES (2, 5, 1, '2023-05-08 11:18:54', NULL);
INSERT INTO `notice_record` VALUES (3, 1, 12, '2023-05-15 16:45:11', NULL);
INSERT INTO `notice_record` VALUES (4, 9, 12, '2023-05-15 17:25:01', NULL);
INSERT INTO `notice_record` VALUES (5, 10, 12, '2023-05-15 17:25:22', NULL);
INSERT INTO `notice_record` VALUES (6, 9, 1, '2023-05-16 15:29:15', NULL);
INSERT INTO `notice_record` VALUES (7, 3, 1, '2023-05-17 13:15:25', NULL);

-- ----------------------------
-- Table structure for parking
-- ----------------------------
DROP TABLE IF EXISTS `parking`;
CREATE TABLE `parking`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` int(10) NULL DEFAULT NULL COMMENT '用户id',
  `parking_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '车位名称',
  `parking_type` int(1) NULL DEFAULT NULL COMMENT '车位类型',
  `parking_status` int(1) NULL DEFAULT NULL COMMENT '车位状态',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of parking
-- ----------------------------
INSERT INTO `parking` VALUES (1, 1, '龙湖舜山府-地下车库-D011', 1, 1, '2023-05-10 16:15:25', NULL);
INSERT INTO `parking` VALUES (2, 1, '大湖山语-地上-01', 2, 1, '2023-05-10 16:15:47', NULL);
INSERT INTO `parking` VALUES (3, 1, '龙湖舜山府-地下车库-D022', 1, 1, '2023-05-10 16:28:22', NULL);

-- ----------------------------
-- Table structure for parking_bind
-- ----------------------------
DROP TABLE IF EXISTS `parking_bind`;
CREATE TABLE `parking_bind`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `parking_id` int(10) NULL DEFAULT NULL,
  `license_plate` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '车牌号',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `parking_parkingbind`(`parking_id`) USING BTREE,
  CONSTRAINT `parking_parkingbind` FOREIGN KEY (`parking_id`) REFERENCES `parking` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of parking_bind
-- ----------------------------
INSERT INTO `parking_bind` VALUES (1, 1, '辽BBA888', '2023-05-10 16:17:21', NULL);
INSERT INTO `parking_bind` VALUES (2, 1, '辽BBA999', '2023-05-10 16:24:45', NULL);
INSERT INTO `parking_bind` VALUES (5, 3, '辽BBA111', '2023-05-11 09:10:26', NULL);
INSERT INTO `parking_bind` VALUES (6, 3, '辽BBA112', '2023-05-11 09:10:31', NULL);

-- ----------------------------
-- Table structure for visitor
-- ----------------------------
DROP TABLE IF EXISTS `visitor`;
CREATE TABLE `visitor`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` int(10) NULL DEFAULT NULL COMMENT '用户id',
  `visitor_objective` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来访目的',
  `visitor_time` datetime(0) NULL DEFAULT NULL COMMENT '来访时间',
  `visitor_status` int(1) NULL DEFAULT NULL COMMENT '来访状态',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of visitor
-- ----------------------------
INSERT INTO `visitor` VALUES (1, 1, '中介看房', '2023-05-11 00:00:00', 0, '2023-05-10 16:15:25', NULL);
INSERT INTO `visitor` VALUES (2, 1, '搬家放行', '2023-05-12 00:00:00', 1, '2023-05-10 16:15:47', NULL);
INSERT INTO `visitor` VALUES (3, 1, '送货上门', '2023-05-15 00:00:00', 1, '2023-05-10 16:28:22', NULL);
INSERT INTO `visitor` VALUES (4, 1, '中介看房', '2023-05-04 00:00:00', 0, '2023-05-11 10:05:15', NULL);
INSERT INTO `visitor` VALUES (5, 1, '中介看房', '1970-01-01 08:00:00', 0, '2023-05-11 10:05:33', NULL);
INSERT INTO `visitor` VALUES (6, 1, '中介看房', '2023-05-31 00:00:00', 1, '2023-05-11 10:11:34', '2023-05-16 09:52:42');

SET FOREIGN_KEY_CHECKS = 1;
