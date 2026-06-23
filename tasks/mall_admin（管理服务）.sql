/*
 Navicat Premium Data Transfer

 Source Server         : mazh.neuedu.share
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 192.168.137.100:3306
 Source Schema         : mall_admin

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 07/06/2023 10:03:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `menu_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `parent_id` int(11) NULL DEFAULT NULL,
  `menu_component` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `menu_icon` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `menu_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `menu_state` int(1) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES (1, '首页', 0, '/index', 'HomeFilled', '/index', 1, '2023-04-14 14:16:22', NULL);
INSERT INTO `menu` VALUES (2, '权限管理', 0, '/auth/role/index', 'Lock', '/auth', 1, '2023-04-14 14:17:04', NULL);
INSERT INTO `menu` VALUES (3, '角色管理', 2, '/auth/role/index', 'Handbag', '/auth/role', 1, '2023-04-14 14:18:02', NULL);
INSERT INTO `menu` VALUES (4, '用户管理', 2, '/auth/user/index', 'Avatar', '/auth/user', 1, '2023-04-17 13:11:06', NULL);
INSERT INTO `menu` VALUES (5, '商品管理', 0, '/goods/category/index', 'Goods', '/goods/category', 1, '2023-04-17 13:12:46', NULL);
INSERT INTO `menu` VALUES (6, '类别管理', 5, '/goods/category/index', 'Tickets', '/goods/category', 1, '2023-04-17 13:13:42', NULL);
INSERT INTO `menu` VALUES (7, '商品管理', 5, '/goods/index', 'Goods', '/goods/index', 1, '2023-04-20 13:56:08', NULL);
INSERT INTO `menu` VALUES (8, '区域管理', 0, '/area/index', 'Location', '/area/index', 1, '2023-04-21 16:17:02', NULL);
INSERT INTO `menu` VALUES (9, '区域维护', 8, '/area/index', 'Location', '/area/index', 1, '2023-04-21 16:17:48', NULL);
INSERT INTO `menu` VALUES (10, '订单管理', 0, '/order/index', 'Memo', '/order/index', 1, '2023-04-21 16:18:18', NULL);
INSERT INTO `menu` VALUES (11, '订单维护', 10, '/order/index', 'Memo', '/order/index', 1, '2023-04-21 16:18:40', NULL);
INSERT INTO `menu` VALUES (12, '门店管理', 8, '/area/store/index', 'Shop', '/area/store/index', 1, '2023-04-23 14:10:04', NULL);
INSERT INTO `menu` VALUES (13, '营销管理', 0, '/special/index', 'Sell', '/special/index', 1, '2023-04-27 14:39:01', NULL);
INSERT INTO `menu` VALUES (14, '促销模块', 13, '/special/index', 'Sell', '/special/index', 1, '2023-04-27 14:39:31', NULL);
INSERT INTO `menu` VALUES (15, '社区管理', 0, '/community/notice/index', 'Guide', '/community/notice/index', 1, '2023-05-12 11:34:00', NULL);
INSERT INTO `menu` VALUES (16, '通知公告', 15, '/community/notice/index', 'ChatLineSquare', '/community/notice/index', 1, '2023-05-12 11:33:46', NULL);
INSERT INTO `menu` VALUES (17, '车位列表', 15, '/community/parking/index', 'Location', '/community/parking/index', 1, '2023-05-12 11:34:35', NULL);
INSERT INTO `menu` VALUES (18, '访客记录', 15, '/community/visitor/index', 'Right', '/community/visitor/index', 1, '2023-05-12 11:35:04', NULL);
INSERT INTO `menu` VALUES (19, '报事报修', 15, '/community/complaint/index', 'Setting', '/community/complaint/index', 1, '2023-05-12 11:35:31', NULL);
INSERT INTO `menu` VALUES (20, '缴纳记录', 15, '/community/charge/index', 'CreditCard', '/community/charge/index', 1, '2023-05-12 11:36:13', NULL);

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色名',
  `role_desc` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `role_state` int(1) NULL DEFAULT NULL COMMENT '状态',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, 'admin', '超级管理员', 1, '2023-04-17 11:10:18', '2023-05-12 12:49:34');
INSERT INTO `role` VALUES (2, 'order_manager', '订单管理员', 1, '2023-04-17 12:30:34', '2023-05-16 14:56:10');
INSERT INTO `role` VALUES (3, 'goods_manager', '商品管理员', 1, '2023-04-17 12:30:58', '2023-05-16 14:56:27');
INSERT INTO `role` VALUES (4, 'community_manager', '社区管理员', 1, '2023-05-12 11:31:34', '2023-05-12 12:49:42');

-- ----------------------------
-- Table structure for role_menu
-- ----------------------------
DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE `role_menu`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `role_id` int(10) NOT NULL COMMENT '角色id',
  `menu_id` int(10) NOT NULL COMMENT '菜单id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `menu_rolemenu_idx`(`menu_id`) USING BTREE,
  INDEX `role_rolemenu`(`role_id`) USING BTREE,
  CONSTRAINT `menu_rolemenu` FOREIGN KEY (`menu_id`) REFERENCES `menu` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `role_rolemenu` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 201 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_menu
-- ----------------------------
INSERT INTO `role_menu` VALUES (165, 1, 1, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (166, 1, 2, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (167, 1, 3, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (168, 1, 4, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (169, 1, 5, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (170, 1, 6, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (171, 1, 7, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (172, 1, 8, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (173, 1, 9, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (174, 1, 12, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (175, 1, 10, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (176, 1, 11, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (177, 1, 13, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (178, 1, 14, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (179, 1, 15, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (180, 1, 16, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (181, 1, 17, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (182, 1, 18, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (183, 1, 19, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (184, 1, 20, '2023-05-12 12:49:34', NULL);
INSERT INTO `role_menu` VALUES (185, 4, 1, '2023-05-12 12:49:42', NULL);
INSERT INTO `role_menu` VALUES (186, 4, 15, '2023-05-12 12:49:42', NULL);
INSERT INTO `role_menu` VALUES (187, 4, 16, '2023-05-12 12:49:42', NULL);
INSERT INTO `role_menu` VALUES (188, 4, 17, '2023-05-12 12:49:42', NULL);
INSERT INTO `role_menu` VALUES (189, 4, 18, '2023-05-12 12:49:42', NULL);
INSERT INTO `role_menu` VALUES (190, 4, 19, '2023-05-12 12:49:42', NULL);
INSERT INTO `role_menu` VALUES (191, 4, 20, '2023-05-12 12:49:42', NULL);
INSERT INTO `role_menu` VALUES (192, 2, 1, '2023-05-16 14:56:10', NULL);
INSERT INTO `role_menu` VALUES (193, 2, 10, '2023-05-16 14:56:10', NULL);
INSERT INTO `role_menu` VALUES (194, 2, 11, '2023-05-16 14:56:10', NULL);
INSERT INTO `role_menu` VALUES (195, 3, 1, '2023-05-16 14:56:27', NULL);
INSERT INTO `role_menu` VALUES (196, 3, 5, '2023-05-16 14:56:27', NULL);
INSERT INTO `role_menu` VALUES (197, 3, 6, '2023-05-16 14:56:27', NULL);
INSERT INTO `role_menu` VALUES (198, 3, 7, '2023-05-16 14:56:27', NULL);
INSERT INTO `role_menu` VALUES (199, 3, 13, '2023-05-16 14:56:27', NULL);
INSERT INTO `role_menu` VALUES (200, 3, 14, '2023-05-16 14:56:27', NULL);

-- ----------------------------
-- Table structure for role_user
-- ----------------------------
DROP TABLE IF EXISTS `role_user`;
CREATE TABLE `role_user`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '角色人员关联表id',
  `role_id` int(10) NULL DEFAULT NULL COMMENT '角色id',
  `user_id` int(10) NULL DEFAULT NULL COMMENT '人员id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `role_roleuser_idx`(`role_id`) USING BTREE,
  INDEX `user_roleuser_idx`(`user_id`) USING BTREE,
  CONSTRAINT `role_roleuser` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_user
-- ----------------------------
INSERT INTO `role_user` VALUES (4, 1, 1, '2023-04-18 14:07:43', NULL);
INSERT INTO `role_user` VALUES (9, 2, 12, '2023-04-21 10:05:01', NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(50) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `user_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `user_password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `sex` char(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `mail` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
  `last_login_time` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `user_status` int(2) NULL DEFAULT NULL COMMENT '状态 1.启用 2. 禁用 3.已登录  4 .未登录',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `u_ix_phone`(`phone`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '18611111111', '超级管理员', '6512bd43d9caa6e02c990b0a82652dca', '1', 'mazhihao@neuddu.com', '/avatar/f3df511e79794dbea73089673381ab31.jpg', '2023-04-14 10:28:08', 1, '2023-03-24 09:08:55', '2023-04-12 09:16:49');

SET FOREIGN_KEY_CHECKS = 1;
