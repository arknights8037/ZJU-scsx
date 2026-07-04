/*
 Navicat Premium Data Transfer

 Source Server         : mazh.neuedu.share
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 192.168.137.100:3306
 Source Schema         : mall_portal

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 07/06/2023 10:04:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for area
-- ----------------------------
DROP TABLE IF EXISTS `area`;
CREATE TABLE `area`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '区域id',
  `area_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '区域编号',
  `area_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '区域名称',
  `parent_id` int(11) NULL DEFAULT NULL COMMENT '上一级id',
  `area_type` int(10) NULL DEFAULT NULL COMMENT '地区类型',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of area
-- ----------------------------
INSERT INTO `area` VALUES (1, '100', '大连市', 0, 1, NULL, NULL);
INSERT INTO `area` VALUES (2, '1001', '甘井子区', 1, 2, NULL, NULL);
INSERT INTO `area` VALUES (3, '10011', '龙湖舜山府', 2, 3, NULL, NULL);
INSERT INTO `area` VALUES (4, '10012', '大湖山语', 2, 3, NULL, NULL);
INSERT INTO `area` VALUES (5, '10023', '坦城', 2, 3, NULL, NULL);
INSERT INTO `area` VALUES (7, '3838ef78d4fa439c8812f8d4e1d37b85', '百合山庄', 2, 3, '2023-04-23 15:48:39', '2023-04-23 15:49:01');
INSERT INTO `area` VALUES (8, '33a7e4de1a184c3ea4977c12a84744ee', '沙河口区', 1, 2, '2023-04-23 15:50:50', NULL);
INSERT INTO `area` VALUES (9, '08530bf677044a9baf32efba2dff2958', '软件园社区', 8, 3, '2023-04-26 13:33:10', '2023-04-26 13:33:38');

-- ----------------------------
-- Table structure for carts
-- ----------------------------
DROP TABLE IF EXISTS `carts`;
CREATE TABLE `carts`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '购物车id',
  `user_id` int(50) NOT NULL COMMENT '用户id',
  `store_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '门店编号',
  `goods_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品编号',
  `amount` int(11) NULL DEFAULT NULL COMMENT '数量',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_cart_idx`(`user_id`) USING BTREE,
  INDEX `store_cart_idx`(`store_no`) USING BTREE,
  INDEX `goods_cart_idx`(`goods_no`) USING BTREE,
  CONSTRAINT `goods_trolley` FOREIGN KEY (`goods_no`) REFERENCES `goods` (`goods_no`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `store_cart` FOREIGN KEY (`store_no`) REFERENCES `store` (`store_no`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user_cart` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `goods_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '商品编号',
  `goods_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `category_id` int(11) NULL DEFAULT NULL COMMENT '类别id',
  `goods_introduce` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品介绍',
  `goods_content` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品详细信息',
  `goods_state` int(1) NULL DEFAULT 0 COMMENT '0',
  `goods_picture` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `goods_market_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品市场售价',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `category_goods_idx`(`category_id`) USING BTREE,
  INDEX `goods_no_idx`(`goods_no`) USING BTREE,
  CONSTRAINT `category_goods` FOREIGN KEY (`category_id`) REFERENCES `goods_category` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 200 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods` VALUES (1, '16a2fa61e8c94f6699d36964f61cb7f7', '珍欣无核葡萄干135g', 29, '珍欣无核葡萄干135g', '<p><strong>测试测试</strong><img src=\"/goods/203d25de249a46fc9eea2015a319074f.jpg\" alt=\"\" data-href=\"\" style=\"\"/></p>', 1, '/commodity/6a6f58d2-c0f9-473e-983d-497f05af0c83.jpg', 230.60, '2023-03-30 17:23:32', '2023-04-23 16:08:37');
INSERT INTO `goods` VALUES (2, '292a51053b1749b9bb29825b05c393fa', '珍欣精选广昌通芯白莲(无锡)250g', 30, '珍欣精选广昌通芯白莲(无锡)250g', '<p><img alt=\"\" src=\"/commodity/7e28e9b1-856e-4c85-a1e6-7821bb4e9805.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/7f7af918-ed0f-484a-816d-800be3428bbc.jpg', 230.30, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (3, 'a64897e692ef41b5a4501f50d70f29e0', '珍欣淡水墨鱼王500g', 31, '珍欣淡水墨鱼王500g', '<p>123</p>', 1, '/commodity/6946881700346-1.jpg', 123.00, '2023-03-30 17:23:32', '2023-04-21 15:46:39');
INSERT INTO `goods` VALUES (4, 'f56f34efb86c4db4b3eda8e1f60b2643', '珍欣淡水墨鱼500g', 31, '珍欣淡水墨鱼500g', '', 1, '/commodity/614377d2-10a4-4ebc-a8ed-0a81b5599987.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (5, '421bbac8c94341158841c032d9894e49', '珍欣特选香菇500g', 32, '珍欣特选香菇500g', '<p><img alt=\"\" src=\"/commodity/11b4f0eb-7470-40a1-859e-b32d5a9781e7.jpg\" style=\"width:100%\" /></p>\n', 1, '/goods/1495198ee74048b9b6c17b3f278f0b8f.jpg', 111.00, '2023-03-30 17:23:32', '2023-04-21 15:46:47');
INSERT INTO `goods` VALUES (6, 'c126c02616484a648f0370ff2b87aea2', '珍欣野生好冬菇300g', 32, '珍欣野生好冬菇300g', '<p><img alt=\"\" src=\"/commodity/4a518c4d-42d1-4f2b-ba12-e2460c786f40.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/302ad9ec-f93d-4167-ad53-d15ba9f6c376.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (7, '3aa8cd13da714e6dabad76f816163675', '珍欣广昌茶树菇250g', 32, '珍欣广昌茶树菇250g', '<p><img alt=\"\" src=\"/commodity/50df7904-9034-4ec0-9dc4-48921520b901.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/edbc9970-0adb-431f-8d09-629d4a1e03ff.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (8, 'a12668cc124649d1b94cfe3f53a81bf9', '珍欣猴头菇（无锡)200g', 32, '珍欣猴头菇（无锡)200g', '<p><img alt=\"\" src=\"/commodity/44d71807-f553-467c-9625-122c7a0fda65.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/83e21865-8359-4b42-976a-cab03d7077bd.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (9, 'b909af4bd0d34ad49416949a88cfe1c7', '珍欣特大小籽桂圆王500g', 33, '珍欣特大小籽桂圆王500g', '<p><img alt=\"\" src=\"/commodity/124d07ed-b410-42bb-86db-7f328cab30c2.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/90e8b9c7-68aa-4020-a707-6116df99ec52.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (10, '871d4dea9dd7406e89cf224bd36f66a8', '珍欣精选银耳150g', 34, '珍欣精选银耳150g', '<p><img alt=\"\" src=\"/commodity/92883cde-be6c-4283-a49e-af7b37470725.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/ad1ab5b3-45d7-4776-9554-99343b573813.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (11, '361486a0c9eb43df8eda4e42daa8ee61', '珍欣腐竹250g', 35, '珍欣腐竹250g', '<p><img alt=\"\" src=\"/commodity/d01abc91-e448-455b-912e-a2477fb71857.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/c42e48f4-22e8-45e3-95de-16b86e1170a2.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (12, '2b96c721ddb047dea614533c9914413c', '珍欣新疆和田骏枣500g', 36, '珍欣新疆和田骏枣500g', '<p><img alt=\"\" src=\"/commodity/1b30a724-292e-4b98-9bf4-367aa812fdb4.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/8e90015e-c21a-4582-943e-51da8dd22241.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (13, 'f22f36c8b322410f90af7387c1d4550d', '珍欣阿胶蜜枣380g', 36, '珍欣阿胶蜜枣380g', '', 1, '/commodity/f465753d-d253-4e27-a9e9-efc64c656d03.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (14, '7b08bfbf7b1943df879634b3bf6d7cf1', '齐云山高纯山茶油2L', 37, '齐云山高纯山茶油2l', '<p><img alt=\"\" src=\"/commodity/1b77d1d1-2535-4761-bd67-2d9f5660e7ab.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/01fffe8d-b880-4f94-afb1-c74f14de51ee.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (15, '492970f5f780430e9351ded7bf1a80c7', '多力压榨非转基因葵花籽油8L', 38, '多力压榨非转基因葵花籽油1.8l', '<p><img alt=\"\" src=\"/commodity/cb026077-3dd0-4e4a-87d2-d84c0507ef3b.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/4213930d-5175-4ab5-b0ec-437dad6046d6.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (16, '1e0752b8c9ad42479fcebc9bbfd1f872', '多力压榨非转基因葵花油5L', 38, '多力压榨非转基因葵花油5l', '<p><img alt=\"\" src=\"/commodity/6d08d78d-0eee-4f19-b34f-a0189e02b4ba.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/339e26af-7baa-4821-be49-466c0ca406ad.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (17, 'ce0dd5e0d77a47c48f37301fcabf06c5', '福临门非转基因葵花籽油1.8L', 38, '福临门非转基因葵花籽油1.8l', '<p><img alt=\"\" src=\"/commodity/9727d94b-e69b-4009-b272-5243278c089f.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/929d520c-d0a8-4883-a151-3b10ca0576a6.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (18, 'f611ef02dd2d40eaa8b8447b90722553', '福临门非转基因葵花籽油', 38, '福临门非转基因葵花籽油5l+900ml', '<p><img alt=\"\" src=\"/commodity/8ae0ec7c-7cc4-4365-9b2a-c3984fdda087.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/6941499100816-1.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (19, '415077ea72fe486fb139d345a7c4a800', '多力葵花籽转基因调和油', 39, '多力葵花籽转基因调和油5l', '<p><img alt=\"\" src=\"/commodity/0ffe0067-b0aa-40ca-9ba5-f8f001bab388.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/08613d8d-0447-4c8f-93e9-545fa98f2e5f.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (20, 'd0bb67447493418c8ada18bfb11e1649', '福临门非转压榨一级菜籽油', 40, '福临门非转压榨一级菜籽油5l', '', 1, '/commodity/e2c13ea0-9526-4993-8d20-01ee72991d0d.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (21, 'd280ae925eba42a99bb988da213c7a88', '福临门家香味浓香压榨菜籽油', 40, '福临门家香味浓香压榨菜籽油5l', '<p><img alt=\"\" src=\"/commodity/c5dbe604-145a-48e1-a987-5f9534c0892e.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/cc8a4aeb-1a48-492e-a477-b926e1faa878.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (22, 'efc240af98ba46e4a5d46c4e47c2c323', '福临门非转基因玉米油1.8L', 41, '福临门非转基因玉米油1.8l', '<p><img alt=\"\" src=\"/commodity/9115178b-c209-4150-b651-3323e07ad45f.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/c9414ee7-d9e1-4e48-ab73-3070c0a68823.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (23, 'c96757e31c1a4969be08070434393a29', '多力压榨非转基因玉米油5L', 41, '多力压榨非转基因玉米油5l', '<p><img alt=\"\" src=\"/commodity/840fecc5-45d4-4f85-994e-c41ac2ff222f.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/e085a30a-4ae2-4b69-8815-a2e43cb9f54f.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (24, 'd5e13afe86564d1d8663b3028b1488fa', '福临门非转基因甾醇玉米油5L', 41, '福临门非转基因甾醇玉米油5l', '<p><img alt=\"\" src=\"/commodity/1dd287bb-1c07-42e7-8e6c-de7af17b7d4c.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/43abcde3-c265-441b-a938-eccd5b88ac4b.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (25, '012da18c4d34427e8deadcaf12b285f4', '福临门泰玉香高级进口茉莉香米10kg', 42, '福临门泰玉香高级进口茉莉香米10kg', '<p><img alt=\"\" src=\"/commodity/25ed647e-72fd-47d1-981e-4504a4ad9547.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/5010486f-e852-4fdc-9105-58ed8a9e75c4.jpg', NULL, '2023-03-30 17:23:32', NULL);
INSERT INTO `goods` VALUES (26, 'd3e3b54f636d4f18bbf74741677ce2bb', '福临门香粘米10kg', 42, '福临门香粘米10kg', '', 1, '/commodity/0a7cef06-a4e9-4482-adb3-6e4ad9909ab5.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (27, '982a87f2d5d747cda74bab30fd00c9cd', '福临门桂香软米10kg', 42, '福临门桂香软米10kg', '<p><img alt=\"\" src=\"/commodity/de136547-a9c0-44b0-81fe-1f6d878b677b.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/7326460e-f0bf-4f11-bef6-dab2caf2b6d4.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (28, 'f0d6b7c1b8064bdea70a67c5f0cae685', '福临门优选丝苗米', 42, '福临门优选丝苗米10kg', '', 1, '/commodity/cddb20c9-1c94-4200-8bb2-af8b9f780e2c.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (29, '7d42f802c8b34970acf5aff3bc2a79d3', '齐云山南酸枣糕', 18, '齐云山南酸枣糕454g', '<p><img alt=\"\" src=\"/commodity/72712177-e254-4072-8f39-47e96e04d455.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/957cce56-b9bc-4c6c-9a3f-8cd7e2e575af.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (30, '592e437d264a4dba966e2e68a52ae088', '齐云山礼盒枣糕', 18, '齐云山礼盒枣糕1.2kg', '<p><img alt=\"\" src=\"/commodity/d0f53688-8de3-4393-9eaa-46759bf9c4fd.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/e4400a4d-6054-4771-9113-1fcc7c171300.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (31, '9b1eb99ccf5a41ba9facce8aa4278940', '齐云山高纯山茶油', 37, '齐云山高纯山茶油5L', '<p><img alt=\"\" src=\"/commodity/51ac9315-791c-4138-ac0c-35ece7ef7cb0.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/df279eea-2c81-4631-bf0e-d708a1f3f2f0.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (32, 'ab061af8673f4beb90669772f2d4a28e', '福临门泰玉香高级进口茉莉香米5kg', 42, '福临门泰玉香高级进口茉莉香米5kg', '<p><img alt=\"\" src=\"/commodity/b1628bff-b86c-4d92-9b4c-d38430244f43.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/6943ad2e-5399-4bd7-a2b6-5c758f5ed079.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (33, '0cbba76f4cf0434da9aa7e4a14e884ea', '福临门香粘米', 42, '福临门香粘米5kg', '<p><img alt=\"\" src=\"/commodity/47b15b89-f565-4db9-8c42-89b73acd10dd.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/6944910322101-1.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (34, '4c8ae591ecb340289b1330547be08fbd', '福临门桂香软米5kg', 42, '福临门桂香软米5kg', '<p><img alt=\"\" src=\"/commodity/f73ea12d-c51b-45a7-82e0-4be9110888a6.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/3c83c181-b016-4e01-a6d5-772f75b0c4c3.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (35, 'e486e0f6a32d4756b8104d5c1c124d16', '珍欣精选新疆玉枣1kg', 36, '珍欣精选新疆玉枣1kg', '<p><img alt=\"\" src=\"/commodity/d3983ca7-2c28-4d5c-9eb3-17afb2400654.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/6c644a6e-0166-4314-9db8-b848ad50a352.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (36, '87ae1ecaeb424499a5ea95e427106ba5', '珍欣无核葡萄干218g', 29, '珍欣无核葡萄干218g', '<p><img alt=\"\" src=\"/commodity/d6b07ceb-9475-4886-bf32-4b601060971f.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/bee1a77d-3cd1-40ca-a74a-991fa6d6bef5.png', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (37, '32ecd0a88f1c40fe82e40ce796572e34', '珍欣无核葡萄干1', 29, '珍欣无核葡萄干250g', '', 1, '/commodity/6970016720039-1.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (38, '68c99eb3565f499890ebaf9545353438', '珍欣精选野生花菇400g', 32, '珍欣精选野生花菇400g', '<p><img alt=\"\" src=\"/commodity/9daccc2b-975d-432d-a3fa-ea940241b7b0.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/accddc25-eaea-4c6a-900e-a531cea5d354.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (39, '3b9c5bd0fd454ca4b7217d5c094b290f', '珍欣珍珠菇250g', 32, '珍欣珍珠菇250g', '<p><img alt=\"\" src=\"/commodity/08c303d8-14be-4589-a70e-9a94063987a4.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/f101b300-fd61-4e50-a43d-81433f3abd5f.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (40, '638d4d072f06458a8c9014f5ae31a68d', '珍欣精选香菇400g', 32, '珍欣精选香菇400g', '<p><img alt=\"\" src=\"/commodity/d60c62d9-0bee-45e2-ad3e-33c67a3fdfd1.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/71355484-0a74-4dc7-94b0-ff29ef66620e.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (41, '95b1e97e6e64419ba5fade025c2ef00f', '珍欣精选黑木耳500g', 43, '珍欣精选黑木耳500g', '<p><img alt=\"\" src=\"/commodity/4d0ffbc6-e635-4de5-98e3-0b0811d8ac52.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/171f6190-105c-46d0-985c-10d5cbf61f56.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (42, 'd2ab314272804c5b98fd4276d04dc838', '珍欣精选秋木耳300g', 43, '珍欣精选秋木耳300g', '<p><img alt=\"\" src=\"/commodity/fb2fbedf-cab3-4c2b-8470-5d0f2e07b4a0.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/9fecd79c-72a9-4e2f-a7c4-e7aaaa5e6ae0.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (43, 'a21211eca70e43508f383175c86379b7', '珍欣野生云耳250g', 34, '珍欣野生云耳250g', '', 1, '/commodity/2d92f5ff-1028-415a-86d4-ffe1cc7989c3.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (44, '33f9ea4aadad4bcb9c14e0883cbffd05', '珍欣本色银耳250g', 34, '珍欣本色银耳250g', '<p><img alt=\"\" src=\"/commodity/f85b5e09-be48-4585-8f48-c6da299a5cbd.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/3e53cdd2-6ae0-40ec-95eb-5fc74ece1e1e.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (45, '7427dee2cce34900a5c83e8459ffe56b', '珍欣特级东北绿豆2.5kg', 80, '珍欣特级东北绿豆2.5kg', '<p><img alt=\"\" src=\"/commodity/f4f65616-67fb-4928-9e82-a4f6b929c03b.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/bb7e4068-345a-4a24-bcb5-6f1cb0583f28.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (46, '9bc484e25e18466a86489e068a246460', '潘婷垂顺直发洗发水200ml', 46, '潘婷垂顺直发洗发水200ml', '<p><img alt=\"\" src=\"/commodity/b51ff369-0526-4d8c-9a81-e29dd558151f.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/2a58f8c5-b349-4f97-8bc1-b5bbd6ca713c.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (47, 'a456157078c9493bb1463f7b572ea06f', '飘柔兰花长效去屑洗发水200ml', 46, '飘柔兰花长效去屑洗发水200ml', '<p><img alt=\"\" src=\"/commodity/127c1b16-d7c9-42af-91ef-489b94ed026f.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/95884e81-ad1a-43f7-8b9d-8eaaff873a24.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (48, 'e849f2c9d4174c6a8bc5b75424c16bcd', '飘柔兰花长效水润洗发水200ml', 46, '飘柔兰花长效水润洗发水200ml', '<p><img alt=\"\" src=\"/commodity/8c95990a-28ba-45ab-9ca3-d7bbdeadfe06.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/1af5369f-5427-4fc1-aef4-e4e5b9a40395.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (49, '24da267a6b4d43af835a05fd9e3d8404', '飘柔兰花长效绿茶洗发水400ml', 46, '飘柔兰花长效绿茶洗发水400ml', '<p><img alt=\"\" src=\"/commodity/c676cc76-00db-430e-bdbf-f95016723dcc.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/c0abb6d0-e7c0-4abe-94ba-7a783d16cfc5.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (50, 'ac2345f5ecb446e79a33155598079a32', '飘柔兰花长效清爽去屑洗发水400ml', 46, '飘柔兰花长效清爽去屑洗发水400ml', '<p><img alt=\"\" src=\"/commodity/d4d38ad7-22d3-4df0-b83b-3539561c6348.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/32805f99-d68f-4858-90c1-71eebe08cb77.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (51, 'b0a765e9e417457f9738be66fcdee3f2', '飘柔精华维他命长发洗发水200ml', 46, '飘柔精华维他命长发洗发水200ml', '<p><img alt=\"\" src=\"/commodity/a563ff2b-baa2-45f3-9e12-b2fc2955cfa2.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/ac36c470-231b-4585-8640-40cd479bcc2d.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (52, 'fd37d1bf188f44a588efa1761b47ae2d', '飘柔精华滋润去屑洗发露200ml', 46, '飘柔精华滋润去屑洗发露200ml', '<p><img alt=\"\" src=\"/commodity/019a4892-a36b-430e-a6b0-af8a719430d1.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/ea1d257c-441c-41aa-ac66-466067eb4285.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (53, '738a0e245d6e47f9b6501bc6e0a5c0f4', '海飞丝怡神冰凉型去屑洗发水200ml', 46, '海飞丝怡神冰凉型去屑洗发水200ml', '<p><img alt=\"\" src=\"/commodity/3a1eb508-2557-428e-868a-ecc8f963822b.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/57b40a43-8d84-4cb6-9cd4-639da06c4160.jpg', NULL, '2023-03-30 17:23:33', NULL);
INSERT INTO `goods` VALUES (54, 'f6197fe4652a4ed09786dbd94da2a0af', '海飞丝海洋活力洗发水200ml', 46, '海飞丝海洋活力洗发水200ml', '<p><img alt=\"\" src=\"/commodity/57abba27-d883-4c29-b87e-6e050ad0ecaf.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/24715ac9-2997-4253-9674-657cb509bc42.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (55, 'dbc9c69427b44e16bce77052b610c20b', '海飞丝丝质柔滑洗发水200ml', 46, '海飞丝丝质柔滑洗发水200ml', '<p><img alt=\"\" src=\"/commodity/88351289-2119-4ac7-91bb-154e19ef0b5e.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/54c9e26e-8aef-4a7a-87c6-f1e1beca8720.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (56, 'eb88efd402304cbd80ada5c92e78c904', '清扬活力运动薄荷型男士洗发露', 46, '清扬活力运动薄荷型男士洗发露200ml', '<p><img alt=\"\" src=\"/commodity/c85e6802-1605-486d-83a4-42b9798a023e.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/6902088113112-1.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (57, '737a730e730d4882830af119a29537bb', '海飞丝去屑洗发露丝质柔滑型400ml', 46, '海飞丝去屑洗发露丝质柔滑型400ml', '<p><img alt=\"\" src=\"/commodity/f037450f-6b1e-49e6-897d-66d203f75955.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/5867d182-88d6-4431-a7f6-9840f0c90950.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (58, 'fa61586d51134267832c37ac94f11be8', '蒂花之秀倍效焗油顺滑护发素', 47, '蒂花之秀倍效焗油顺滑护发素500ml', '<p><img alt=\"\" src=\"/commodity/1417ec14-6a0f-4ec5-a5e7-7b77bd1eb698.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/e7bad617-7733-4fa0-a2a8-5ee320a94580.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (59, 'b96d0b9010cb4ef69a027341785f0f09', '舒肤佳芦荟护肤沐浴露200ml', 48, '舒肤佳芦荟护肤沐浴露200ml', '<p><img alt=\"\" src=\"/commodity/87f4bd74-134c-46dc-bdb6-df3e0763fcff.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/131231a5-0287-4845-a1d1-68e8b16c8658.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (60, 'c096e1e874004c4eb39602fbbef5d477', '舒肤佳柠檬清新型沐浴露200ml', 48, '舒肤佳柠檬清新型沐浴露200ml', '<p><img alt=\"\" src=\"/commodity/468e468b-9453-4bfd-8622-d734513e8084.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/78c04700-71b3-4fa2-9d1c-8a9a9be18e8c.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (61, '2e2ed6cd61904623817a822cb6426a05', '舒肤佳纯白清香沐浴露200ml', 48, '舒肤佳纯白清香沐浴露200ml', '<p><img alt=\"\" src=\"/commodity/21af8fbd-0cec-4b9c-a2b9-2f906e305fa9.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/20b14204-7f66-4991-a301-c9d85b9df897.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (62, '925eda52282b45deb9f74711a56261d1', '舒肤佳(纯白清香型)沐浴露400ml', 48, '舒肤佳(纯白清香型)沐浴露400ml', '<p><img alt=\"\" src=\"/commodity/87c0d434-df7c-411b-ba4b-7923dea08099.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/cf94542e-0570-4b85-b03f-cf37ad893a0f.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (63, '498b07069d2e4031a62b34927e8f16f0', '舒肤佳芦荟水润呵护型沐浴露400ml', 48, '舒肤佳芦荟水润呵护型沐浴露400ml', '<p><img alt=\"\" src=\"/commodity/4cf55785-674f-47ec-ac4d-0920f927dd33.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/26c6a0b1-e683-4029-869d-031c16114b33.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (64, 'f45ba8fc7a6e4dbba87cd561ae226b78', '玉兰油深润滋养沐浴露200ml', 48, '玉兰油深润滋养沐浴露200ml', '<p><img alt=\"\" src=\"/commodity/c8a45cca-e8e2-4264-b6a7-e43b2540a19b.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/05951006-b15c-410a-8956-f8f91d376f94.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (65, 'aa7840d2a64142e7a42b65422c292380', '玉兰油美肌滋润美白沐浴乳200ml', 48, '玉兰油美肌滋润美白沐浴乳200ml', '<p><img alt=\"\" src=\"/commodity/60fa1337-8b7f-426a-96cb-6dcfb7045dcf.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/beea10d4-2e68-4e94-8957-e85bb980ea22.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/cb58d66e-8444-4a72-9f9d-dda52ed070fd.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/1499eca6-2d1b-405d-a7af-154c640e7723.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (66, 'bb36194793044e72aea8b8225fa3464a', '玉兰油深润滋养沐浴露400ml', 48, '玉兰油深润滋养沐浴露400ml', '<p><img alt=\"\" src=\"/commodity/c349195f-befa-4b7d-a794-578b9681accd.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/9467e218-a1b8-4f74-b6cf-9b6e122b1848.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (67, '07287166fa454be18e8c6142efb46a39', '玉兰油美白滋润沐浴露400ml', 48, '玉兰油美白滋润沐浴露400ml', '<p><img alt=\"\" src=\"/commodity/ab1130ad-b1a9-4524-9ee2-1d37f628b828.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/26da1188-3110-4da2-b249-32e612f18544.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (68, 'c3d58cd202154790af95c542e1a8b54c', '舒肤佳芦荟呵护型115g', 49, '舒肤佳芦荟呵护型香皂115g', '<p><img alt=\"\" src=\"/commodity/985c4953-dcb0-48e2-b4d8-f5cf19d4af0e.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/68bd47da-0b41-435c-a791-a3e7a1019ee1.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (69, 'b0267fc794ef4a66855652443083c795', '舒肤佳柠檬清新型香皂115g', 49, '舒肤佳柠檬清新型香皂115g', '<p><img alt=\"\" src=\"/commodity/bd9c3528-eaa9-469c-a84b-699fac95d069.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/cef939a4-63d1-4842-87b5-08dd9471a2d2.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (70, 'cffedbeca45f4f7bba8df8e06ad647c3', '力士白皙焕彩靓肤香皂115g', 49, '力士白皙焕彩靓肤香皂115g', '<p><img alt=\"\" src=\"/commodity/2c747a4f-55c3-4dc5-a391-09a7dc6b6850.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/38bf14c0-77df-4fcb-9491-72286b05650c.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (71, 'f2fb8c9a456c465da7172f065de8704a', '舒肤佳金银花香皂125g', 49, '舒肤佳金银花香皂125g', '<p><img alt=\"\" src=\"/commodity/dca16884-ee1f-4d0f-b5c6-196b13ff4b79.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/da969428-7fe2-427d-b786-a7f8ad1462a7.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (72, 'c41624f99cdd46d5a56b5c2c190eb02c', '舒肤佳柠檬去味型香皂125g', 49, '舒肤佳柠檬去味型香皂125g', '<p><img alt=\"\" src=\"/commodity/b1bed7c0-2042-4a25-9c5c-fe64ac6704b3.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/06f60675-d2b5-44eb-bdf7-911841fa1baa.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (73, '858be1887e544acb95172f0f0d5fc1fd', '舒肤佳纯白清香型香皂125g', 49, '舒肤佳纯白清香型香皂125g', '<p><img alt=\"\" src=\"/commodity/70ed2e18-76de-415c-90c0-b847f9947057.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/6d3a03ac-3a3c-4e2d-a7a7-53356751a073.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (74, 'd0b97ae7961d4a5cb099c4c709c8aa8a', '舒肤佳纯白清香型香皂115g', 49, '舒肤佳纯白清香型香皂115g', '<p><img alt=\"\" src=\"/commodity/0dff4b13-b801-46be-8e26-9f8f918c3a8f.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/0633e373-6fb0-4e01-ba17-8971634a21c4.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (75, 'f54b150480af4c1788baba75fd401f2f', '七度空间日用少女纯棉卫生巾10片', 55, '七度空间日用少女纯棉卫生巾', '<p><img alt=\"\" src=\"/commodity/1fb20de2-6d4b-4b2c-900b-02eb34e1c1fd.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/391abe49-3f49-4241-8e95-c6807741ed48.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (76, 'f07a7f33ca7c465b84508c17844d3155', '七度空间夜用少女纯棉卫生巾10片', 55, '七度空间夜用少女纯棉卫生巾', '<p><img alt=\"\" src=\"/commodity/cfd9b23e-a761-458b-bd3b-d92952b81cbc.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/6e865782-76b1-4933-9cd9-3e756b05369f.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (77, '15729d1dc0834cd0b04276055b2d30ad', '七度空间女生护垫18片', 55, '七度空间女生护垫', '<p><img alt=\"\" src=\"/commodity/b2f86a86-b7a9-4a31-baba-ed45fcb3e93f.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/1552777b-b9fe-42a4-a63f-3e2a9b7563ce.jpg', NULL, '2023-03-30 17:23:34', NULL);
INSERT INTO `goods` VALUES (78, '3f17ce33f4b44ce28e9ba171d0f4f526', '丁家宜美白补水洗面奶120g', 57, '丁家宜美白补水洗面奶120g', '<p><img alt=\"\" src=\"/commodity/a6b92ad2-f13a-4a67-9f3c-7bc6d86dff9e.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/32abbe83-9fca-4f03-bf67-edead01befd4.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (79, '2e7a3125f438410e9c1c3b40a7c170fa', '妮维雅晶纯晳白泡沫洁面乳100g', 58, '妮维雅晶纯晳白泡沫洁面乳100g', '<p><img alt=\"\" src=\"/commodity/894447f2-b46e-4242-bec0-5b2143f89d1a.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/ffed24bc-6495-4d43-9732-03bea5cedc4f.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (80, '1e8e882c45824ddcae8e4a21c8610fa8', '妮维雅男士多效净透洁面乳100g', 58, '妮维雅男士多效净透洁面乳100g', '<p><img alt=\"\" src=\"/commodity/51a07707-b2e8-4c2d-89f4-042007207759.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/398b305e-052c-4deb-9b30-b6e799ccef3a.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (81, '5d10a42b834a4dc48ca23676b2902887', '金号柔丝毛牙刷', 59, '金号柔丝毛牙刷', '', 1, '/commodity/4e8667d8-da24-49b8-8cc9-8f6f26367b06.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (82, 'c21ef37b6f714ed3b9ad6bacbace6fcd', '金号纳米牙刷533', 59, '金号纳米牙刷533', '', 1, '/commodity/e2b58728-465d-452b-9d4c-de52646c2760.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (83, '4cc384339b404dae870983efd3394057', '中华健齿白炫动果香味牙膏90g', 60, '中华健齿白炫动果香味牙膏90g', '<p><img alt=\"\" src=\"/commodity/17f09b8d-fd9e-4704-a2b9-83be29ff1b04.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/4b41fb05-4db3-4881-8d41-583839ce5b23.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (84, '249da0b225aa49ce8a7e9482fda971a7', '中华健齿白清新薄荷味90g', 60, '中华健齿白清新薄荷味90g', '', 1, '/commodity/b9612278-3082-45ef-b44d-ebab8152d8fc.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (85, '9fbe4575ea7a4f8e862b8395c3df9362', '黑人双重薄荷牙膏90g', 60, '黑人双重薄荷牙膏90g', '<p><img alt=\"\" src=\"/commodity/d625415d-0bee-4c52-aec5-94c90e952ed9.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/eddd3be7-930e-499e-8162-424d2bda3efd.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (86, 'ee41b9fbdbc045fb96fe5916e757cfc6', '黑人超白牙膏140g', 60, '黑人超白牙膏140g', '', 1, '/commodity/39e1cdc8-eddb-42e2-b8d2-609f4147c488.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (87, '5ddc33b8d5f9423fb4860616bf489fbf', '佳洁士炫白柠檬茶爽牙膏120g', 60, '佳洁士炫白+柠檬茶爽牙膏120g', '<p><img alt=\"\" src=\"/commodity/154ca4cb-dcba-4206-996d-bb8983852d38.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/c9d948d5-9953-4aaf-ac28-4e862dd90e9a.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (88, 'f92798bbd3014da4b246dc8f857a5fa2', '高露洁固齿清新美白牙膏90g', 60, '高露洁固齿清新美白牙膏90g', '', 1, '/commodity/e85aa3dc-d2d8-4fe4-b439-794b9cd959fa.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (89, '7d29a9e9c54e4ce5ab593945a8c94edf', '竹盐素盐白爽口牙膏100g', 60, '竹盐素盐白爽口牙膏100g', '<p><img alt=\"\" src=\"/commodity/7c6fd959-baa2-437b-b08d-ca0663518321.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/1b090a31-587d-4c88-a5ea-c8a3936d6e50.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (90, '16caad5d3da943fdba228944f9249019', '奥妙净蓝全效洗衣粉300g', 61, '奥妙净蓝全效洗衣粉300g', '<p><img alt=\"\" src=\"/commodity/4ddb314f-f6f0-4817-bbee-e0c18b8a5ba2.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/448a73a6-c54a-47e0-9741-ea9826fdc1c2.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (91, '9bcf993ac2394fe797580f38b1cfb1a8', '汰渍净白去渍洗衣粉(柠檬清新型)508g', 61, '汰渍净白去渍洗衣粉(柠檬清新型)508g', '<p><img alt=\"\" src=\"/commodity/6247c81e-6a4e-4920-a844-c841289f7c2a.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/4613b332-d6c0-4803-9113-9b972347af24.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (92, '490ebcbe76834f659f0d2e95ce3342e7', '汰渍净白去渍洗衣粉1.36g', 61, '汰渍净白去渍洗衣粉1.36kg', '<p><img alt=\"\" src=\"/commodity/a8dcc471-4f9d-4496-9e92-c68ff986566c.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/53018e84-a948-42ac-bc80-7010fef52862.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (93, '07e3fab379244ff5b6f78f6ad41e75ad', '雕牌超效加酶洗衣粉252g', 61, '雕牌超效加酶洗衣粉252g', '<p><img alt=\"\" src=\"/commodity/8d7ab8b3-04d5-44c4-847e-b048e411288e.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/7c337b0a-e4f6-4df8-836c-22af252713b7.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (94, '4582ea22e68647368a7f0ffcb25efe2c', '雕牌洗衣粉508g', 61, '雕牌洗衣粉508g', '<p><img alt=\"\" src=\"/commodity/727e1c9b-2245-4dd0-b700-3832d88a6299.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/73f38055-c925-4127-a4c3-ad214cdcfb97.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (95, '6d78ac772ec84a149d6df33e53699c3b', '立白新金桔洗洁精408g', 62, '立白新金桔洗洁精408g', '<p><img alt=\"\" src=\"/commodity/de6ffdac-5116-4eeb-b2dc-b0bf195e6162.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/027583cf-f836-41d0-a04a-ce46946d0c15.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (96, '5e4251cbcf77491994f812f650b99388', '雕牌高效洗洁精500g', 62, '雕牌高效洗洁精500g', '<p><img alt=\"\" src=\"/commodity/b67d1652-ea3a-4307-86cd-06644877432f.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/68514e0c-e256-470e-ae9e-65fc41c55c7a.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (97, '870bdc36ff044951b6650ceef339ac8f', '蓝月亮芦荟抑菌洗手液', 63, '蓝月亮芦荟抑菌洗手液500g', '<p><img alt=\"\" src=\"/commodity/4fd6b56c-c427-46c5-80a7-a165b766ece8.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/6902022130861-1.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (98, 'a16a86ecd361421f92e61bf3b0ae768c', '蓝月亮深层洁净护理薰衣草味洗衣液', 64, '蓝月亮深层洁净护理薰衣草味洗衣液500g', '<p><img alt=\"\" src=\"/commodity/c185f5eb-410d-4e71-9e37-64e63893182e.jpg\" style=\"width:1005px\" /></p>\n', 1, '/commodity/707e6fb4-437c-4581-ba4b-8bf796199ca9.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (99, 'f446cce840784008b072bcf6b0650ee0', '蓝月亮薰衣草亮白增艳洗衣液', 64, '蓝月亮薰衣草亮白增艳洗衣液3kg', '<p><img alt=\"\" src=\"/commodity/3c076459-2bd2-4ed0-a199-3611b3e269fd.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/9f07a51d-21a1-487a-b898-b5e250285d7d.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (100, 'd519569e8d87410b8521435a26cb9933', '雕牌洗衣皂202g', 65, '雕牌洗衣皂202g', '', 1, '/commodity/95f7343e-f4cb-4f3f-8695-a0c6b79cb610.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (101, '706040790f5e4ff69a20eedb38c56549', '六神花露水95ml', 66, '六神花露水95ml', '<p><img alt=\"\" src=\"/commodity/320bfa5f-fa45-4e27-8628-82a0bdf1c33d.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/b77b3258-3f85-4505-bdf7-7c5f4a5d4258.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (102, 'cc02635cdfdc4defbad3a0d6c1b41964', '六神特效花露水195ml', 66, '六神特效花露水195ml', '<p><img alt=\"\" src=\"/commodity/f291ce77-1a94-46e1-bb6d-65d91b6ebec7.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/84b3c347-dce8-4264-99e4-313b02f1cb5d.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (103, 'd4d1a3bd9e994f64ad7c2ee8eff69ee1', '六神驱蚊喷雾花露水', 66, '六神驱蚊喷雾花露水180ml', '<p><img alt=\"\" src=\"/commodity/ea979a76-a223-4431-b2be-28899f68b00c.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/6901294179608-1.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (104, '308502b9df0149b7ad07919fcfbc7127', '维达150g2粒卷纸', 69, '维达150g2粒卷纸', '<p><img alt=\"\" src=\"/commodity/47bf2bd8-f0f1-4472-9a33-fe246bc21a30.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/fe26de6f-fd80-4cb2-9d32-36f932de9361.jpg', NULL, '2023-03-30 17:23:35', NULL);
INSERT INTO `goods` VALUES (105, '8efd0cddfba14d469f68d3fbdf7bd146', '维达140g10粒卷纸', 69, '维达140g10粒卷纸', '<p><img alt=\"\" src=\"/commodity/6c7c4734-9530-4f39-9c10-51b7b179e0a8.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/7a67f84c-3190-433e-b759-d9ec6ea381d5.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (106, 'ff8392fcfbbe4bc3b7243218996fb1de', '维达70g10粒无芯长卷纸', 69, '维达70g10粒无芯长卷纸', '<p><img alt=\"\" src=\"/commodity/5741edaf-ffea-4cfb-85f7-2fe3531ada11.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/163efa06-34d0-481a-b4ec-cf143babb537.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (107, '0fb231f449844c65be8e4ea6d012bacf', '维达迷你手帕纸*12', 70, '维达迷你手帕纸*12', '<p><img alt=\"\" src=\"/commodity/9d2cadd8-48c0-42d5-96bb-dc9d7746e09b.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/cbed06a5-729e-46cf-943d-ba1654d1a8ec.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (108, '79f68fc662d94ffaae352be967633760', '维达清爽10片湿巾', 71, '维达清爽10片湿巾', '<p><img alt=\"\" src=\"/commodity/6c35ecc8-a97e-4ff4-bb71-9504d9c144ca.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/882acda5-9a4f-4cd3-990d-344ac6905684.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (109, '01833ffb827d4da48dfe8a2528c0020e', '维达去油去汗10片湿巾', 71, '维达去油去汗10片湿巾', '<p><img alt=\"\" src=\"/commodity/7b5ad168-7489-4c53-b9bf-e2cb5e14b865.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/7c7c568a-8a84-4991-a105-999041ccc2ba.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (110, '58686e68a3db47f3a723a1acb08257eb', '蒙牛纯牛奶250ml*16', 96, '蒙牛纯牛奶250ml*16', '<p><img alt=\"\" src=\"/commodity/3091d9ee-44df-4929-a035-56fdd54527c6.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/60ce0787-4b02-4b2a-81a0-704383c1d920.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (111, '940b6061d34a45f69d3ae7d1e5c8d925', '蒙牛特仑苏纯牛奶250ml*12', 96, '蒙牛特仑苏纯牛奶250ml*12', '', 1, '/commodity/c21d0dcf-767d-4571-9373-d71e35f07c17.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (112, 'd69b08093ecb46ed9f94e59a7a1e5246', '蒙牛纯甄200g*12', 96, '蒙牛纯甄200g*12', '<p><img alt=\"\" src=\"/commodity/87081444-744b-4215-b881-b72362ed4132.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/aa3cc53b-e4c4-4c64-8b4a-d456e51e750e.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (113, 'f5aa6e9f55cb4014a13cef7fd0c4127c', '蒙牛酸酸乳原味250ml*24', 97, '蒙牛酸酸乳原味250ml*24', '<p><img alt=\"\" src=\"/commodity/0a9b7238-ad54-4ca5-8758-0005be509954.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/642f83e6-4818-4c40-b939-3a5cf7beddbe.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (114, '3a166b2cf6204424be4019e9dda7d001', '蒙牛酸酸乳草莓250ml*24', 97, '蒙牛酸酸乳草莓250ml*24', '<p><img alt=\"\" src=\"/commodity/c4402824-1745-4f65-b804-6e6f5aa299ad.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/6d861f91-0004-46c2-a0d2-549fdd5a0831.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (115, 'ec290854177e4b17a00acdb6c2d81677', '蒙牛真果粒草莓味', 97, '蒙牛真果粒草莓味250ml*12', '<p><img alt=\"\" src=\"/commodity/434455de-a250-42ce-ad6b-7f9337e32b82.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/216a26f6-9fd6-41f2-9c1f-1dc5999c1104.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (116, 'b1f367961f754c87b9ceed9a40887b21', '蒙牛真果粒黄桃味250ml*12', 97, '蒙牛真果粒黄桃味250ml*12', '<p><img alt=\"\" src=\"/commodity/e3cc4718-ba34-4bc6-81c8-738a42213167.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/7c9da8ad-79a5-40fd-b9c4-9811cdb90ac0.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (117, '9f6e9d7263bd4e4d88ea90b0be58179e', '蒙牛朱古力奶特243ml*12', 97, '蒙牛朱古力奶特243ml*12', '<p><img alt=\"\" src=\"/commodity/992ac768-c523-4d5c-bf73-472708002a2c.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/ff3d85ee-fdbc-404f-beff-d6c88c2e25ee.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (118, '3e221bcfbccc456383da96f9e33efa0c', '蒙牛香蕉慕斯奶特243ml*12', 97, '蒙牛香蕉慕斯奶特243ml*12', '<p><img alt=\"\" src=\"/commodity/a3f7638c-fbeb-4910-befe-dc78d687f306.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/233c3459-e54a-46da-951b-f6fde94de1ed.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (119, 'd6b7310e8bbf4bab81f177144fcad301', '蒙牛香草味奶特243ml*12', 97, '蒙牛香草味奶特243ml*12', '<p><img alt=\"\" src=\"/commodity/312b0a08-a4e0-44d2-859e-40e0b25cb3cc.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/f5c8d469-c3b9-4a82-924e-baae5b7f8a49.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (120, 'ebb769fc418d4f4e9c3dff35d3ddcc3c', '蒙牛果蔬草莓250ml*12', 97, '蒙牛果蔬草莓250ml*12', '<p><img alt=\"\" src=\"/commodity/bae85aa7-0ddc-41dc-af0f-ca257563de46.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/69ea45d5-6632-4908-82eb-a2fb09929b52.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (121, 'f47fddee2b75447a85f7959c683f0071', '蒙牛果蔬菠萝250ml*12', 97, '蒙牛果蔬菠萝250ml*12', '<p><img alt=\"\" src=\"/commodity/d0ac0b22-5803-43e8-b87c-76f391b5cbd3.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/883b3739-68c9-45c4-90cc-efa597f62158.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (122, 'fd45827cb8da46158d3074045b34beb3', '蒙牛早餐奶原麦250ml*16', 96, '蒙牛早餐奶原麦250ml*16', '<p><img alt=\"\" src=\"/commodity/179536d7-879e-4771-9e85-a5cb442db5d1.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/7b7f19c1-e544-4d2f-88d7-0b4f7cfdb8ed.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (123, 'e65f694df46a4937b94f25115ec02f83', '蒙牛早餐奶核桃250ml*16', 96, '蒙牛早餐奶核桃250ml*16', '<p><img alt=\"\" src=\"/commodity/1cb022c3-8bf8-4ba9-8b95-f5c02fe63e64.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/c8aafdb6-e50e-4076-a96e-4458bbcdc966.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (124, '158cfae18bc844879d15177f3e886033', '中华健齿白炫动果香味牙膏90g', 60, '中华健齿白炫动果香味牙膏90g', '<p><img alt=\"\" src=\"/commodity/c9777d04-5d50-4d17-af50-5d13a174fd41.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/cc8c25fa-4087-4c5a-a592-af4d4258cba4.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (125, '428db7b0a52b45d39f8495b394f38a43', '中华健齿白清新薄荷味90g', 60, '中华健齿白清新薄荷味90g', '<p><img alt=\"\" src=\"/commodity/3eacc681-f81f-4062-8c78-9383006daa9a.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/914228d6-ea4c-4b9a-b0a3-87f45c658019.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (126, '398e03385e48418880ee520a322a2174', '黑人超白牙膏140g', 60, '黑人超白牙膏140g', '<p><img alt=\"\" src=\"/commodity/2938cac1-7794-4578-b64e-b51a43a5f101.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/7a7ecf7a-7d1c-4697-9d9d-a936da4457c5.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (127, '2858bfb6b4fa4ecfb9a44fcca8d104e3', '高露洁固齿清新美白牙膏90g', 60, '高露洁固齿清新美白牙膏90g', '<p>one nigerad asd asd asd asd asd asd asd asd&nbsp;</p>\n\n<p>asfdsfd&nbsp;</p>\n\n<p>dfgfd</p>\n\n<p>g\\&nbsp;</p>\n\n<p>fghlpfdgl\\d f</p>\n\n<p>&nbsp;g\\</p>\n\n<p>fghlg;h./s\\df</p>\n\n<p>&nbsp;</p>\n\n<p><img alt=\"\" src=\"/commodity/50dd7bc9-47ee-4343-a230-0683dee9629c.png\" style=\"height:188px; width:300px\" /><img alt=\"\" src=\"/commodity/9d01ec74-64f8-4502-a71b-70f600218bda.jpg\" style=\"height:248px; width:330px\" /></p>\n', 1, '/commodity/a5877978-836a-4566-9e9d-5e9ddf6bc185.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (128, '029db30046e04b3e9324d31cf55e35f5', '三叶草单衣', 95, '万佳蛋酥沙琪玛50g', '', 1, '/commodity/e5ddbbbf-a649-46c8-89fd-daae345a9ff2.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (129, '4f3a80a835064f889b72dbf240f999f3', '徐福记香橙酥', 18, '徐福记91g香橙酥', '<p><img alt=\"\" src=\"/commodity/42791fc4-e245-42a5-a2d0-6715d35eecd0.jpg\" style=\"height:430px; width:430px\" /></p>\n', 1, '/commodity/3b069b53-7cb7-4880-83a0-ebf7d6d620b8.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (130, 'fc8b011e9d744b95850c82bea2f5b024', '好大嫂老坛酸菜鸡爪25g', 20, '好大嫂老坛酸菜鸡爪25g', '<p><img alt=\"\" src=\"/commodity/955f027e-88cc-467a-bb45-d800bea976d6.jpg\" style=\"height:100%; width:100%\" /></p>\n', 1, '/commodity/827d9d4a-33b0-460e-af8e-1f3d3577a190.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (131, 'f603355a7c224374ad21003a90bc2786', '好大嫂老坛酸菜鸭掌28g', 20, '好大嫂老坛酸菜鸭掌28g', '', 1, '/commodity/8cbdeda4-0069-4cce-be36-03f7e2764092.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (132, '88ec044990f0463b9e59536725044c0b', '珍欣精选广昌通芯白莲(无锡)250g', 30, '珍欣精选广昌通芯白莲（无锡）500g', '<p><img alt=\"\" src=\"/commodity/8696acc0-1fc3-4fd6-9660-3f9f0d265894.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/7f7af918-ed0f-484a-816d-800be3428bbc.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (133, '9cf9a0d3710a47da83dffcfca6f34750', '齐云山高纯山茶油2L', 37, '银鹭好粥道玉米味八宝粥1*12', '', 1, '/commodity/01fffe8d-b880-4f94-afb1-c74f14de51ee.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (134, 'ad9633af46594e6faa6af98b894b816d', '汰渍净白去渍洗衣粉1.36g', 61, '汰渍净白去渍洗衣粉1.36kg', '<p><img alt=\"\" src=\"/commodity/aaf5106e-58a5-4eed-8fb4-c00da8ce3506.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/53018e84-a948-42ac-bc80-7010fef52862.jpg', NULL, '2023-03-30 17:23:36', NULL);
INSERT INTO `goods` VALUES (135, '03690d93b1c849ff83651315a36b4389', '误闯侏罗纪大冒险', 73, '1误闯侏罗纪大冒险我的第一本探险漫画书', '<p><img alt=\"\" src=\"/commodity/69120000-663d-4071-84c7-99c69d95a6d4.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/b3838b01-c06a-4e2c-9b15-d6b801d70015.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (136, 'c54ff1fa8f8443e789226f2e287a36ce', '荷兰寻宝记', 77, '16荷兰寻宝记我的第一本历史探险漫画书', '<p><img alt=\"\" src=\"/commodity/99032473-0262-4231-94ae-f899ca1abf72.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/bf419bb2-0e48-4aaf-9496-efcc1fdba1a7.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (137, 'dcfd16649e6046819bc5b921655e8932', '心中有座独木桥', 77, '心中有座独木桥皮皮鲁总动员皮皮鲁讲堂', '<p><img alt=\"\" src=\"/commodity/2335aef4-caef-4d3b-ab67-0905a96452dc.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/2462351d-c1ea-4fdf-8b45-d57e4771d543.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (138, '81bf2df010f948c4ace9bf50c06139db', '漫画弟子规', 77, '漫画弟子规', '<p><img alt=\"\" src=\"/commodity/a630aa46-7169-4069-a54c-703824ba161f.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/6d690e10-83ba-41ea-9fcb-aa1689f67471.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (139, '62346c8600b74dfa859501a08612997e', '漫画中国成语', 77, '漫画中国成语1', '<p><img alt=\"\" src=\"/commodity/6cac21ca-bc20-48db-9dd9-a0a1eafbedbd.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/701dfbec-6b77-49cf-855f-8b904703cc5f.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (140, '235102a6cbdb4360a81c130efcdf3164', '老鼠记者新译本', 77, '老鼠记者新译本第1季盒装（共5册1-5）', '<p><img alt=\"\" src=\"/commodity/259b9100-be52-4760-ae59-903a199c98e4.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/6d69e891-cc51-42c9-8665-8d68d58ca158.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (141, '6fbd6fdf20e9481481f755d5868df2db', '漫画孝经', 77, '漫画孝经', '<p><img alt=\"\" src=\"/commodity/058eecfd-7222-4eb1-aa93-0fd927f07b19.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/d3ff9166-9e0e-4b4f-b4c2-227ac4f365bc.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (142, '16984d51e4eb4f7480f38046163e5441', '吃噩梦的小精灵', 77, '吃噩梦的小精灵恩德作品绘本系列', '<p><img alt=\"\" src=\"/commodity/a5edbde7-d39b-4489-9961-8adeabdec9e3.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/5369d3da-f829-44a9-8295-4a37cfb9f5ec.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (143, 'f40d936790dd4ee4925216e8f8e66c32', '上海寻宝记', 77, '1.上海寻宝记我的第一本大中华寻宝漫画书', '<p><img alt=\"\" src=\"/commodity/13185f85-733b-4bcd-9140-73292d2e6632.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/1dcb48e3-9f2d-4223-97f5-5e9107780f8a.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (144, 'a15c8e814493432398ded321cce8e16b', '在那小小的池塘里', 77, '在那小小的池塘里爱的催眠系列麦克米伦世纪', '<p><img alt=\"\" src=\"/commodity/867c662e-79c9-4b68-9c87-01d1b9d1b2cc.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/2b445e7e-5dd3-4e4d-9bd5-d033c89b2ded.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (145, 'c273a02ab88c411bbc2d25f02ac79a6c', '天使的翅膀', 77, '天使的翅膀好孩子成长故事分享', '<p><img alt=\"\" src=\"/commodity/00da0d50-0d69-406c-ba5a-0895b1b5aa07.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/5f382178-988d-4ddf-8a40-73597e80bd78.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (146, 'ad30d3aa49954d70811fd9fb746a63a3', '幻想大王奇遇记非常插班生', 77, '幻想大王奇遇记6非常插班生', '<p><img alt=\"\" src=\"/commodity/b7b310c5-3458-434d-8133-6fc513c2a15a.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/f12a10de-7d1e-435f-bdb3-e2cd1a933de6.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (147, 'a5f28fb59d8d4acf899aad74b98be51d', '幼学琼林', 77, '新课标小学国学文库幼学琼林彩图本', '<p><img alt=\"\" src=\"/commodity/1c911885-b60c-41b7-b77e-25fc381364cf.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/b9472be9-ace6-4c63-8c5a-345ec5a97a06.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (148, 'ef7292010dea4c2fbc1ddecb7c7fda09', '神隐大陆', 73, '神隐大陆②蓝湖的水下世界', '<p><img alt=\"\" src=\"/commodity/ed4a1edc-f135-40ea-839c-83e9c2cd373a.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/70e0b532-ddcb-4922-97b0-33546980486f.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (149, 'f5de4558b2e2477489856dfeb29ec445', '科学家故事100个', 73, '科学家故事100个', '<p><img alt=\"\" src=\"/commodity/e96c2ae5-d07a-4e0a-97e5-5a62b30f1b12.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/6009ebf2-7935-4cd3-98f6-4046a8060a24.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (150, '932ef51a35b842b9b443c9123c6c344a', '科学实验王', 73, '科学实验王8基因与遗传我的第一本科学漫画书', '<p><img alt=\"\" src=\"/commodity/e1b7e0f7-d5ed-4951-8daa-945fdf970d1a.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/8fd0ad42-13e6-4c13-92bc-b2fe8c4eb119.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (151, '8ed404859d2d4fc89da2c20221ce292d', '幼儿神奇贴纸', 73, '幼儿神奇贴纸升级版-IQ基础篇', '<p><img alt=\"\" src=\"/commodity/9473be85-5aa3-4e90-a6e0-ab3894933824.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/7ad1444f-0395-43a0-84fb-9d18c8a964ad.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (152, '21fdbab24ce44787a80bebd15a8ad1cf', '亚马逊丛林历险记', 73, '4亚马逊丛林历险记我的第一本科学漫画书绝境生存系列', '<p><img alt=\"\" src=\"/commodity/3b7902bb-c629-450b-958b-41acf213190a.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/d117c87f-35a0-4baf-b1b7-1448d278d28d.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (153, '08e9e5efa26c47a5a8f51c1f9208a978', '唐代诗苑揽胜', 78, '唐代诗苑揽胜——诗词背后的故事', '<p><img alt=\"\" src=\"/commodity/9570d1d7-5c48-40c4-b217-ccaf7963a3a3.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/4fe597b4-0f3c-4c4d-9744-2687626c5039.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (154, '452af6779cc540b4bfdc679a45a05ba5', '中华成语千句文（注释版）', 78, '中华成语千句文（注释版）', '<p><img alt=\"\" src=\"/commodity/6a71a97a-8ba1-4745-bd66-7d201901b09d.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/08c83569-0ed2-4813-8de1-6afcae8ddb94.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (155, '94ecfd156cfb4b969c47c87b5ab6cea7', '不老泉文库3时代广场的蟋蟀', 78, '不老泉文库3时代广场的蟋蟀麦克米伦世纪', '<p><img alt=\"\" src=\"/commodity/e8d78295-ea2e-44ee-8c76-bd737c9b6503.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/d39f8286-47d7-4235-940f-ed7fadd4f6f3.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (156, 'f99e482d695f493291b0238311893ba4', '野人的第三只眼睛', 78, '1.野人的第三只眼睛安瑟十三系列', '<p><img alt=\"\" src=\"/commodity/1dc82308-4421-43de-923f-46b2f3a03136.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/18daf79a-12b1-4c67-8e65-1c5b9f4e9451.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (157, 'e020ee8bf7d94a5f8f4739a85e2c116d', '大发明家达芬奇', 73, '大发明家达芬奇大发明家系列麦克米伦世纪', '<p><img alt=\"\" src=\"/commodity/3fb2ab13-772c-459b-851d-f843630c584b.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/d04f665e-4bb4-482f-8afb-c88074983044.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (158, 'c36861dc5ce443f3addde97a827baf5e', '谁为奶奶哭泣曹文轩小说馆', 78, '谁为奶奶哭泣曹文轩小说馆', '<p><img alt=\"\" src=\"/commodity/932477c3-6361-4c93-9f79-5b67d9cf4096.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/d1c72198-8523-43a7-bcb8-ba8666f1a650.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (159, '7f0743fffc10486e995f27d869360772', '埃及金字塔历险记', 73, '古文明大揭秘4埃及金字塔历险记2我的第一本科学漫画书', '<p><img alt=\"\" src=\"/commodity/b7fd068f-14e4-4299-8f16-42c6f85556c8.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/c8229c2a-718f-48b7-91ce-e28e72775cd0.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (160, 'b7b5b0f4c42341fe9a58c9ebbea02283', '我为自己点个赞', 78, '非常成长书我为自己点个赞--快快长大的28个成长密码', '<p><img alt=\"\" src=\"/commodity/79f7c958-2823-4c87-b22a-99a25e3086ff.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/165ac9b3-fed2-48b7-b00e-8801ae1cb7b5.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (161, '7ff47f4b0bdd46a88b3f74880c88e57a', '数字世界历险记', 73, '数学世界历险记5.黑暗中的怪物我的第一本科学漫画书', '<p><img alt=\"\" src=\"/commodity/90f8cd5a-3a12-445c-b8f8-d0d8f001fe92.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/319d9b22-dfec-445e-a754-47bdc31c8f35.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (162, '77a058537afe49f7b555cc0f38817d3d', '汉字童话总动员6自投罗网', 75, '汉字童话总动员6自投怪网', '<p><img alt=\"\" src=\"/commodity/47dacb56-6809-4564-9d60-42b5198d2d56.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/7a1feece-9a98-4155-8774-d038b124e4fd.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (163, '72b4dbed72eb408f91e621768792a84f', '章鱼国小时代学霸归来', 75, '章鱼国小时代1学霸归来', '<p><img alt=\"\" src=\"/commodity/5a4009f0-aa3d-413b-ba94-9bff22df0f21.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/743e3060-2721-45f3-8991-4b802fda0113.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (164, 'f59bb7ca872643848eb36a0e6542fe14', '长袖毛线衫CA1539C99M23-1', 87, '长袖毛线衫CA1539C99M23-1', '<p><img alt=\"\" src=\"/commodity/45400815-0333-475f-8922-ea4aa386537e.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/7bc94401-8071-4ee1-83ac-6b5a3fcd1026.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/d9b8e421-cdb5-4913-b8a2-a8542d52ddf8.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/2afbfdeb-e0ec-4f69-b49c-40680228a95d.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/55731164-d27b-4cb4-a1d2-0b3c03ee6f37.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (165, 'ecd53aa96b3343c9bc1c92bd0e4f4b9b', '女长衬衫CB1503C66M03-1', 86, '女长衬衫CB1503C66M03-1', '<p><img alt=\"\" src=\"/commodity/51e91c28-be4d-4d5d-962d-d51cdc0386b8.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/f6649333-ec70-4743-92dd-7adf541e585f.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/caa7ca5a-2b00-4696-bedd-67af77ae46f8.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/52f7eb6c-ad19-4d15-895a-2caa5e3da4e1.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (166, 'c4d1d09b7e5843288e1a8ec465acfc30', '长袖衬衫CA1528C55M23-1', 86, '长袖衬衫CA1528C55M23-1', '<p><img alt=\"\" src=\"/commodity/1af47152-a326-47a1-b627-6f6b0f7400cb.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/8fd5d67d-d6f6-47de-84ec-9fcaeb8428fa.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/4fcce4c9-5a96-4446-b666-a228e18e6e02.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/7808d4c3-435e-4efe-bf25-1a2d059cd8ad.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (167, '9db13050033f45a697e0e3ee0fecd86c', '女西裤CB1528NC55M30-2', 92, '女西裤CB1528NC55M30-2', '<p><img alt=\"\" src=\"/commodity/079e0625-fa57-4712-bfb9-bbe7c98ded3f.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/b8be4933-1dbb-4841-b351-c96b575707e4.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/aad7fd3f-1f9c-4272-8399-bdf4b34b5f12.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/66e6c475-f810-46d5-945d-c5415c53ef26.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (168, '4e6313c62aeb467cba6d4de70db9a3d6', '女长裤青CB1528C55M25-2', 92, '女长裤青CB1528C55M25-2', '<p><img alt=\"\" src=\"/commodity/3b3e32a6-5452-4f81-9ebd-e7564c936457.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/2fc4202b-dc85-4a68-bdf1-de46eefa69f2.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/18f65e1c-d109-477a-b84e-dd9313af088b.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/147710ba-520b-4c83-9083-110d491cdf19.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (169, 'f0a65a0c8dc04b5b850ed7835ce11088', '女西服CB1527NC55M30-1', 88, '女西服CB1527NC55M30-1', '<p><img alt=\"\" src=\"/commodity/10ec67fc-6fb2-44e9-a59a-2d11dac7f64c.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/cf341006-0bbe-4758-a183-9de5135a5e7e.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/fd4a34ff-b389-404f-be67-cdadfc12590d.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/0c5b46a3-faa1-479c-b446-12f8dcdd659a.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (170, '3da4fed1ddf94183a60558d0bcced790', '女西服CB1513NC55M30-1', 88, '女西服CB1513NC55M30-1', '<p><img alt=\"\" src=\"/commodity/8e9dff82-e4aa-42ce-ac8f-eed6cdb68ee5.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/e1f9525c-e282-473a-97b2-8b113408c257.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/693d032b-8786-493a-beaa-515a489ee4a7.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/bb15cdd8-f73f-4f39-b03f-f1a0035e0c0f.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/1d30ff23-6b1f-4515-856a-308e63098a8f.jpg', NULL, '2023-03-30 17:23:37', NULL);
INSERT INTO `goods` VALUES (171, '40f42ffefb914adcaadf4d7596df1665', '女长裤卡CB1511C33M25-2', 92, '女长裤卡CB1511C33M25-2', '<p><img alt=\"\" src=\"/commodity/4a8db291-bf1c-414c-b11e-afde0ad80060.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/08183bf1-5c7f-4d23-aa45-d359f0091af5.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/70bc945f-4661-4822-a6d1-a9136358ed05.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/15b6556f-0339-43c1-aa66-83e9dee30a9c.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/54749c40-8ef8-49b4-8689-f3a0f76e7bbb.jpg', NULL, '2023-03-30 17:23:38', NULL);
INSERT INTO `goods` VALUES (172, '4c346154c2c94e7ba7186448f51553a1', '女长衬衫CB1511C00M27-1', 86, '女长衬衫CB1511C00M27-1', '<p><img alt=\"\" src=\"/commodity/27121924-c4b3-4711-acaf-7f6338ed97d1.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/5670aa9e-39ab-47b0-a5cc-f82fbe6d3361.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/799547c0-e146-43df-8b5d-269cb690d4d3.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/479f3ab8-7be2-4184-b7ea-3fe63d7fe962.jpg', NULL, '2023-03-30 17:23:38', NULL);
INSERT INTO `goods` VALUES (173, '12cb06f4fa1948078636a50717f743fe', '男长衬衫CA1511C00M27-1', 86, '男长衬衫CA1511C00M27-1', '<p><img alt=\"\" src=\"/commodity/34303663-6d06-4c72-89f2-3f64bba3ed81.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/d878e085-e821-4387-a5a2-7626e4123a26.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/7e6f3607-1d8c-4b50-8505-230d361f6542.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/8f5fc0e5-c574-43e6-88e7-f39c650b6725.jpg', NULL, '2023-03-30 17:23:38', NULL);
INSERT INTO `goods` VALUES (174, '4fbefb6948ae4ef58cc827762575656e', '冬装大衣DA1502NC14M33-1', 89, '冬装大衣DA1502NC14M33-1', '<p><img alt=\"\" src=\"/commodity/cbe68b4d-3f87-4cfb-952b-4f91cac77c0e.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/c29c7cb6-fc36-4a0a-bbf8-343e9a1675de.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/b0ac6939-62f7-4844-913c-b2a640df79c6.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/c4a5c612-ba0f-416d-9398-3edc04598cfa.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/8c63f06a-7b09-4d24-801a-b23c6e7cd999.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/74413a9e-f4b1-4575-85ec-fd4b53c5e876.jpg', NULL, '2023-03-30 17:23:38', NULL);
INSERT INTO `goods` VALUES (175, 'f53536c6bb2c42f6b931569b4df916b9', '运动装', 85, '运动装', '<p><img alt=\"\" src=\"/commodity/34d91c40-f701-40f0-9a45-81bff45beb09.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/0673f0e3-b2ee-4db4-97eb-4c7318dff943.jpg', NULL, '2023-03-30 17:23:38', NULL);
INSERT INTO `goods` VALUES (176, '8e4ca6d93f6e4382aa3eff8838ca9543', '男西服CA1513NC55M30-1', 88, '男西服CA1513NC55M30-1', '<p><img alt=\"\" src=\"/commodity/f3e09ad0-5bf5-4cf4-a1c7-c1daa05b3bfc.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/60a4176e-8221-4ced-8c0c-bdb20945595b.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/cb838e99-eb0c-4de7-920d-307dceb9bdd0.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/e422d698-993c-46b9-a700-91be385e29ac.jpg', NULL, '2023-03-30 17:23:38', NULL);
INSERT INTO `goods` VALUES (177, 'dfad8b082aeb4edbaf1400d83c8b0af1', '男西服CA1527NC55M30-1', 88, '男西服CA1527NC55M30-1', '<p><img alt=\"\" src=\"/commodity/7f75f1e1-940c-44d7-b5db-687205b0a080.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/0aabfb2e-4fc7-44fc-9018-210e419f083c.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/d0b364ce-faf9-4e29-baca-c6b55b64584a.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/465be096-b1f5-41b8-ba81-a68c21ace7d0.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/27473d65-9a46-4fdc-8012-5dc9f4aef89b.jpg', NULL, '2023-03-30 17:23:38', NULL);
INSERT INTO `goods` VALUES (178, '7ce97d9f1f974916856c33b0788fc0a5', '男长裤青CA1528C55M25-2', 88, '男长裤青CA1528C55M25-2', '<p><img alt=\"\" src=\"/commodity/1fd0163e-4ebc-46d2-bfe2-5d43fa331dcd.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/8178852c-a2c6-4600-94b1-a84e30e9e326.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/f9f84825-e2b5-4778-b46d-59d915b72ee0.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/f47ce8cd-7848-45ca-84d0-4f7db88e0eb6.jpg', NULL, '2023-03-30 17:23:38', NULL);
INSERT INTO `goods` VALUES (179, '56385112f1164bdd84b0f8b048ae0053', '男长衬衫A1503C66M03-1', 86, '男长衬衫A1503C66M03-1', '<p><img alt=\"\" src=\"/commodity/b60d1912-bb97-4638-8186-a31233608f05.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/d7f00708-0125-4beb-bf17-c613211e4252.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/e3b7926b-e9eb-4c93-8145-9195338ba30e.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/2f285af8-5a46-43cc-aa9a-56cfb9eac72f.jpg', NULL, '2023-03-30 17:23:38', NULL);
INSERT INTO `goods` VALUES (180, 'e32ee11e1fb94d1092dae98f217056ee', '男西裤CA1528NC55M30-2', 90, '男西裤CA1528NC55M30-2', '<p><img alt=\"\" src=\"/commodity/75cb68c5-02b2-4e1d-a5cf-99376ea8ea34.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/d7e1eb16-9fb3-4321-87dc-d4cf2e8a7d73.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/a809a413-1e46-4292-8deb-cce5ecf9c165.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/fa2fcb67-e153-411b-b19e-f125b725cd9f.jpg', NULL, '2023-03-30 17:23:38', NULL);
INSERT INTO `goods` VALUES (181, '705bbf9e904f46d782e77d4f0425df23', '男长裤卡CA1511C33M25-2', 91, '男长裤卡CA1511C33M25-2', '<p><img alt=\"\" src=\"/commodity/21cfce13-d29a-4dcc-afae-2b7b2d9b215f.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/63ec3c89-a3ee-42c8-a801-c2205ffb7cd9.jpg\" style=\"width:100%\" /><img alt=\"\" src=\"/commodity/a113e57e-13a3-498a-99ce-34d4ca90ea42.jpg\" style=\"width:100%\" /></p>\n', 1, '/commodity/fbdc19e8-bee3-44e3-854a-9c6dd0af6f54.jpg', NULL, '2023-03-30 17:23:38', NULL);
INSERT INTO `goods` VALUES (182, '834b81d55f404723ba2ea0629d92d0c6', '耳机', NULL, '旺哥唇动30g', '', 1, '/commodity/8e59df0e-5b6f-4562-b457-2eca3eefff72.jpg', NULL, '2023-03-30 17:23:38', NULL);
INSERT INTO `goods` VALUES (183, 'aebbb174db5240c29a26534c96994e63', '天使的翅膀', NULL, '联营爆米花3元', '', 1, '/commodity/5f382178-988d-4ddf-8a40-73597e80bd78.jpg', NULL, '2023-03-30 17:23:38', NULL);
INSERT INTO `goods` VALUES (184, 'db5f0db8275e480db4b9e9445f7c8a02', '三叶草ZX750', NULL, '三叶草ZX750jieshao', '<p>三叶草。。。。。。。。。。。。。。。。。</p>\n', 1, '/commodity/07ce504e-2d58-451d-844b-578bf0fbb107.jpg', NULL, '2023-03-30 17:23:38', NULL);
INSERT INTO `goods` VALUES (185, '8c5518d3fba94d6a8d42554efedad955', '三叶草单衣', NULL, '三叶草单衣', NULL, 1, '/commodity/e5ddbbbf-a649-46c8-89fd-daae345a9ff2.jpg', NULL, '2023-03-30 17:23:38', NULL);
INSERT INTO `goods` VALUES (199, '1c0a017886bd49b9b81cc28660a0767a', '123', 112, '123', '<p>123</p><p><img src=\"/goods/5ce7d98403704efd8f3b0ed8e9e3e20c.jpg\" alt=\"\" data-href=\"\" style=\"\"/></p>', 1, '/goods/d48635797c774d63bf8898133fafd9e1.jpg', 11.00, '2023-04-21 16:11:38', '2023-04-27 11:03:27');

-- ----------------------------
-- Table structure for goods_category
-- ----------------------------
DROP TABLE IF EXISTS `goods_category`;
CREATE TABLE `goods_category`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '商品类型id',
  `category_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品类型名称',
  `category_type` int(2) NULL DEFAULT NULL COMMENT '类型级别 1 一级  2二级  默认一级',
  `parent_id` int(2) NULL DEFAULT NULL COMMENT '上一级id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 113 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of goods_category
-- ----------------------------
INSERT INTO `goods_category` VALUES (1, '学习装备', 1, 0, '2023-04-19 13:37:46', '2023-04-21 16:10:38');
INSERT INTO `goods_category` VALUES (2, '营养膳食', 1, 0, '2023-04-19 13:37:46', '2023-04-21 10:07:09');
INSERT INTO `goods_category` VALUES (3, '学习攻坚', 1, 0, '2023-04-19 13:37:46', '2023-04-21 15:57:31');
INSERT INTO `goods_category` VALUES (4, '营养早餐', 2, 2, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (5, '精品午餐', 2, 2, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (6, '健康晚餐', 2, 2, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (7, '圆珠笔', 2, 1, '2023-04-19 13:37:46', '2023-05-16 15:30:40');
INSERT INTO `goods_category` VALUES (8, '橡皮', 2, 1, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (9, '语文材料', 2, 3, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (10, '数学材料', 2, 3, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (11, '橡皮', 3, 8, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (12, '圆珠笔', 3, 7, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (13, '小学语文教材', 3, 9, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (14, '初中语文教材', 3, 9, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (15, '小学数学教材', 3, 10, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (16, '初中数学教材', 3, 10, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (17, '香肠', 3, 5, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (18, '蛋糕', 3, 5, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (19, '鸡蛋', 3, 5, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (20, '烧鸡', 3, 6, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (21, '鱼翅', 3, 6, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (22, '大学数学教材', 3, 10, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (23, '拉面', 3, 6, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (24, '教育培训', 1, NULL, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (25, '健康五谷', 1, NULL, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (26, '特产干货', 2, 25, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (27, '食用油', 2, 25, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (28, '米面', 2, 25, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (29, '葡萄干', 3, 26, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (30, '白莲', 3, 26, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (31, '墨鱼', 3, 26, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (32, '菌菇', 3, 26, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (33, '桂圆', 3, 26, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (34, '银耳', 3, 26, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (35, '腐竹', 3, 26, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (36, '枣类', 3, 26, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (37, '茶油', 3, 27, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (38, '葵花油', 3, 27, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (39, '调和油', 3, 27, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (40, '菜籽油', 3, 27, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (41, '玉米油', 3, 27, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (42, '米', 3, 28, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (43, '木耳', 3, 26, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (44, '精致生活', 1, NULL, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (45, '洗发护肤', 2, 44, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (46, '洗发水', 3, 45, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (47, '护发素', 3, 45, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (48, '沐浴露', 3, 45, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (49, '香皂', 3, 45, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (50, '女性护理', 2, 44, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (51, '面部护肤', 2, 44, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (52, '口腔护理', 2, 44, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (53, '洗涤用品', 2, 67, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (54, '身体护理', 2, 44, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (55, '卫生巾', 3, 50, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (56, '护垫', 3, 50, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (57, '洗面奶', 3, 51, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (58, '洁面乳', 3, 51, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (59, '牙刷', 3, 52, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (60, '牙膏', 3, 52, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (61, '洗衣粉', 3, 53, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (62, '洗洁精', 3, 53, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (63, '洗手液', 3, 53, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (64, '洗衣液', 3, 53, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (65, '洗衣皂', 3, 53, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (66, '花露水', 3, 54, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (67, '家居保卫', 1, NULL, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (68, '纸巾', 2, 67, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (69, '卷纸', 3, 68, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (70, '手帕纸', 3, 68, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (71, '湿巾', 3, 68, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (72, '学习攻坚', 1, NULL, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (73, '科普漫画', 2, 72, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (74, '儿童文学', 2, 72, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (75, '启蒙益智', 2, 72, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (76, '少儿百科', 3, 73, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (77, '童话故事', 3, 74, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (78, '成长认知', 3, 75, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (79, '粗粮', 2, 25, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (80, '绿豆', 3, 79, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (81, '校服文化', 1, NULL, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (82, '上装', 2, 81, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (83, '下装', 2, 81, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (84, '套装', 2, 81, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (85, '运动衫', 3, 84, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (86, '长袖衬衫', 3, 82, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (87, '长袖毛线衫', 3, 82, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (88, '西装上衣', 3, 82, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (89, '呢子大衣', 3, 82, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (90, '西装下装', 3, 83, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (91, '男长裤', 3, 83, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (92, '女长裤', 3, 83, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (93, '天天特价', 1, NULL, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (94, '断码清仓', 2, 93, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (95, '三叶草', 3, 94, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (96, '牛奶', 3, 4, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (97, '酸奶', 3, 4, '2023-04-19 13:37:46', NULL);
INSERT INTO `goods_category` VALUES (108, '测试类别', 1, 0, '2023-04-21 15:47:25', '2023-04-21 15:57:42');
INSERT INTO `goods_category` VALUES (110, '测试1', 2, 108, '2023-04-21 16:04:25', '2023-04-21 16:11:04');
INSERT INTO `goods_category` VALUES (112, '测试2', 3, 110, '2023-04-21 16:11:15', NULL);

-- ----------------------------
-- Table structure for goods_favorites
-- ----------------------------
DROP TABLE IF EXISTS `goods_favorites`;
CREATE TABLE `goods_favorites`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '收藏id',
  `user_id` int(50) NULL DEFAULT NULL COMMENT '用户id',
  `goods_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品编号',
  `store_no` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '门店编号',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_favorites_idx`(`user_id`) USING BTREE,
  INDEX `goods_favorites_idx`(`goods_no`) USING BTREE,
  INDEX `store_favorites_idx`(`store_no`) USING BTREE,
  CONSTRAINT `goods_favorites` FOREIGN KEY (`goods_no`) REFERENCES `goods` (`goods_no`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `store_favorites` FOREIGN KEY (`store_no`) REFERENCES `store` (`store_no`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user_favorites` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of goods_favorites
-- ----------------------------
INSERT INTO `goods_favorites` VALUES (2, 1, '17e3a399c6c9441c94603356b410e1f9', NULL, '2023-03-24 13:53:24');
INSERT INTO `goods_favorites` VALUES (6, 1, '940b6061d34a45f69d3ae7d1e5c8d925', NULL, '2023-04-04 13:23:09');
INSERT INTO `goods_favorites` VALUES (7, 1, 'fd45827cb8da46158d3074045b34beb3', NULL, '2023-04-04 13:23:12');
INSERT INTO `goods_favorites` VALUES (8, 1, 'd69b08093ecb46ed9f94e59a7a1e5246', NULL, '2023-04-04 13:23:20');
INSERT INTO `goods_favorites` VALUES (9, 1, 'e65f694df46a4937b94f25115ec02f83', NULL, '2023-04-04 13:23:26');
INSERT INTO `goods_favorites` VALUES (10, 1, 'a456157078c9493bb1463f7b572ea06f', NULL, '2023-04-04 13:23:31');
INSERT INTO `goods_favorites` VALUES (11, 1, '24da267a6b4d43af835a05fd9e3d8404', NULL, '2023-04-04 13:23:34');
INSERT INTO `goods_favorites` VALUES (12, 1, 'fd37d1bf188f44a588efa1761b47ae2d', NULL, '2023-04-04 13:23:37');
INSERT INTO `goods_favorites` VALUES (13, 1, '738a0e245d6e47f9b6501bc6e0a5c0f4', NULL, '2023-04-04 13:23:40');
INSERT INTO `goods_favorites` VALUES (14, 1, '737a730e730d4882830af119a29537bb', NULL, '2023-04-04 13:23:46');
INSERT INTO `goods_favorites` VALUES (15, 1, 'f6197fe4652a4ed09786dbd94da2a0af', NULL, '2023-05-15 08:54:55');

-- ----------------------------
-- Table structure for goods_picture
-- ----------------------------
DROP TABLE IF EXISTS `goods_picture`;
CREATE TABLE `goods_picture`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '商品图片',
  `goods_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品编号',
  `picture_url` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图片url',
  `picture_type` int(20) NULL DEFAULT NULL COMMENT '图片类型',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `goods_goodspicture_idx`(`goods_no`) USING BTREE,
  CONSTRAINT `goods_goods_picture` FOREIGN KEY (`goods_no`) REFERENCES `goods` (`goods_no`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 509 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of goods_picture
-- ----------------------------
INSERT INTO `goods_picture` VALUES (3, '292a51053b1749b9bb29825b05c393fa', '/commodity/49fd9238-b9d2-4aac-bdc4-fbad824841e7.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (6, 'f56f34efb86c4db4b3eda8e1f60b2643', '/commodity/0df74503-b6cf-4451-bd83-d715a6f2649f.png', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (8, 'c126c02616484a648f0370ff2b87aea2', '/commodity/6946881700056-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (9, '3aa8cd13da714e6dabad76f816163675', '/commodity/6946881700377-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (10, '3aa8cd13da714e6dabad76f816163675', '/commodity/f30a53e0-c650-4d96-8db9-1460c14af532.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (11, 'a12668cc124649d1b94cfe3f53a81bf9', '/commodity/b29d3b31-dab2-4275-b207-10764950d431.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (12, 'b909af4bd0d34ad49416949a88cfe1c7', '/commodity/6946881700131-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (13, '871d4dea9dd7406e89cf224bd36f66a8', '/commodity/6946881700650-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (14, '871d4dea9dd7406e89cf224bd36f66a8', '/commodity/6946881700650-2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (15, '361486a0c9eb43df8eda4e42daa8ee61', '/commodity/4f2cab0c-caf5-46db-bdb4-a03e34b44e32.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (16, '361486a0c9eb43df8eda4e42daa8ee61', '/commodity/91027c4b-4ad6-45ca-add8-d3a4792c3767.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (17, '2b96c721ddb047dea614533c9914413c', '/commodity/6946881700735-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (18, '2b96c721ddb047dea614533c9914413c', '/commodity/6946881700735-2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (19, 'f22f36c8b322410f90af7387c1d4550d', '/commodity/6946881700766-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (20, 'f22f36c8b322410f90af7387c1d4550d', '/commodity/6946881700766-2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (21, '7b08bfbf7b1943df879634b3bf6d7cf1', '/commodity/38f4ade2-8b1e-4586-9025-5cfacd1378af.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (22, '7b08bfbf7b1943df879634b3bf6d7cf1', '/commodity/b3bfc29b-db52-4cc2-85a7-a2b22562a6b0.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (23, '7b08bfbf7b1943df879634b3bf6d7cf1', '/commodity/a0bb2181-4162-4c3e-85f3-4016a1227b99.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (24, '7b08bfbf7b1943df879634b3bf6d7cf1', '/commodity/2e1b6a8f-3b2e-4c0b-94c1-b6430a4fed39.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (25, '492970f5f780430e9351ded7bf1a80c7', '/commodity/9dc5afbe-0abc-43c4-925b-91b12b7b1786.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (26, '1e0752b8c9ad42479fcebc9bbfd1f872', '/commodity/4ec7d06a-e402-4840-8ce5-061df20c4fc2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (27, 'ce0dd5e0d77a47c48f37301fcabf06c5', '/commodity/523e0b84-111f-4251-9cd8-29fff7fec733.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (28, 'f611ef02dd2d40eaa8b8447b90722553', '/commodity/546a6227-22a6-448a-a614-7fa9491c4a18.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (29, '415077ea72fe486fb139d345a7c4a800', '/commodity/046184df-9547-4472-a663-eed83598e0b1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (30, '415077ea72fe486fb139d345a7c4a800', '/commodity/2c38796d-2f74-45e6-8f00-2e771d660785.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (31, 'd280ae925eba42a99bb988da213c7a88', '/commodity/9e0a571b-5a91-4627-b502-20eb40d95d9f.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (32, 'd280ae925eba42a99bb988da213c7a88', '/commodity/ce5a9a40-2404-40ca-9482-ba1a729b562d.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (33, 'd280ae925eba42a99bb988da213c7a88', '/commodity/d6bbffd2-0fed-42de-a581-3b9656a39cab.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (34, 'efc240af98ba46e4a5d46c4e47c2c323', '/commodity/c7edcee5-79f8-459f-be60-2be44ed28220.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (35, 'efc240af98ba46e4a5d46c4e47c2c323', '/commodity/f44e5dfe-955c-4e7c-87e6-98d2f6cd01f8.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (36, 'c96757e31c1a4969be08070434393a29', '/commodity/6909931502116-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (37, 'c96757e31c1a4969be08070434393a29', '/commodity/6909931502116-2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (38, 'c96757e31c1a4969be08070434393a29', '/commodity/6909931502116-3.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (39, 'c96757e31c1a4969be08070434393a29', '/commodity/6909931502116-4.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (40, 'd5e13afe86564d1d8663b3028b1488fa', '/commodity/6941499102735-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (41, 'd5e13afe86564d1d8663b3028b1488fa', '/commodity/6941499102735-2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (42, 'd5e13afe86564d1d8663b3028b1488fa', '/commodity/6941499102735-3.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (43, '012da18c4d34427e8deadcaf12b285f4', '/commodity/f501530f-d007-469b-898f-340d6e67cbda.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (44, 'd3e3b54f636d4f18bbf74741677ce2bb', '/commodity/6944910322125-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (45, 'd3e3b54f636d4f18bbf74741677ce2bb', '/commodity/6944910322125-2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (46, '982a87f2d5d747cda74bab30fd00c9cd', '/commodity/cb3bd49b-83f8-4577-86e5-37c08d846c71.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (47, 'f0d6b7c1b8064bdea70a67c5f0cae685', '/commodity/6944910322668-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (48, 'f0d6b7c1b8064bdea70a67c5f0cae685', '/commodity/6944910322668-2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (49, '7d42f802c8b34970acf5aff3bc2a79d3', '/commodity/deeb1926-b425-4137-b2d5-a142abf0e6f6.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (50, '7d42f802c8b34970acf5aff3bc2a79d3', '/commodity/13b987fd-29ca-4360-afc0-e8e6b76f28ce.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (51, '7d42f802c8b34970acf5aff3bc2a79d3', '/commodity/b3fd7162-206a-4168-8a5e-e0192905611e.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (52, '7d42f802c8b34970acf5aff3bc2a79d3', '/commodity/eb31f615-2cfa-47c3-a202-89bd2d5e5f2b.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (53, '592e437d264a4dba966e2e68a52ae088', '/commodity/4dcc3847-bae7-4f31-8f42-c4e9ca4dc22c.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (54, '592e437d264a4dba966e2e68a52ae088', '/commodity/49e5f6ac-7943-43ba-babb-9f98c9f94840.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (55, '592e437d264a4dba966e2e68a52ae088', '/commodity/f96a62f6-510e-4b3c-8d52-616d9bb2c5f7.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (56, '592e437d264a4dba966e2e68a52ae088', '/commodity/c8c65d4d-9865-4806-9c58-bb0cbf5ff63e.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (57, '9b1eb99ccf5a41ba9facce8aa4278940', '/commodity/c00feed0-9fa2-4644-8fe6-d90e34b5cf7f.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (58, '9b1eb99ccf5a41ba9facce8aa4278940', '/commodity/b17c5e12-c8e2-4a6a-9c1d-a1b25d1ce324.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (59, '9b1eb99ccf5a41ba9facce8aa4278940', '/commodity/29fafdc0-5b9e-4f54-b5e9-627457286c1f.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (60, '9b1eb99ccf5a41ba9facce8aa4278940', '/commodity/e50509cc-38bf-4f1c-936c-b774878db82a.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (61, 'ab061af8673f4beb90669772f2d4a28e', '/commodity/6944910329308-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (62, 'ab061af8673f4beb90669772f2d4a28e', '/commodity/6944910329308-2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (63, 'ab061af8673f4beb90669772f2d4a28e', '/commodity/6944910329308-3.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (64, 'ab061af8673f4beb90669772f2d4a28e', '/commodity/6944910329308-4.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (65, '0cbba76f4cf0434da9aa7e4a14e884ea', '/commodity/6944910322101-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (66, '4c8ae591ecb340289b1330547be08fbd', '/commodity/6944910319200-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (67, '4c8ae591ecb340289b1330547be08fbd', '/commodity/6944910319200-2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (68, '4c8ae591ecb340289b1330547be08fbd', '/commodity/6944910319200-3.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (69, '4c8ae591ecb340289b1330547be08fbd', '/commodity/6944910319200-4.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (70, 'e486e0f6a32d4756b8104d5c1c124d16', '/commodity/44de955d-de66-4095-bb8b-fe4b4bdb8d06.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (71, '87ae1ecaeb424499a5ea95e427106ba5', '/commodity/6946881700469-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (72, '68c99eb3565f499890ebaf9545353438', '/commodity/6946881700360-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (73, '3b9c5bd0fd454ca4b7217d5c094b290f', '/commodity/6946881700285-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (74, '638d4d072f06458a8c9014f5ae31a68d', '/commodity/6946881700353-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (75, '638d4d072f06458a8c9014f5ae31a68d', '/commodity/6946881700353-2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (76, '95b1e97e6e64419ba5fade025c2ef00f', '/commodity/6946881700070-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (77, '95b1e97e6e64419ba5fade025c2ef00f', '/commodity/6946881700070-2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (78, 'd2ab314272804c5b98fd4276d04dc838', '/commodity/6946881700315-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (79, 'a21211eca70e43508f383175c86379b7', '/commodity/6946881700292-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (80, '33f9ea4aadad4bcb9c14e0883cbffd05', '/commodity/6946881700704-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (81, '7427dee2cce34900a5c83e8459ffe56b', '/commodity/6946881701046-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (82, '9bc484e25e18466a86489e068a246460', '/commodity/e796c98d-61ab-429d-8884-0c401dd1ae2d.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (83, '9bc484e25e18466a86489e068a246460', '/commodity/7d849c67-db4f-4298-a6c4-85383657be6e.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (84, '9bc484e25e18466a86489e068a246460', '/commodity/611daa11-d5ce-4491-b078-41590633f812.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (85, '9bc484e25e18466a86489e068a246460', '/commodity/012dc85e-f1e6-40ff-a6f9-e3fdd8db708b.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (86, 'a456157078c9493bb1463f7b572ea06f', '/commodity/ae24f66d-c29c-4f55-978b-86117dbf4dc6.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (87, 'a456157078c9493bb1463f7b572ea06f', '/commodity/38b0f2a6-04a2-422c-ac7d-31821e259be7.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (88, 'a456157078c9493bb1463f7b572ea06f', '/commodity/4945ea18-801f-45ef-ab10-7667d3cc1afa.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (89, 'a456157078c9493bb1463f7b572ea06f', '/commodity/3175fd22-95e6-4e9c-a0b4-d7e06e0fe428.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (90, 'e849f2c9d4174c6a8bc5b75424c16bcd', '/commodity/3beeeb6d-d86f-4c46-82b0-03c7c4f03fc1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (91, 'e849f2c9d4174c6a8bc5b75424c16bcd', '/commodity/88b975f2-1ef8-442b-b494-be6a93156251.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (92, 'e849f2c9d4174c6a8bc5b75424c16bcd', '/commodity/4623c9f7-22ca-4be3-a85e-6a258881025d.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (93, 'e849f2c9d4174c6a8bc5b75424c16bcd', '/commodity/bc10b995-535e-40c6-9642-fc77a0c77b9b.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (94, '24da267a6b4d43af835a05fd9e3d8404', '/commodity/fd75301f-dfc4-49d9-9597-abfe349129a8.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (95, '24da267a6b4d43af835a05fd9e3d8404', '/commodity/d1351fc6-f259-46ec-b189-a82cc2a674ee.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (96, '24da267a6b4d43af835a05fd9e3d8404', '/commodity/0b54aca5-baa4-4372-b5d1-2b535b7c9e9c.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (97, '24da267a6b4d43af835a05fd9e3d8404', '/commodity/a0536455-04e5-4c91-bdfd-d74db8d7bb16.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (98, 'ac2345f5ecb446e79a33155598079a32', '/commodity/b2065e78-2021-418e-9363-0a388488658d.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (99, 'ac2345f5ecb446e79a33155598079a32', '/commodity/e990b3ee-24f7-453e-b290-20a03ab62035.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (100, 'ac2345f5ecb446e79a33155598079a32', '/commodity/b0700839-52ca-478f-b505-d27ef909f127.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (101, 'ac2345f5ecb446e79a33155598079a32', '/commodity/fb276701-32e3-4b6d-bf89-d4cdee021ad7.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (102, 'b0a765e9e417457f9738be66fcdee3f2', '/commodity/7daa0824-c084-4b54-a247-3ed50eb99231.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (103, 'b0a765e9e417457f9738be66fcdee3f2', '/commodity/efa8be4b-2455-4c9b-ab62-d2ef9303b397.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (104, 'b0a765e9e417457f9738be66fcdee3f2', '/commodity/aa198e3d-aaaa-4389-b3f4-4617ba0439de.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (105, 'fd37d1bf188f44a588efa1761b47ae2d', '/commodity/76eb781e-cdab-489b-82c8-e0470c202df4.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (106, 'fd37d1bf188f44a588efa1761b47ae2d', '/commodity/905ee61b-a6ce-40ac-a2ff-e136dca212b2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (107, '738a0e245d6e47f9b6501bc6e0a5c0f4', '/commodity/2f3a1783-0823-4c0d-93ce-4e18fe0fa6be.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (108, '738a0e245d6e47f9b6501bc6e0a5c0f4', '/commodity/40021cb5-f626-444c-8041-a123b248acc0.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (109, '738a0e245d6e47f9b6501bc6e0a5c0f4', '/commodity/3bf602c0-909d-40d3-983c-69d9a9068659.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (110, 'f6197fe4652a4ed09786dbd94da2a0af', '/commodity/fcbf455a-6599-4889-a59e-1ff3a23d6ea3.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (111, 'f6197fe4652a4ed09786dbd94da2a0af', '/commodity/0ff1eab1-3258-484a-a3f2-d5c21aa20cc5.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (112, 'dbc9c69427b44e16bce77052b610c20b', '/commodity/06431c9e-2e16-426d-a833-f437c013bb7d.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (113, 'dbc9c69427b44e16bce77052b610c20b', '/commodity/004d7da1-9500-4f8a-875f-a397442509f5.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (114, 'dbc9c69427b44e16bce77052b610c20b', '/commodity/2c365e8a-e9bd-46aa-921e-ee4557e1a9ef.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (115, 'dbc9c69427b44e16bce77052b610c20b', '/commodity/518ebf41-d7b1-4660-94aa-64c9f19efb00.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (116, '737a730e730d4882830af119a29537bb', '/commodity/f2680bf0-8279-40a0-8b25-7fb88baa2513.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (117, '737a730e730d4882830af119a29537bb', '/commodity/900ad217-8d5a-4f23-8d76-94c300c8703c.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (118, '737a730e730d4882830af119a29537bb', '/commodity/d7676dd1-f043-4b73-a9c4-285de6faaaa2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (119, '737a730e730d4882830af119a29537bb', '/commodity/95a9e2c7-95ea-4333-930f-1913325efd6e.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (120, 'fa61586d51134267832c37ac94f11be8', '/commodity/b2e4ce7f-3e2b-4770-ab1f-40eb38496d7f.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (121, 'fa61586d51134267832c37ac94f11be8', '/commodity/ea3d3933-45d4-4051-9429-276cc4b91a16.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (122, 'b96d0b9010cb4ef69a027341785f0f09', '/commodity/a9564f22-d13f-4efa-8058-409d711fc859.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (123, 'b96d0b9010cb4ef69a027341785f0f09', '/commodity/ef8c52e2-59f9-4edc-8943-6d5532baf169.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (124, 'c096e1e874004c4eb39602fbbef5d477', '/commodity/64356ffa-187a-41cb-bab6-81243cae14c0.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (125, 'c096e1e874004c4eb39602fbbef5d477', '/commodity/1f102144-97e6-445f-8414-5a05f1394cce.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (126, 'c096e1e874004c4eb39602fbbef5d477', '/commodity/9df77dde-a057-44da-ba5a-2d301c43af04.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (127, 'c096e1e874004c4eb39602fbbef5d477', '/commodity/af724f89-736c-47c8-bb47-635c9aad46ff.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (128, '2e2ed6cd61904623817a822cb6426a05', '/commodity/13cdefe5-d752-4408-ac95-652bda16352d.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (129, '2e2ed6cd61904623817a822cb6426a05', '/commodity/83a26816-312d-4d1c-8f0e-e4321e676d75.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (130, '2e2ed6cd61904623817a822cb6426a05', '/commodity/3d7e5803-752b-4ff4-84a9-167fd55f9b50.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (131, '2e2ed6cd61904623817a822cb6426a05', '/commodity/081248a7-2e66-4133-94b6-58b8505a8ce6.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (132, '925eda52282b45deb9f74711a56261d1', '/commodity/2d0e03d2-c5a0-4625-89b6-bd287b4c4387.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (133, '925eda52282b45deb9f74711a56261d1', '/commodity/0c2f2de6-c8b1-4a6d-b511-b1eba709aa8f.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (134, '925eda52282b45deb9f74711a56261d1', '/commodity/aea36293-391d-4952-b66b-cfecec40b1c0.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (135, '925eda52282b45deb9f74711a56261d1', '/commodity/3b629b8e-13d0-421e-8dcf-9488877f6fd7.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (136, 'f45ba8fc7a6e4dbba87cd561ae226b78', '/commodity/978029a4-1e17-41df-9b93-4b27c3075608.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (137, 'f45ba8fc7a6e4dbba87cd561ae226b78', '/commodity/dc979843-4197-470b-a921-0dd68d0b59b9.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (138, 'f45ba8fc7a6e4dbba87cd561ae226b78', '/commodity/03c9b3d7-bb17-421e-b06e-b81fbec542fb.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (139, 'f45ba8fc7a6e4dbba87cd561ae226b78', '/commodity/a6c08822-2ba1-4095-81f1-d3e62d21e788.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (140, 'aa7840d2a64142e7a42b65422c292380', '/commodity/671c3eb1-c43c-4dfb-9beb-c9a697569524.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (141, 'aa7840d2a64142e7a42b65422c292380', '/commodity/17c88d2d-eb1d-4085-9503-0c443571dc9f.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (142, 'aa7840d2a64142e7a42b65422c292380', '/commodity/a7daea62-ebcc-4f53-a85b-5bbc0cbe4a13.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (143, 'bb36194793044e72aea8b8225fa3464a', '/commodity/e4d14763-430d-4120-93d0-f3fb619bd556.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (144, 'bb36194793044e72aea8b8225fa3464a', '/commodity/98b56c74-fc85-46a7-a228-39a113b01e8b.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (145, 'bb36194793044e72aea8b8225fa3464a', '/commodity/12c8b514-30b4-4337-8869-5f833b9d170f.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (146, 'bb36194793044e72aea8b8225fa3464a', '/commodity/14614885-d170-48aa-adeb-8966faee73de.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (147, 'c3d58cd202154790af95c542e1a8b54c', '/commodity/170c0459-81e5-477e-9cbe-964cc2521f79.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (148, 'c3d58cd202154790af95c542e1a8b54c', '/commodity/948aaf84-8629-402b-9780-fa4776f3b064.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (149, 'c3d58cd202154790af95c542e1a8b54c', '/commodity/fb906c85-4464-40e7-bca5-cb9bda28683b.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (150, 'c3d58cd202154790af95c542e1a8b54c', '/commodity/f35fceba-0b1c-411f-9e31-f1132af96568.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (151, 'b0267fc794ef4a66855652443083c795', '/commodity/f70a236e-9772-4c71-830e-3fd9de436dbf.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (152, 'b0267fc794ef4a66855652443083c795', '/commodity/cefb478c-29d8-4337-8862-9d210a9ab68d.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (153, 'cffedbeca45f4f7bba8df8e06ad647c3', '/commodity/4ce8f5c1-c02b-441b-8b81-1fd93b8ef9f9.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (154, 'cffedbeca45f4f7bba8df8e06ad647c3', '/commodity/9b8c6e0a-67e3-4603-a1ef-1356ffcf7df8.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (155, 'cffedbeca45f4f7bba8df8e06ad647c3', '/commodity/aa6b59dd-c7e7-403f-a333-650cee5c2aaa.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (156, 'cffedbeca45f4f7bba8df8e06ad647c3', '/commodity/241073b7-a490-44fa-bf49-8713377571d8.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (157, 'f2fb8c9a456c465da7172f065de8704a', '/commodity/1807277d-16ec-469d-a481-8e6c728ab943.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (158, 'f2fb8c9a456c465da7172f065de8704a', '/commodity/aad12783-1219-4cd3-8fd2-819674d8e477.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (159, 'f2fb8c9a456c465da7172f065de8704a', '/commodity/a0286ff7-d693-4669-9bfa-d2e27c6da3a2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (160, 'f2fb8c9a456c465da7172f065de8704a', '/commodity/9b0377e5-bde9-41ed-94df-238c341a89e6.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (161, 'c41624f99cdd46d5a56b5c2c190eb02c', '/commodity/6e42b038-8510-49bc-9457-fe603236d0d5.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (162, 'c41624f99cdd46d5a56b5c2c190eb02c', '/commodity/a39576c6-627e-4ec1-981a-0f03b36c6ca9.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (163, 'c41624f99cdd46d5a56b5c2c190eb02c', '/commodity/c1b5e02e-aff7-49ba-9599-97316db26449.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (164, '858be1887e544acb95172f0f0d5fc1fd', '/commodity/652d22d8-7f32-4006-9fc4-c4073306173a.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (165, '858be1887e544acb95172f0f0d5fc1fd', '/commodity/bbfc94d0-e086-4b3c-ac12-44cad43cb5c1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (166, '858be1887e544acb95172f0f0d5fc1fd', '/commodity/1764d498-1031-4445-bfa8-4e0535d9d055.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (167, '858be1887e544acb95172f0f0d5fc1fd', '/commodity/901cd23d-7c01-401f-8094-0b361d8c58d2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (168, 'd0b97ae7961d4a5cb099c4c709c8aa8a', '/commodity/6d16811e-2d67-48c7-a7a1-8872f4be020a.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (169, 'd0b97ae7961d4a5cb099c4c709c8aa8a', '/commodity/417c61fc-e4a7-4dac-97b3-20d6afff5cf2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (170, 'd0b97ae7961d4a5cb099c4c709c8aa8a', '/commodity/9201862f-7cdb-4555-ac8c-2327196760c5.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (171, 'd0b97ae7961d4a5cb099c4c709c8aa8a', '/commodity/6fa68278-1897-4bb6-8add-25e35450000a.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (172, 'f54b150480af4c1788baba75fd401f2f', '/commodity/81c17bd7-b172-41cb-b1e1-23316b3d566c.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (173, 'f54b150480af4c1788baba75fd401f2f', '/commodity/c9a4a013-3554-4ddd-9c8a-bff028bce2eb.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (174, 'f54b150480af4c1788baba75fd401f2f', '/commodity/2bc72088-f31b-4335-83ae-dcb07c0fbcda.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (175, 'f54b150480af4c1788baba75fd401f2f', '/commodity/ee690b13-16d5-4893-9f1d-21411ade77b4.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (176, 'f07a7f33ca7c465b84508c17844d3155', '/commodity/4a5ad2c7-05b8-4940-b93f-9486b0932fcf.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (177, 'f07a7f33ca7c465b84508c17844d3155', '/commodity/24c0903e-75cd-4ff1-bc31-412e2569c900.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (178, 'f07a7f33ca7c465b84508c17844d3155', '/commodity/fc6d3cee-a8c8-4afa-bd72-0eaeff9bd5d4.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (179, 'f07a7f33ca7c465b84508c17844d3155', '/commodity/77ca5f34-7147-481c-9715-1377e1c0d930.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (180, '15729d1dc0834cd0b04276055b2d30ad', '/commodity/13ef03e2-be6f-4c32-ae15-c4997a6d062a.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (181, '15729d1dc0834cd0b04276055b2d30ad', '/commodity/d970c2ae-15f4-420f-8be6-7492da5f9428.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (182, '15729d1dc0834cd0b04276055b2d30ad', '/commodity/d8eaaa64-b299-4321-8da5-023bf39f9fac.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (183, '15729d1dc0834cd0b04276055b2d30ad', '/commodity/6375981c-a272-4b41-9845-a41de2309f29.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (184, '3f17ce33f4b44ce28e9ba171d0f4f526', '/commodity/580a0313-16b4-49c0-8c4a-ed76417400eb.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (185, '3f17ce33f4b44ce28e9ba171d0f4f526', '/commodity/739ec13e-68cd-42de-a82d-0d85616989a4.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (186, '3f17ce33f4b44ce28e9ba171d0f4f526', '/commodity/96de0b8e-2c80-4bb3-9715-15032b0f8a54.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (187, '2e7a3125f438410e9c1c3b40a7c170fa', '/commodity/4e7c0d89-ae6e-4200-a9ef-c2653134a7d6.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (188, '2e7a3125f438410e9c1c3b40a7c170fa', '/commodity/0d10a4ef-d1b4-4e26-af01-246d3a955c6c.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (189, '2e7a3125f438410e9c1c3b40a7c170fa', '/commodity/133b31d8-0b38-4e31-8530-1a7d10f40d33.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (190, '2e7a3125f438410e9c1c3b40a7c170fa', '/commodity/75ac9957-4a99-491a-a273-518807c33eb0.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (191, '1e8e882c45824ddcae8e4a21c8610fa8', '/commodity/c903be7e-4171-44f0-942d-f3645c320cb6.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (192, '1e8e882c45824ddcae8e4a21c8610fa8', '/commodity/78572ad4-185e-4d5c-b361-f90d58f6af92.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (193, '1e8e882c45824ddcae8e4a21c8610fa8', '/commodity/2ecee1a4-ffc8-4bda-8566-f3f32df55763.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (194, '5d10a42b834a4dc48ca23676b2902887', '/commodity/6927728505096-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (195, '5d10a42b834a4dc48ca23676b2902887', '/commodity/6927728505096-2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (196, '5d10a42b834a4dc48ca23676b2902887', '/commodity/6927728505096-3.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (197, '5d10a42b834a4dc48ca23676b2902887', '/commodity/6927728505096-4.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (198, 'c21ef37b6f714ed3b9ad6bacbace6fcd', '/commodity/6022369925333-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (199, 'c21ef37b6f714ed3b9ad6bacbace6fcd', '/commodity/6022369925333-2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (200, 'c21ef37b6f714ed3b9ad6bacbace6fcd', '/commodity/6022369925333-3.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (201, '9fbe4575ea7a4f8e862b8395c3df9362', '/commodity/f35b4143-0f80-4a90-925e-26768bcacc7e.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (202, '9fbe4575ea7a4f8e862b8395c3df9362', '/commodity/a630ff47-27f2-4472-bd0f-0e1e9c652798.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (203, '9fbe4575ea7a4f8e862b8395c3df9362', '/commodity/85263264-a8ab-452a-855d-56492102fe12.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (204, '9fbe4575ea7a4f8e862b8395c3df9362', '/commodity/1a36126f-a0ef-4e31-88e8-da610b034cb3.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (205, '5ddc33b8d5f9423fb4860616bf489fbf', '/commodity/43fe38eb-7b73-4875-8577-a7f0639012a7.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (206, '5ddc33b8d5f9423fb4860616bf489fbf', '/commodity/4b7497cf-fad7-4b3a-b5bc-936b695b4bf0.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (207, '5ddc33b8d5f9423fb4860616bf489fbf', '/commodity/e8a008ab-e484-4bea-93d5-2049a5f3f618.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (208, '5ddc33b8d5f9423fb4860616bf489fbf', '/commodity/87f75345-c0d4-42dc-922a-ff8f32d8b56c.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (209, '7d29a9e9c54e4ce5ab593945a8c94edf', '/commodity/aff8f662-197f-47e2-b57b-1d5e85f47293.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (210, '7d29a9e9c54e4ce5ab593945a8c94edf', '/commodity/f301c942-8c81-4ff1-bdca-d384db53a67d.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (211, '16caad5d3da943fdba228944f9249019', '/commodity/1c8d1ab4-8178-4e2b-a540-55aa5e585866.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (212, '16caad5d3da943fdba228944f9249019', '/commodity/6a57ff99-bded-4fc2-9f42-28d347db0604.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (213, '16caad5d3da943fdba228944f9249019', '/commodity/c1490259-8ff7-41bf-8ee6-b16a9c7be2d5.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (214, '9bcf993ac2394fe797580f38b1cfb1a8', '/commodity/88342801-d795-405c-a11f-17e627ebd33f.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (215, '9bcf993ac2394fe797580f38b1cfb1a8', '/commodity/80b92d26-bcd7-464a-8556-02629828b672.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (216, '9bcf993ac2394fe797580f38b1cfb1a8', '/commodity/985d8aec-6ca5-4151-8835-3082f0fc9d30.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (217, '490ebcbe76834f659f0d2e95ce3342e7', '/commodity/9834766d-ba51-4bcf-80ae-9bf82ce5ab22.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (218, '490ebcbe76834f659f0d2e95ce3342e7', '/commodity/5b31a356-7be2-4e5c-81bb-bf72f95ebc77.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (219, '490ebcbe76834f659f0d2e95ce3342e7', '/commodity/62e17778-a70c-4b8a-aad8-34825f928ed5.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (220, '07e3fab379244ff5b6f78f6ad41e75ad', '/commodity/55bc61d2-b3c9-4b79-968e-4903575a1a7c.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (221, '07e3fab379244ff5b6f78f6ad41e75ad', '/commodity/5620300c-9322-452f-aea5-a0c88368d58c.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (222, '07e3fab379244ff5b6f78f6ad41e75ad', '/commodity/be4738c2-8988-4f66-9643-1d290cfb2825.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (223, '07e3fab379244ff5b6f78f6ad41e75ad', '/commodity/70455587-9e73-447f-97d3-cfa023edfcf7.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (224, '4582ea22e68647368a7f0ffcb25efe2c', '/commodity/67369858-df00-4369-8a57-45c0e91c0354.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (225, '4582ea22e68647368a7f0ffcb25efe2c', '/commodity/48212540-cb0d-4f57-9c45-3baa770cbad3.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (226, '4582ea22e68647368a7f0ffcb25efe2c', '/commodity/28827251-ff94-4e6f-b338-c101962216c1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (227, '6d78ac772ec84a149d6df33e53699c3b', '/commodity/1a24c676-1054-4e10-a889-7d3ebedeca54.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (228, '6d78ac772ec84a149d6df33e53699c3b', '/commodity/fa3027b6-1663-43fb-bb4b-37b5d51e7671.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (229, '6d78ac772ec84a149d6df33e53699c3b', '/commodity/e4df76bd-b172-4420-99e1-d70977855d3f.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (230, '5e4251cbcf77491994f812f650b99388', '/commodity/f2bf9b70-beb8-46e2-8a3c-a94effb8f707.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (231, '5e4251cbcf77491994f812f650b99388', '/commodity/675b0af2-3fcf-4b8d-9cbb-31815d79a334.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (232, '5e4251cbcf77491994f812f650b99388', '/commodity/9359262f-1531-46d4-b918-bf16d2d319c5.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (233, '5e4251cbcf77491994f812f650b99388', '/commodity/e66dfb4b-5b9f-4811-b99d-114800453288.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (234, '870bdc36ff044951b6650ceef339ac8f', '/commodity/64910658-e128-4b31-b8dd-28910b99c49a.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (235, '870bdc36ff044951b6650ceef339ac8f', '/commodity/7a6a1574-dd5e-497b-9c3d-b6a62c22bd4e.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (236, '870bdc36ff044951b6650ceef339ac8f', '/commodity/d0c63e6f-c26f-4b06-a255-fa2b2d3a6e1d.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (237, '870bdc36ff044951b6650ceef339ac8f', '/commodity/95312cd7-eca6-4df4-829c-a41f5f7ac7a0.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (238, 'a16a86ecd361421f92e61bf3b0ae768c', '/commodity/cb4f6430-a942-4353-89e4-5224a3888d95.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (239, 'a16a86ecd361421f92e61bf3b0ae768c', '/commodity/123cdc0f-9d9f-431b-af23-01b42549aa12.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (240, 'a16a86ecd361421f92e61bf3b0ae768c', '/commodity/0f9049ec-ec61-4426-b2d6-558e1e1ac3ff.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (241, 'a16a86ecd361421f92e61bf3b0ae768c', '/commodity/b6d5726c-3603-4d73-a9e6-1af219f4fda7.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (242, 'f446cce840784008b072bcf6b0650ee0', '/commodity/4fdfe143-2b6d-4cbb-9b4c-d60173d0f13b.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (243, 'f446cce840784008b072bcf6b0650ee0', '/commodity/9d543b76-162e-41f6-b3c4-5e2fc328caa0.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (244, 'f446cce840784008b072bcf6b0650ee0', '/commodity/07ca5b6c-a5e0-4b57-a8b9-1f1409f280b3.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (245, 'f446cce840784008b072bcf6b0650ee0', '/commodity/8b74b6da-35a1-4993-93da-2ca0e1599ae2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (246, 'd519569e8d87410b8521435a26cb9933', '/commodity/c2281c81-f32d-4576-a6d4-677da3a146ad.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (247, 'd519569e8d87410b8521435a26cb9933', '/commodity/88500c3b-2b18-4fb2-a7fa-8d24aff96934.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (248, '706040790f5e4ff69a20eedb38c56549', '/commodity/53919a91-d93d-4414-b3f8-759f152aa547.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (249, '706040790f5e4ff69a20eedb38c56549', '/commodity/cb291148-e78e-4745-83d6-65b7a1ecd203.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (250, '706040790f5e4ff69a20eedb38c56549', '/commodity/1a3cb60e-d271-4c6d-b851-e2614d735ff7.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (251, '706040790f5e4ff69a20eedb38c56549', '/commodity/30a82151-16f0-4cff-adac-79c9fa01c0ca.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (252, 'cc02635cdfdc4defbad3a0d6c1b41964', '/commodity/45f54055-6f5f-42c6-8ee1-689a7b6b81b7.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (253, 'cc02635cdfdc4defbad3a0d6c1b41964', '/commodity/4ee64af6-89a3-4b3b-97fa-8bba025abd39.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (254, 'cc02635cdfdc4defbad3a0d6c1b41964', '/commodity/495efbb8-d2e5-4e02-9694-64ad0fe6038a.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (255, 'cc02635cdfdc4defbad3a0d6c1b41964', '/commodity/6a36f1a3-fe92-489e-a4fb-103d6eb51f11.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (256, 'd4d1a3bd9e994f64ad7c2ee8eff69ee1', '/commodity/0d3f4123-2099-423a-87d4-576bc82f99f2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (257, 'd4d1a3bd9e994f64ad7c2ee8eff69ee1', '/commodity/19b2a6b8-1c56-493a-a078-383144e00f56.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (258, 'd4d1a3bd9e994f64ad7c2ee8eff69ee1', '/commodity/10e7807c-884c-4987-a4a7-49d95100359f.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (259, 'd4d1a3bd9e994f64ad7c2ee8eff69ee1', '/commodity/d0241dcf-d4a6-44e2-9951-ceca2df35e6d.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (260, '308502b9df0149b7ad07919fcfbc7127', '/commodity/c6f71477-1809-4f84-9c68-24eef2243e1a.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (261, '308502b9df0149b7ad07919fcfbc7127', '/commodity/dc138c3c-9a0b-46e7-9bb3-1d65e70daa48.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (262, '308502b9df0149b7ad07919fcfbc7127', '/commodity/7a58c8d8-90b1-4ae8-bd46-dd815eac3de3.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (263, '8efd0cddfba14d469f68d3fbdf7bd146', '/commodity/fc13dfe7-bfcf-4b30-af1b-5d5e85432dd1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (264, '8efd0cddfba14d469f68d3fbdf7bd146', '/commodity/2b021a77-e048-4080-a87d-fa4be00fd33a.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (265, '8efd0cddfba14d469f68d3fbdf7bd146', '/commodity/45e876a2-02fe-4af1-8d22-37b2543a7c5a.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (266, 'ff8392fcfbbe4bc3b7243218996fb1de', '/commodity/7a0bd124-0a21-47d2-af51-ab4b23061481.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (267, 'ff8392fcfbbe4bc3b7243218996fb1de', '/commodity/b041ccad-74cb-440c-8fff-e7487e309a94.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (268, 'ff8392fcfbbe4bc3b7243218996fb1de', '/commodity/adcd9de0-101f-4521-944e-082823c7e7e1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (269, '0fb231f449844c65be8e4ea6d012bacf', '/commodity/bdf16d64-e546-4510-a389-ee21b2bba28d.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (270, '0fb231f449844c65be8e4ea6d012bacf', '/commodity/3932b353-747f-425e-84c3-77f560a956e0.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (271, '79f68fc662d94ffaae352be967633760', '/commodity/e005b96b-1718-4de4-91f0-b562dc360f50.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (272, '79f68fc662d94ffaae352be967633760', '/commodity/a0549b81-39d1-4d5b-907c-54eb9d85a556.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (273, '79f68fc662d94ffaae352be967633760', '/commodity/7980445c-4ce5-4dfc-9288-d1dc35a372a1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (274, '01833ffb827d4da48dfe8a2528c0020e', '/commodity/5138da87-cf2e-4847-9fa8-32221cbf7579.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (275, '01833ffb827d4da48dfe8a2528c0020e', '/commodity/a8ee10f2-b287-4e08-99dd-5bb90941fea9.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (276, '01833ffb827d4da48dfe8a2528c0020e', '/commodity/7aa32fbf-f449-4fb5-93cd-f5076c37ad01.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (277, '01833ffb827d4da48dfe8a2528c0020e', '/commodity/fa11266d-57f0-482c-9c9a-c0411254c093.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (278, '58686e68a3db47f3a723a1acb08257eb', '/commodity/db109d86-206f-4f49-85f6-4cb1aaea62b2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (279, '58686e68a3db47f3a723a1acb08257eb', '/commodity/af5a6f88-54c9-4644-8418-88210907a833.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (280, '58686e68a3db47f3a723a1acb08257eb', '/commodity/ad0a59c6-9dd9-403c-b8e2-cfc1f226c366.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (281, '940b6061d34a45f69d3ae7d1e5c8d925', '/commodity/a688aa60-2213-457a-84ea-b03a4f993168.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (282, '940b6061d34a45f69d3ae7d1e5c8d925', '/commodity/ad6c4432-cb24-4261-9cfe-e551cfd961b5.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (283, '940b6061d34a45f69d3ae7d1e5c8d925', '/commodity/2f0b0a35-5e10-47ed-ab1c-072ae4f08bd9.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (284, 'd69b08093ecb46ed9f94e59a7a1e5246', '/commodity/6923644278595-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (285, 'd69b08093ecb46ed9f94e59a7a1e5246', '/commodity/6923644278595-2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (286, 'd69b08093ecb46ed9f94e59a7a1e5246', '/commodity/6923644278595-1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (287, 'd69b08093ecb46ed9f94e59a7a1e5246', '/commodity/6923644278595-2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (288, 'f5aa6e9f55cb4014a13cef7fd0c4127c', '/commodity/eeb95b45-8a1c-419e-bcf0-ec78c3eb1e0d.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (289, 'f5aa6e9f55cb4014a13cef7fd0c4127c', '/commodity/7ef4bfca-b9c2-4575-ad98-5f3202e0a9f1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (290, 'f5aa6e9f55cb4014a13cef7fd0c4127c', '/commodity/726d8caa-e940-4343-9731-2618a22edf4a.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (291, 'f5aa6e9f55cb4014a13cef7fd0c4127c', '/commodity/d57d1ffc-8288-45e1-9b80-38fea7fd19ff.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (292, '3a166b2cf6204424be4019e9dda7d001', '/commodity/f62bd00f-b6df-4453-baf9-9aa2184a678c.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (293, '3a166b2cf6204424be4019e9dda7d001', '/commodity/13bfb4a6-9931-48c8-b767-f62ab237cc9e.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (294, '3a166b2cf6204424be4019e9dda7d001', '/commodity/5ef1b70f-91b7-4bee-8634-eeac923f55fa.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (295, 'ec290854177e4b17a00acdb6c2d81677', '/commodity/21755e7a-f8ab-472b-b087-203491187e87.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (296, 'ec290854177e4b17a00acdb6c2d81677', '/commodity/5777aaf6-752a-40b3-86e8-dbe710ac33df.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (297, 'ec290854177e4b17a00acdb6c2d81677', '/commodity/291bffbb-0031-44b6-93d9-b68eb8e0f691.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (298, 'ec290854177e4b17a00acdb6c2d81677', '/commodity/fa093338-97eb-477e-aa46-35dcfbb8534e.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (299, 'b1f367961f754c87b9ceed9a40887b21', '/commodity/33465d42-ae04-43b2-a9df-a4ead739ce89.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (300, 'b1f367961f754c87b9ceed9a40887b21', '/commodity/4143b21c-5585-4e62-9c5a-61b31c25a1c1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (301, 'b1f367961f754c87b9ceed9a40887b21', '/commodity/4aac155e-9f9e-45d7-a9be-6fb077d4a982.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (302, '9f6e9d7263bd4e4d88ea90b0be58179e', '/commodity/3ce81c19-8c19-41ea-9cef-1ab85c9abac4.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (303, '9f6e9d7263bd4e4d88ea90b0be58179e', '/commodity/21ac3678-2f82-459d-96df-8c0581bb5f8a.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (304, '9f6e9d7263bd4e4d88ea90b0be58179e', '/commodity/3e12737a-957d-47e4-9365-e138e0432a27.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (305, '9f6e9d7263bd4e4d88ea90b0be58179e', '/commodity/53e1902b-f0e6-4597-8551-f1350999677e.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (306, '3e221bcfbccc456383da96f9e33efa0c', '/commodity/9f6effa5-dd53-4cda-921d-fe9cabc8f4a6.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (307, '3e221bcfbccc456383da96f9e33efa0c', '/commodity/39859dbc-801c-4358-ab4f-c1631b1b2c46.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (308, '3e221bcfbccc456383da96f9e33efa0c', '/commodity/d6774a27-c2fd-45be-8747-1b7b0dc24c8c.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (309, '3e221bcfbccc456383da96f9e33efa0c', '/commodity/3349dae6-3032-46c6-a4ad-822b0024da0c.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (310, 'd6b7310e8bbf4bab81f177144fcad301', '/commodity/c1ca6ba6-24ce-4a61-8f33-0e8e105019ba.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (311, 'd6b7310e8bbf4bab81f177144fcad301', '/commodity/bd36ff83-dfec-4e34-a945-aa0904b7e6c7.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (312, 'd6b7310e8bbf4bab81f177144fcad301', '/commodity/7b3642d7-0b5c-4490-b389-5a81badfeba3.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (313, 'ebb769fc418d4f4e9c3dff35d3ddcc3c', '/commodity/82491f7e-2b78-4499-9060-ab6ea7d9a624.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (314, 'ebb769fc418d4f4e9c3dff35d3ddcc3c', '/commodity/737170d4-a8fd-4b78-a3d3-7455ab0363c1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (315, 'ebb769fc418d4f4e9c3dff35d3ddcc3c', '/commodity/72800861-afe6-4199-86dc-b6c007eb95f2.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (316, 'ebb769fc418d4f4e9c3dff35d3ddcc3c', '/commodity/eacec571-1d5b-4811-8ab7-c82a807ec5ae.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (317, 'f47fddee2b75447a85f7959c683f0071', '/commodity/9a4ca358-bc6c-43b4-99b8-edcfadfbf461.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (318, 'f47fddee2b75447a85f7959c683f0071', '/commodity/4e3c8910-b740-435e-ad3e-d37846872a7f.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (319, 'f47fddee2b75447a85f7959c683f0071', '/commodity/659130dd-8515-4d5a-93b9-253e141122ae.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (320, 'f47fddee2b75447a85f7959c683f0071', '/commodity/2381906c-1a5f-4ac5-acc6-f484f8dc9132.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (321, 'fd45827cb8da46158d3074045b34beb3', '/commodity/1c7b9177-607b-4f41-84dd-df62ef5046fe.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (322, 'fd45827cb8da46158d3074045b34beb3', '/commodity/409df856-6fb8-4b09-8131-9d182cd764b4.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (323, 'fd45827cb8da46158d3074045b34beb3', '/commodity/f98f1ccb-186f-4904-8e0a-eb160b9023ad.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (324, 'fd45827cb8da46158d3074045b34beb3', '/commodity/2d204e7f-ec48-4753-8c4f-d30ec146afbf.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (325, 'e65f694df46a4937b94f25115ec02f83', '/commodity/1d3904dc-086f-43da-ba40-218b65649072.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (326, 'e65f694df46a4937b94f25115ec02f83', '/commodity/8ba96b8a-068d-49d0-a050-d43bb3d27544.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (327, 'e65f694df46a4937b94f25115ec02f83', '/commodity/c8f6f8b4-74cb-4120-91b2-c85de3b2eff7.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (328, 'e65f694df46a4937b94f25115ec02f83', '/commodity/7f3c9aaa-4817-4f5e-83c3-829a50936ad4.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (329, '158cfae18bc844879d15177f3e886033', '/commodity/ac42f43e-e041-4055-8323-61d617b1d688.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (330, '158cfae18bc844879d15177f3e886033', '/commodity/c700b3c9-07db-46d6-b1fc-23d6ddcf3179.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (331, '158cfae18bc844879d15177f3e886033', '/commodity/3b951b10-2e8b-47b5-95f2-3720a3a49757.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (332, '428db7b0a52b45d39f8495b394f38a43', '/commodity/d68ae30c-874d-4ded-bdae-ff9e6ef831e6.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (333, '428db7b0a52b45d39f8495b394f38a43', '/commodity/42cbccce-da2e-4d90-ad72-67918bd574f5.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (334, '428db7b0a52b45d39f8495b394f38a43', '/commodity/52f6d618-a7d3-4ca9-843c-8a7e00a42b30.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (335, '428db7b0a52b45d39f8495b394f38a43', '/commodity/bed1b7c0-cd9f-46bb-ac9a-510db2e59b5d.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (336, '2858bfb6b4fa4ecfb9a44fcca8d104e3', '/commodity/ba242147-6efd-4b0e-a7cd-52214a71e2f7.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (337, '2858bfb6b4fa4ecfb9a44fcca8d104e3', '/commodity/658deead-c37c-4d75-8ea7-5d71fcc83e1a.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (338, '2858bfb6b4fa4ecfb9a44fcca8d104e3', '/commodity/1fb3b337-9a37-4ae2-8743-b99bc5a95e7e.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (339, 'fc8b011e9d744b95850c82bea2f5b024', '/commodity/a7267b2e-bc81-4f9a-8652-8a3811c5288b.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (340, 'fc8b011e9d744b95850c82bea2f5b024', '/commodity/f20b075a-a5da-4285-ab30-2bb64306f0da.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (341, '88ec044990f0463b9e59536725044c0b', '/commodity/a74ee6d1-000b-47a6-bf5b-b5276aa5184f.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (342, '88ec044990f0463b9e59536725044c0b', '/commodity/96b5314b-0010-4567-a125-11cfa8cf9c8f.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (343, '9cf9a0d3710a47da83dffcfca6f34750', '/commodity/51fc4c6a-de96-4b08-9c8d-8093f29d89c4.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (344, 'ad9633af46594e6faa6af98b894b816d', '/commodity/1727bde6-0261-4e82-9ca3-1d972ab37f9f.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (345, 'ad9633af46594e6faa6af98b894b816d', '/commodity/d3ee235c-cc46-4117-99d8-f3be5843b51a.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (346, 'ad9633af46594e6faa6af98b894b816d', '/commodity/a79249a0-e8bb-470e-adaa-afc5565007ae.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (347, '03690d93b1c849ff83651315a36b4389', '/commodity/7db9c922-047a-4b00-9ccf-2409eead7454.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (348, 'c54ff1fa8f8443e789226f2e287a36ce', '/commodity/33f2dbc8-78ba-42ba-bcd1-628ad565a304.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (349, 'dcfd16649e6046819bc5b921655e8932', '/commodity/e664f3f6-d13c-4d8f-a729-678cd5ee64ed.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (350, '81bf2df010f948c4ace9bf50c06139db', '/commodity/55b1e06d-c81e-4aad-95e6-9951f67f1fa0.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (351, '81bf2df010f948c4ace9bf50c06139db', '/commodity/4742971b-aa59-4dfa-8598-03ed430cc6f5.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (352, '62346c8600b74dfa859501a08612997e', '/commodity/0e6aca6d-f829-48a8-97cd-2ce6fdd067c9.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (353, '235102a6cbdb4360a81c130efcdf3164', '/commodity/751842be-9f29-4b3f-934d-d48768a339b5.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (354, '6fbd6fdf20e9481481f755d5868df2db', '/commodity/f614ea70-46c7-4a91-af0b-91fff8e74579.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (355, '6fbd6fdf20e9481481f755d5868df2db', '/commodity/303e7fc4-6e5d-4964-8a8b-264d116b34e5.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (356, '16984d51e4eb4f7480f38046163e5441', '/commodity/0bb872cb-f776-47a0-b7f6-60bd25889178.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (357, 'f40d936790dd4ee4925216e8f8e66c32', '/commodity/f227b8e9-146e-4199-89ba-9d45fd9d996f.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (358, 'a15c8e814493432398ded321cce8e16b', '/commodity/8cd5bdb5-22ad-454c-98b5-5933f70f9c83.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (359, 'c273a02ab88c411bbc2d25f02ac79a6c', '/commodity/abcb3bf3-636f-4a94-a59d-bba99d832166.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (360, 'ad30d3aa49954d70811fd9fb746a63a3', '/commodity/0b087f33-1369-4b46-9ef5-a2a856be0282.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (361, 'a5f28fb59d8d4acf899aad74b98be51d', '/commodity/b9ddb465-4af6-4656-b48b-54a5f7538ec5.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (362, 'ef7292010dea4c2fbc1ddecb7c7fda09', '/commodity/d2ff6e01-c3b2-464b-9d5d-1d917ed8660f.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (363, 'f5de4558b2e2477489856dfeb29ec445', '/commodity/e3450c9b-353e-41f1-b7ef-38f444d7a0d3.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (364, '932ef51a35b842b9b443c9123c6c344a', '/commodity/00eb84c0-003e-4c2e-96ba-b74abb3a2d91.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (365, '8ed404859d2d4fc89da2c20221ce292d', '/commodity/e25d31df-c114-448f-8690-b7fab6a3a790.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (366, '21fdbab24ce44787a80bebd15a8ad1cf', '/commodity/610cd93f-a29c-412c-a384-86c5ba5732a4.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (367, '08e9e5efa26c47a5a8f51c1f9208a978', '/commodity/6dcaa83e-4cfc-4737-9510-31269d69e2e0.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (368, '452af6779cc540b4bfdc679a45a05ba5', '/commodity/80a81dfe-9236-462a-87ea-11bb067c7a54.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (369, '94ecfd156cfb4b969c47c87b5ab6cea7', '/commodity/bd36f2d0-8328-4ca4-a0da-8ad89a49962d.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (370, 'f99e482d695f493291b0238311893ba4', '/commodity/c232602a-3cf2-4a7f-8d96-ca2d171601b8.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (371, 'e020ee8bf7d94a5f8f4739a85e2c116d', '/commodity/0e7e524e-9cb4-406d-be29-1959b51c07b6.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (372, 'c36861dc5ce443f3addde97a827baf5e', '/commodity/88820ef6-f717-48af-a605-8b46fef4bc11.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (373, '7f0743fffc10486e995f27d869360772', '/commodity/a2dc9844-b806-4176-bf3a-5fe96eefa6d8.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (374, 'b7b5b0f4c42341fe9a58c9ebbea02283', '/commodity/01c010ed-de8c-418d-b6a4-1d158efc7012.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (375, '7ff47f4b0bdd46a88b3f74880c88e57a', '/commodity/67547552-54bd-4c09-9084-649867152f95.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (376, '77a058537afe49f7b555cc0f38817d3d', '/commodity/d9262fa5-bff8-4ee6-93b4-f8f3dfd1c85e.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (377, '72b4dbed72eb408f91e621768792a84f', '/commodity/9d2e4ffe-aeda-4ee2-8e70-dd03d5aa0185.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (378, 'f59bb7ca872643848eb36a0e6542fe14', '/commodity/86bd5c03-0567-495a-a9ae-7ee1ff0cefba.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (379, 'f59bb7ca872643848eb36a0e6542fe14', '/commodity/bc464c34-a2a2-4281-a15e-e97a3eb7c5dc.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (380, 'f59bb7ca872643848eb36a0e6542fe14', '/commodity/cbdba9d9-3a68-4c5a-90db-dc8209acc212.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (381, 'f59bb7ca872643848eb36a0e6542fe14', '/commodity/023abf6e-b457-4094-a671-658e4cee17d9.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (382, 'ecd53aa96b3343c9bc1c92bd0e4f4b9b', '/commodity/b0b00d6b-faeb-4d6e-bad5-6c8942ea3860.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (383, 'ecd53aa96b3343c9bc1c92bd0e4f4b9b', '/commodity/34b82fd0-bd75-4cb7-9870-a7836f75def6.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (384, 'ecd53aa96b3343c9bc1c92bd0e4f4b9b', '/commodity/28025d06-27ff-4b6c-991b-9166689cbbf5.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (385, 'c4d1d09b7e5843288e1a8ec465acfc30', '/commodity/6b4ab8f4-0540-4c8c-80f0-7df5d04150b5.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (386, 'c4d1d09b7e5843288e1a8ec465acfc30', '/commodity/d0194c50-8ed0-4710-98f5-aab819ec2731.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (387, 'c4d1d09b7e5843288e1a8ec465acfc30', '/commodity/5b748a43-2409-4699-879d-d9ec84108b0f.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (388, '9db13050033f45a697e0e3ee0fecd86c', '/commodity/36862f39-34a7-48c8-9baf-93e9f72da63b.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (389, '9db13050033f45a697e0e3ee0fecd86c', '/commodity/e5fd9a76-aa72-4ac8-887a-333577ce4f6b.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (390, '9db13050033f45a697e0e3ee0fecd86c', '/commodity/a933a5c2-2d02-4c5f-a03d-703ed2e6f2db.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (391, '4e6313c62aeb467cba6d4de70db9a3d6', '/commodity/af503508-95af-4a59-97a4-4b1e9fbedd56.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (392, '4e6313c62aeb467cba6d4de70db9a3d6', '/commodity/e7fdc56f-456e-4c58-8197-298f41f1a6a7.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (393, '4e6313c62aeb467cba6d4de70db9a3d6', '/commodity/07120802-e19b-4c24-a092-fffdd08b06f8.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (394, 'f0a65a0c8dc04b5b850ed7835ce11088', '/commodity/b8b60b64-495c-49f3-aed4-2b1bc6891a6e.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (395, 'f0a65a0c8dc04b5b850ed7835ce11088', '/commodity/e2f3dacc-7a05-4d29-9069-da2c10fdf2f8.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (396, 'f0a65a0c8dc04b5b850ed7835ce11088', '/commodity/41d1e6b8-2f02-4122-a6be-9ab690e3d904.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (397, '3da4fed1ddf94183a60558d0bcced790', '/commodity/b4a289f7-cb6d-4b2e-9d1e-72da0efcbc85.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (398, '3da4fed1ddf94183a60558d0bcced790', '/commodity/681d122e-a04a-4552-908b-82973b9bbeef.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (399, '3da4fed1ddf94183a60558d0bcced790', '/commodity/73a9d882-a34d-4996-b51f-6bbc8c058489.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (400, '3da4fed1ddf94183a60558d0bcced790', '/commodity/89148926-277e-4008-ac3a-23edaa94d0ce.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (401, '40f42ffefb914adcaadf4d7596df1665', '/commodity/f7691b06-3d20-4df6-8af2-5adc97c9ed81.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (402, '40f42ffefb914adcaadf4d7596df1665', '/commodity/6bb2242c-3b6b-477f-a6b3-d9d8c5be6b76.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (403, '40f42ffefb914adcaadf4d7596df1665', '/commodity/03092434-7309-4fde-a0be-d8231d5a4cd1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (404, '40f42ffefb914adcaadf4d7596df1665', '/commodity/eb4db4cd-206b-4eb2-9192-19ca70e4fc8a.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (405, '4c346154c2c94e7ba7186448f51553a1', '/commodity/dbfa52d9-98f2-494b-a8aa-70a47fb30bad.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (406, '4c346154c2c94e7ba7186448f51553a1', '/commodity/856c5910-a671-46f5-9458-50684d95ab7b.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (407, '4c346154c2c94e7ba7186448f51553a1', '/commodity/1b473a18-714e-4c49-97c7-48658f872fd6.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (408, '12cb06f4fa1948078636a50717f743fe', '/commodity/5d01d28f-ee82-4f15-9919-5d9ea1715bf1.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (409, '12cb06f4fa1948078636a50717f743fe', '/commodity/3c8c0aae-d6b1-4f8f-adee-33b523d08142.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (410, '12cb06f4fa1948078636a50717f743fe', '/commodity/96ec3c88-ce60-4b5a-a227-9f2ec9b60520.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (411, '4fbefb6948ae4ef58cc827762575656e', '/commodity/7b46080e-a0d3-41ce-80d8-072e1d16489b.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (412, '4fbefb6948ae4ef58cc827762575656e', '/commodity/24e5182b-a5f5-4aa8-b5cb-431790023f79.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (413, '4fbefb6948ae4ef58cc827762575656e', '/commodity/93197010-2a69-44a1-94a5-dda892c48aa4.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (414, '4fbefb6948ae4ef58cc827762575656e', '/commodity/a060e0be-cc4b-49f7-b8b8-ad3543217997.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (415, '8e4ca6d93f6e4382aa3eff8838ca9543', '/commodity/fa95e3b5-206f-437d-a407-01e6e6efcf2a.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (416, '8e4ca6d93f6e4382aa3eff8838ca9543', '/commodity/84b74c79-fd58-49b6-8bff-814f822d2ee5.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (417, '8e4ca6d93f6e4382aa3eff8838ca9543', '/commodity/54e636b3-5dc8-4f86-90f5-8eff4a677234.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (418, 'dfad8b082aeb4edbaf1400d83c8b0af1', '/commodity/61efb308-00f6-4e0a-84cc-9d42cdc7f094.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (419, 'dfad8b082aeb4edbaf1400d83c8b0af1', '/commodity/324e614a-a41d-4287-9cc1-9edd96148614.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (420, 'dfad8b082aeb4edbaf1400d83c8b0af1', '/commodity/3048e19c-21db-45f1-96ba-c6c0600175a6.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (421, 'dfad8b082aeb4edbaf1400d83c8b0af1', '/commodity/305df8d6-df0c-4734-9bb6-89f541ab1442.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (422, '7ce97d9f1f974916856c33b0788fc0a5', '/commodity/cd1c24a1-3d68-45f9-924c-fa7c164231ee.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (423, '7ce97d9f1f974916856c33b0788fc0a5', '/commodity/676f7126-aa63-4a7a-85da-7e11c4680053.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (424, '56385112f1164bdd84b0f8b048ae0053', '/commodity/1452c996-30d6-434c-a256-d8e36a29acc8.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (425, '56385112f1164bdd84b0f8b048ae0053', '/commodity/39e267ad-dd69-49b2-8795-f22255a51d64.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (426, '56385112f1164bdd84b0f8b048ae0053', '/commodity/8430530f-7da8-40ea-9874-e37fb4c0cbc0.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (427, 'e32ee11e1fb94d1092dae98f217056ee', '/commodity/4eb4251f-7783-44a6-8812-7f7f51ae4c93.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (428, 'e32ee11e1fb94d1092dae98f217056ee', '/commodity/6e7340e5-7284-42ae-ad77-9fe9a9ab97e8.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (429, 'e32ee11e1fb94d1092dae98f217056ee', '/commodity/78b6dc0c-634f-408c-a059-0f42aee6a4e5.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (430, '705bbf9e904f46d782e77d4f0425df23', '/commodity/437aa716-d448-46d9-8375-ad5716135e5c.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (431, '705bbf9e904f46d782e77d4f0425df23', '/commodity/c3b5c58e-93c0-4389-b297-aaf6563b57ab.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (432, '705bbf9e904f46d782e77d4f0425df23', '/commodity/727b0ac7-ad94-42ee-bca7-143493588e5d.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (433, '834b81d55f404723ba2ea0629d92d0c6', '/commodity/30cae7d4-1102-4550-9aeb-42bfe042af9b.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (434, '834b81d55f404723ba2ea0629d92d0c6', '/commodity/67e8c45a-4aef-4996-8bba-edf423104eb5.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (435, 'aebbb174db5240c29a26534c96994e63', '/commodity/ad319336-b667-4177-b991-2c209a7c1b74.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (436, 'db5f0db8275e480db4b9e9445f7c8a02', '/commodity/09c28b7b-97b0-4924-9901-7e3608b75bdb.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (437, 'db5f0db8275e480db4b9e9445f7c8a02', '/commodity/e0a3a154-0cef-4527-b52c-8709f9a6b331.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (438, 'db5f0db8275e480db4b9e9445f7c8a02', '/commodity/deccf1e5-fdea-4b05-90c9-fa9b8ccb7a22.jpg', 1, '2023-03-31 09:48:05', NULL);
INSERT INTO `goods_picture` VALUES (497, 'a64897e692ef41b5a4501f50d70f29e0', '/commodity/6946881700346-1.jpg', 1, '2023-04-21 15:46:39', NULL);
INSERT INTO `goods_picture` VALUES (498, 'a64897e692ef41b5a4501f50d70f29e0', '/goods/19a69affa1bb4127a5c32e3fda214169.jpg', 1, '2023-04-21 15:46:39', NULL);
INSERT INTO `goods_picture` VALUES (499, '421bbac8c94341158841c032d9894e49', '/goods/1495198ee74048b9b6c17b3f278f0b8f.jpg', 1, '2023-04-21 15:46:47', NULL);
INSERT INTO `goods_picture` VALUES (500, '421bbac8c94341158841c032d9894e49', '/goods/b3d345a2a46044338840b0a212da6301.jpg', 1, '2023-04-21 15:46:47', NULL);
INSERT INTO `goods_picture` VALUES (504, '16a2fa61e8c94f6699d36964f61cb7f7', '/commodity/6a6f58d2-c0f9-473e-983d-497f05af0c83.jpg', 1, '2023-04-23 16:08:37', NULL);
INSERT INTO `goods_picture` VALUES (505, '16a2fa61e8c94f6699d36964f61cb7f7', '/goods/211743c36f4c4f65854e58eb728d1453.jpg', 1, '2023-04-23 16:08:37', NULL);
INSERT INTO `goods_picture` VALUES (507, '1c0a017886bd49b9b81cc28660a0767a', '/goods/d48635797c774d63bf8898133fafd9e1.jpg', 1, '2023-04-27 11:03:27', NULL);
INSERT INTO `goods_picture` VALUES (508, '1c0a017886bd49b9b81cc28660a0767a', '/goods/26f7005b155940f89a710a56c5461128.jpg', 1, '2023-04-27 11:03:27', NULL);

-- ----------------------------
-- Table structure for goods_store
-- ----------------------------
DROP TABLE IF EXISTS `goods_store`;
CREATE TABLE `goods_store`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '商品价格id',
  `store_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '门店id',
  `goods_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品编号',
  `goods_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '商品价格',
  `goods_stock` int(11) NULL DEFAULT 0 COMMENT '商品库存',
  `goods_type` int(1) NULL DEFAULT 0 COMMENT '0',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `goods_goodsstore_idx`(`goods_no`) USING BTREE,
  INDEX `store_goodsstore_idx`(`store_no`) USING BTREE,
  CONSTRAINT `goods_goods_store` FOREIGN KEY (`goods_no`) REFERENCES `goods` (`goods_no`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `store_goods_store` FOREIGN KEY (`store_no`) REFERENCES `store` (`store_no`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 638 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of goods_store
-- ----------------------------
INSERT INTO `goods_store` VALUES (1, '832308d2000d4d1fbf31c8d791497cf3', '16a2fa61e8c94f6699d36964f61cb7f7', 10.00, 469, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (2, '1017f93fc41c45a6a3b8078ba3e778e5', '16a2fa61e8c94f6699d36964f61cb7f7', 10.08, 0, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (3, '144f6c169af64ed19e27347900febac1', '16a2fa61e8c94f6699d36964f61cb7f7', 10.08, 993, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (4, 'febefdc301844bff9f40127c2fd8846b', '16a2fa61e8c94f6699d36964f61cb7f7', 10.08, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (5, '832308d2000d4d1fbf31c8d791497cf3', '292a51053b1749b9bb29825b05c393fa', 206.40, 997, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (6, '1017f93fc41c45a6a3b8078ba3e778e5', '292a51053b1749b9bb29825b05c393fa', 206.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (7, '144f6c169af64ed19e27347900febac1', '292a51053b1749b9bb29825b05c393fa', 206.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (8, 'febefdc301844bff9f40127c2fd8846b', '292a51053b1749b9bb29825b05c393fa', 206.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (9, '832308d2000d4d1fbf31c8d791497cf3', 'a64897e692ef41b5a4501f50d70f29e0', 648.00, 372, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (10, '1017f93fc41c45a6a3b8078ba3e778e5', 'a64897e692ef41b5a4501f50d70f29e0', 648.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (11, '144f6c169af64ed19e27347900febac1', 'f56f34efb86c4db4b3eda8e1f60b2643', 566.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (12, 'febefdc301844bff9f40127c2fd8846b', 'f56f34efb86c4db4b3eda8e1f60b2643', 566.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (13, '832308d2000d4d1fbf31c8d791497cf3', 'f56f34efb86c4db4b3eda8e1f60b2643', 566.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (14, '1017f93fc41c45a6a3b8078ba3e778e5', 'f56f34efb86c4db4b3eda8e1f60b2643', 566.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (15, '144f6c169af64ed19e27347900febac1', '421bbac8c94341158841c032d9894e49', 416.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (16, 'febefdc301844bff9f40127c2fd8846b', '421bbac8c94341158841c032d9894e49', 416.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (17, '832308d2000d4d1fbf31c8d791497cf3', '421bbac8c94341158841c032d9894e49', 416.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (18, '1017f93fc41c45a6a3b8078ba3e778e5', '421bbac8c94341158841c032d9894e49', 416.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (19, '144f6c169af64ed19e27347900febac1', 'c126c02616484a648f0370ff2b87aea2', 285.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (20, 'febefdc301844bff9f40127c2fd8846b', 'c126c02616484a648f0370ff2b87aea2', 285.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (21, '832308d2000d4d1fbf31c8d791497cf3', 'c126c02616484a648f0370ff2b87aea2', 285.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (22, '1017f93fc41c45a6a3b8078ba3e778e5', 'c126c02616484a648f0370ff2b87aea2', 285.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (23, '144f6c169af64ed19e27347900febac1', '3aa8cd13da714e6dabad76f816163675', 273.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (24, 'febefdc301844bff9f40127c2fd8846b', '3aa8cd13da714e6dabad76f816163675', 273.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (25, '832308d2000d4d1fbf31c8d791497cf3', '3aa8cd13da714e6dabad76f816163675', 273.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (26, '1017f93fc41c45a6a3b8078ba3e778e5', '3aa8cd13da714e6dabad76f816163675', 273.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (27, '144f6c169af64ed19e27347900febac1', 'a12668cc124649d1b94cfe3f53a81bf9', 239.04, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (28, 'febefdc301844bff9f40127c2fd8846b', 'a12668cc124649d1b94cfe3f53a81bf9', 239.04, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (29, '832308d2000d4d1fbf31c8d791497cf3', 'a12668cc124649d1b94cfe3f53a81bf9', 239.04, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (30, '1017f93fc41c45a6a3b8078ba3e778e5', 'a12668cc124649d1b94cfe3f53a81bf9', 239.04, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (31, '144f6c169af64ed19e27347900febac1', 'b909af4bd0d34ad49416949a88cfe1c7', 143.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (32, 'febefdc301844bff9f40127c2fd8846b', 'b909af4bd0d34ad49416949a88cfe1c7', 143.52, 997, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (33, '832308d2000d4d1fbf31c8d791497cf3', 'b909af4bd0d34ad49416949a88cfe1c7', 143.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (34, '1017f93fc41c45a6a3b8078ba3e778e5', 'b909af4bd0d34ad49416949a88cfe1c7', 143.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (35, '144f6c169af64ed19e27347900febac1', '871d4dea9dd7406e89cf224bd36f66a8', 117.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (36, 'febefdc301844bff9f40127c2fd8846b', '871d4dea9dd7406e89cf224bd36f66a8', 117.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (37, '832308d2000d4d1fbf31c8d791497cf3', '871d4dea9dd7406e89cf224bd36f66a8', 117.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (38, '1017f93fc41c45a6a3b8078ba3e778e5', '871d4dea9dd7406e89cf224bd36f66a8', 117.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (39, '144f6c169af64ed19e27347900febac1', '361486a0c9eb43df8eda4e42daa8ee61', 69.60, 995, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (40, 'febefdc301844bff9f40127c2fd8846b', '361486a0c9eb43df8eda4e42daa8ee61', 69.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (41, '832308d2000d4d1fbf31c8d791497cf3', '361486a0c9eb43df8eda4e42daa8ee61', 69.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (42, '1017f93fc41c45a6a3b8078ba3e778e5', '361486a0c9eb43df8eda4e42daa8ee61', 69.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (43, '144f6c169af64ed19e27347900febac1', '2b96c721ddb047dea614533c9914413c', 156.48, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (44, 'febefdc301844bff9f40127c2fd8846b', '2b96c721ddb047dea614533c9914413c', 156.48, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (45, '832308d2000d4d1fbf31c8d791497cf3', '2b96c721ddb047dea614533c9914413c', 156.48, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (46, '1017f93fc41c45a6a3b8078ba3e778e5', '2b96c721ddb047dea614533c9914413c', 156.48, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (47, '144f6c169af64ed19e27347900febac1', 'f22f36c8b322410f90af7387c1d4550d', 62.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (48, 'febefdc301844bff9f40127c2fd8846b', 'f22f36c8b322410f90af7387c1d4550d', 62.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (49, '832308d2000d4d1fbf31c8d791497cf3', '7b08bfbf7b1943df879634b3bf6d7cf1', 955.20, 997, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (50, '1017f93fc41c45a6a3b8078ba3e778e5', '7b08bfbf7b1943df879634b3bf6d7cf1', 955.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (51, '144f6c169af64ed19e27347900febac1', '7b08bfbf7b1943df879634b3bf6d7cf1', 955.20, 997, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (52, 'febefdc301844bff9f40127c2fd8846b', '7b08bfbf7b1943df879634b3bf6d7cf1', 955.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (53, '832308d2000d4d1fbf31c8d791497cf3', '492970f5f780430e9351ded7bf1a80c7', 157.92, 986, 1, '2023-03-31 09:59:27', '2023-05-18 16:56:49');
INSERT INTO `goods_store` VALUES (54, '1017f93fc41c45a6a3b8078ba3e778e5', '492970f5f780430e9351ded7bf1a80c7', 157.92, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (55, '144f6c169af64ed19e27347900febac1', '492970f5f780430e9351ded7bf1a80c7', 157.92, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (56, 'febefdc301844bff9f40127c2fd8846b', '492970f5f780430e9351ded7bf1a80c7', 157.92, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (57, '832308d2000d4d1fbf31c8d791497cf3', '1e0752b8c9ad42479fcebc9bbfd1f872', 383.52, 999, 1, '2023-03-31 09:59:27', '2023-04-07 11:02:22');
INSERT INTO `goods_store` VALUES (58, '1017f93fc41c45a6a3b8078ba3e778e5', '1e0752b8c9ad42479fcebc9bbfd1f872', 383.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (59, '144f6c169af64ed19e27347900febac1', '1e0752b8c9ad42479fcebc9bbfd1f872', 383.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (60, 'febefdc301844bff9f40127c2fd8846b', '1e0752b8c9ad42479fcebc9bbfd1f872', 383.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (61, '832308d2000d4d1fbf31c8d791497cf3', 'ce0dd5e0d77a47c48f37301fcabf06c5', 146.40, 997, 1, '2023-03-31 09:59:27', '2023-04-07 10:38:17');
INSERT INTO `goods_store` VALUES (62, '1017f93fc41c45a6a3b8078ba3e778e5', 'ce0dd5e0d77a47c48f37301fcabf06c5', 146.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (63, '144f6c169af64ed19e27347900febac1', 'ce0dd5e0d77a47c48f37301fcabf06c5', 146.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (64, 'febefdc301844bff9f40127c2fd8846b', 'ce0dd5e0d77a47c48f37301fcabf06c5', 146.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (65, '832308d2000d4d1fbf31c8d791497cf3', 'f611ef02dd2d40eaa8b8447b90722553', 376.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (66, '1017f93fc41c45a6a3b8078ba3e778e5', 'f611ef02dd2d40eaa8b8447b90722553', 376.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (67, '144f6c169af64ed19e27347900febac1', 'f611ef02dd2d40eaa8b8447b90722553', 376.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (68, 'febefdc301844bff9f40127c2fd8846b', 'f611ef02dd2d40eaa8b8447b90722553', 376.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (69, '832308d2000d4d1fbf31c8d791497cf3', '415077ea72fe486fb139d345a7c4a800', 330.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (70, '1017f93fc41c45a6a3b8078ba3e778e5', '415077ea72fe486fb139d345a7c4a800', 330.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (71, '144f6c169af64ed19e27347900febac1', '415077ea72fe486fb139d345a7c4a800', 330.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (72, 'febefdc301844bff9f40127c2fd8846b', '415077ea72fe486fb139d345a7c4a800', 330.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (73, '832308d2000d4d1fbf31c8d791497cf3', 'd0bb67447493418c8ada18bfb11e1649', 357.60, 996, 1, '2023-03-31 09:59:27', '2023-04-12 10:18:23');
INSERT INTO `goods_store` VALUES (74, '1017f93fc41c45a6a3b8078ba3e778e5', 'd0bb67447493418c8ada18bfb11e1649', 357.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (75, '144f6c169af64ed19e27347900febac1', 'd0bb67447493418c8ada18bfb11e1649', 357.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (76, 'febefdc301844bff9f40127c2fd8846b', 'd0bb67447493418c8ada18bfb11e1649', 357.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (77, '832308d2000d4d1fbf31c8d791497cf3', 'd280ae925eba42a99bb988da213c7a88', 383.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (78, '1017f93fc41c45a6a3b8078ba3e778e5', 'd280ae925eba42a99bb988da213c7a88', 383.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (79, '144f6c169af64ed19e27347900febac1', 'd280ae925eba42a99bb988da213c7a88', 383.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (80, 'febefdc301844bff9f40127c2fd8846b', 'd280ae925eba42a99bb988da213c7a88', 383.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (81, '832308d2000d4d1fbf31c8d791497cf3', 'efc240af98ba46e4a5d46c4e47c2c323', 146.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (82, '1017f93fc41c45a6a3b8078ba3e778e5', 'efc240af98ba46e4a5d46c4e47c2c323', 146.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (83, '144f6c169af64ed19e27347900febac1', 'efc240af98ba46e4a5d46c4e47c2c323', 146.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (84, 'febefdc301844bff9f40127c2fd8846b', 'efc240af98ba46e4a5d46c4e47c2c323', 146.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (85, '832308d2000d4d1fbf31c8d791497cf3', 'c96757e31c1a4969be08070434393a29', 402.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (86, '1017f93fc41c45a6a3b8078ba3e778e5', 'c96757e31c1a4969be08070434393a29', 402.72, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (87, '144f6c169af64ed19e27347900febac1', 'c96757e31c1a4969be08070434393a29', 402.72, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (88, 'febefdc301844bff9f40127c2fd8846b', 'c96757e31c1a4969be08070434393a29', 402.72, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (89, '832308d2000d4d1fbf31c8d791497cf3', 'd5e13afe86564d1d8663b3028b1488fa', 300.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (90, '1017f93fc41c45a6a3b8078ba3e778e5', 'd5e13afe86564d1d8663b3028b1488fa', 300.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (91, '144f6c169af64ed19e27347900febac1', 'd5e13afe86564d1d8663b3028b1488fa', 300.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (92, 'febefdc301844bff9f40127c2fd8846b', 'd5e13afe86564d1d8663b3028b1488fa', 300.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (93, '832308d2000d4d1fbf31c8d791497cf3', '012da18c4d34427e8deadcaf12b285f4', 57.12, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (94, '1017f93fc41c45a6a3b8078ba3e778e5', '012da18c4d34427e8deadcaf12b285f4', 57.12, 996, 1, '2023-03-31 09:59:27', '2023-05-15 14:15:58');
INSERT INTO `goods_store` VALUES (95, '144f6c169af64ed19e27347900febac1', '012da18c4d34427e8deadcaf12b285f4', 57.12, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (96, 'febefdc301844bff9f40127c2fd8846b', '012da18c4d34427e8deadcaf12b285f4', 57.12, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (97, '832308d2000d4d1fbf31c8d791497cf3', 'd3e3b54f636d4f18bbf74741677ce2bb', 33.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (98, '1017f93fc41c45a6a3b8078ba3e778e5', 'd3e3b54f636d4f18bbf74741677ce2bb', 33.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (99, '144f6c169af64ed19e27347900febac1', 'd3e3b54f636d4f18bbf74741677ce2bb', 33.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (100, 'febefdc301844bff9f40127c2fd8846b', 'd3e3b54f636d4f18bbf74741677ce2bb', 33.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (101, '832308d2000d4d1fbf31c8d791497cf3', '982a87f2d5d747cda74bab30fd00c9cd', 32.16, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (102, '1017f93fc41c45a6a3b8078ba3e778e5', '982a87f2d5d747cda74bab30fd00c9cd', 32.16, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (103, '144f6c169af64ed19e27347900febac1', '982a87f2d5d747cda74bab30fd00c9cd', 32.16, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (104, 'febefdc301844bff9f40127c2fd8846b', '982a87f2d5d747cda74bab30fd00c9cd', 32.16, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (105, '832308d2000d4d1fbf31c8d791497cf3', 'f0d6b7c1b8064bdea70a67c5f0cae685', 26.88, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (106, '1017f93fc41c45a6a3b8078ba3e778e5', 'f0d6b7c1b8064bdea70a67c5f0cae685', 26.88, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (107, '144f6c169af64ed19e27347900febac1', 'f0d6b7c1b8064bdea70a67c5f0cae685', 26.88, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (108, 'febefdc301844bff9f40127c2fd8846b', 'f0d6b7c1b8064bdea70a67c5f0cae685', 26.88, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (109, '832308d2000d4d1fbf31c8d791497cf3', '7d42f802c8b34970acf5aff3bc2a79d3', 11.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (110, '1017f93fc41c45a6a3b8078ba3e778e5', '7d42f802c8b34970acf5aff3bc2a79d3', 11.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (111, '144f6c169af64ed19e27347900febac1', '7d42f802c8b34970acf5aff3bc2a79d3', 11.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (112, 'febefdc301844bff9f40127c2fd8846b', '7d42f802c8b34970acf5aff3bc2a79d3', 11.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (113, '832308d2000d4d1fbf31c8d791497cf3', '592e437d264a4dba966e2e68a52ae088', 0.48, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (114, '1017f93fc41c45a6a3b8078ba3e778e5', '592e437d264a4dba966e2e68a52ae088', 0.48, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (115, '144f6c169af64ed19e27347900febac1', '592e437d264a4dba966e2e68a52ae088', 0.48, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (116, 'febefdc301844bff9f40127c2fd8846b', '592e437d264a4dba966e2e68a52ae088', 0.48, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (117, '832308d2000d4d1fbf31c8d791497cf3', '9b1eb99ccf5a41ba9facce8aa4278940', 2347.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (118, '1017f93fc41c45a6a3b8078ba3e778e5', '9b1eb99ccf5a41ba9facce8aa4278940', 2347.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (119, '144f6c169af64ed19e27347900febac1', '9b1eb99ccf5a41ba9facce8aa4278940', 2347.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (120, 'febefdc301844bff9f40127c2fd8846b', 'ab061af8673f4beb90669772f2d4a28e', 349.92, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (121, '832308d2000d4d1fbf31c8d791497cf3', 'ab061af8673f4beb90669772f2d4a28e', 349.92, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (122, '1017f93fc41c45a6a3b8078ba3e778e5', 'ab061af8673f4beb90669772f2d4a28e', 349.92, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (123, '144f6c169af64ed19e27347900febac1', 'ab061af8673f4beb90669772f2d4a28e', 349.92, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (124, 'febefdc301844bff9f40127c2fd8846b', '0cbba76f4cf0434da9aa7e4a14e884ea', 191.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (125, '832308d2000d4d1fbf31c8d791497cf3', '0cbba76f4cf0434da9aa7e4a14e884ea', 191.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (126, '1017f93fc41c45a6a3b8078ba3e778e5', '0cbba76f4cf0434da9aa7e4a14e884ea', 191.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (127, '144f6c169af64ed19e27347900febac1', '0cbba76f4cf0434da9aa7e4a14e884ea', 191.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (128, 'febefdc301844bff9f40127c2fd8846b', '4c8ae591ecb340289b1330547be08fbd', 186.72, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (129, '832308d2000d4d1fbf31c8d791497cf3', '4c8ae591ecb340289b1330547be08fbd', 186.72, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (130, '1017f93fc41c45a6a3b8078ba3e778e5', '4c8ae591ecb340289b1330547be08fbd', 186.72, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (131, '144f6c169af64ed19e27347900febac1', '4c8ae591ecb340289b1330547be08fbd', 186.72, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (132, 'febefdc301844bff9f40127c2fd8846b', 'e486e0f6a32d4756b8104d5c1c124d16', 667.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (133, '832308d2000d4d1fbf31c8d791497cf3', 'e486e0f6a32d4756b8104d5c1c124d16', 667.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (134, '1017f93fc41c45a6a3b8078ba3e778e5', 'e486e0f6a32d4756b8104d5c1c124d16', 667.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (135, '144f6c169af64ed19e27347900febac1', 'e486e0f6a32d4756b8104d5c1c124d16', 667.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (136, 'febefdc301844bff9f40127c2fd8846b', '87ae1ecaeb424499a5ea95e427106ba5', 62.40, 0, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (137, '832308d2000d4d1fbf31c8d791497cf3', '87ae1ecaeb424499a5ea95e427106ba5', 62.40, 998, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (138, '1017f93fc41c45a6a3b8078ba3e778e5', '87ae1ecaeb424499a5ea95e427106ba5', 62.40, 998, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (139, '144f6c169af64ed19e27347900febac1', '87ae1ecaeb424499a5ea95e427106ba5', 62.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (140, 'febefdc301844bff9f40127c2fd8846b', '32ecd0a88f1c40fe82e40ce796572e34', 81.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (141, '832308d2000d4d1fbf31c8d791497cf3', '32ecd0a88f1c40fe82e40ce796572e34', 81.60, 997, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (142, '1017f93fc41c45a6a3b8078ba3e778e5', '68c99eb3565f499890ebaf9545353438', 372.96, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (143, '144f6c169af64ed19e27347900febac1', '68c99eb3565f499890ebaf9545353438', 372.96, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (144, 'febefdc301844bff9f40127c2fd8846b', '68c99eb3565f499890ebaf9545353438', 372.96, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (145, '832308d2000d4d1fbf31c8d791497cf3', '68c99eb3565f499890ebaf9545353438', 372.96, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (146, '1017f93fc41c45a6a3b8078ba3e778e5', '3b9c5bd0fd454ca4b7217d5c094b290f', 244.32, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (147, '144f6c169af64ed19e27347900febac1', '3b9c5bd0fd454ca4b7217d5c094b290f', 244.32, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (148, 'febefdc301844bff9f40127c2fd8846b', '3b9c5bd0fd454ca4b7217d5c094b290f', 244.32, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (149, '832308d2000d4d1fbf31c8d791497cf3', '3b9c5bd0fd454ca4b7217d5c094b290f', 244.32, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (150, '1017f93fc41c45a6a3b8078ba3e778e5', '638d4d072f06458a8c9014f5ae31a68d', 330.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (151, '144f6c169af64ed19e27347900febac1', '638d4d072f06458a8c9014f5ae31a68d', 330.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (152, 'febefdc301844bff9f40127c2fd8846b', '638d4d072f06458a8c9014f5ae31a68d', 330.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (153, '832308d2000d4d1fbf31c8d791497cf3', '638d4d072f06458a8c9014f5ae31a68d', 330.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (154, '1017f93fc41c45a6a3b8078ba3e778e5', '95b1e97e6e64419ba5fade025c2ef00f', 407.04, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (155, '144f6c169af64ed19e27347900febac1', '95b1e97e6e64419ba5fade025c2ef00f', 407.04, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (156, 'febefdc301844bff9f40127c2fd8846b', '95b1e97e6e64419ba5fade025c2ef00f', 407.04, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (157, '832308d2000d4d1fbf31c8d791497cf3', '95b1e97e6e64419ba5fade025c2ef00f', 407.04, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (158, '1017f93fc41c45a6a3b8078ba3e778e5', 'd2ab314272804c5b98fd4276d04dc838', 239.04, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (159, '144f6c169af64ed19e27347900febac1', 'd2ab314272804c5b98fd4276d04dc838', 239.04, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (160, 'febefdc301844bff9f40127c2fd8846b', 'd2ab314272804c5b98fd4276d04dc838', 239.04, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (161, '832308d2000d4d1fbf31c8d791497cf3', 'd2ab314272804c5b98fd4276d04dc838', 239.04, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (162, '1017f93fc41c45a6a3b8078ba3e778e5', 'a21211eca70e43508f383175c86379b7', 328.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (163, '144f6c169af64ed19e27347900febac1', 'a21211eca70e43508f383175c86379b7', 328.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (164, 'febefdc301844bff9f40127c2fd8846b', '33f9ea4aadad4bcb9c14e0883cbffd05', 153.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (165, '832308d2000d4d1fbf31c8d791497cf3', '33f9ea4aadad4bcb9c14e0883cbffd05', 153.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (166, '1017f93fc41c45a6a3b8078ba3e778e5', '33f9ea4aadad4bcb9c14e0883cbffd05', 153.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (167, '144f6c169af64ed19e27347900febac1', '33f9ea4aadad4bcb9c14e0883cbffd05', 153.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (168, 'febefdc301844bff9f40127c2fd8846b', '7427dee2cce34900a5c83e8459ffe56b', 292.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (169, '832308d2000d4d1fbf31c8d791497cf3', '7427dee2cce34900a5c83e8459ffe56b', 292.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (170, '1017f93fc41c45a6a3b8078ba3e778e5', '7427dee2cce34900a5c83e8459ffe56b', 292.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (171, '144f6c169af64ed19e27347900febac1', '7427dee2cce34900a5c83e8459ffe56b', 292.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (172, 'febefdc301844bff9f40127c2fd8846b', '9bc484e25e18466a86489e068a246460', 93.60, 996, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (173, '832308d2000d4d1fbf31c8d791497cf3', '9bc484e25e18466a86489e068a246460', 93.60, 42, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (174, '1017f93fc41c45a6a3b8078ba3e778e5', '9bc484e25e18466a86489e068a246460', 93.60, 995, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (175, '144f6c169af64ed19e27347900febac1', '9bc484e25e18466a86489e068a246460', 93.60, 996, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (176, 'febefdc301844bff9f40127c2fd8846b', 'a456157078c9493bb1463f7b572ea06f', 43.20, 998, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (177, '832308d2000d4d1fbf31c8d791497cf3', 'a456157078c9493bb1463f7b572ea06f', 43.20, 998, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (178, '1017f93fc41c45a6a3b8078ba3e778e5', 'a456157078c9493bb1463f7b572ea06f', 43.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (179, '144f6c169af64ed19e27347900febac1', 'a456157078c9493bb1463f7b572ea06f', 43.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (180, 'febefdc301844bff9f40127c2fd8846b', 'e849f2c9d4174c6a8bc5b75424c16bcd', 43.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (181, '832308d2000d4d1fbf31c8d791497cf3', 'e849f2c9d4174c6a8bc5b75424c16bcd', 43.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (182, '1017f93fc41c45a6a3b8078ba3e778e5', 'e849f2c9d4174c6a8bc5b75424c16bcd', 43.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (183, '144f6c169af64ed19e27347900febac1', 'e849f2c9d4174c6a8bc5b75424c16bcd', 43.20, 997, 1, '2023-03-31 09:59:27', '2023-05-15 08:55:58');
INSERT INTO `goods_store` VALUES (184, 'febefdc301844bff9f40127c2fd8846b', '24da267a6b4d43af835a05fd9e3d8404', 79.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (185, '832308d2000d4d1fbf31c8d791497cf3', '24da267a6b4d43af835a05fd9e3d8404', 79.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (186, '1017f93fc41c45a6a3b8078ba3e778e5', '24da267a6b4d43af835a05fd9e3d8404', 79.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (187, '144f6c169af64ed19e27347900febac1', '24da267a6b4d43af835a05fd9e3d8404', 79.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (188, 'febefdc301844bff9f40127c2fd8846b', 'ac2345f5ecb446e79a33155598079a32', 79.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (189, '832308d2000d4d1fbf31c8d791497cf3', 'ac2345f5ecb446e79a33155598079a32', 79.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (190, '1017f93fc41c45a6a3b8078ba3e778e5', 'ac2345f5ecb446e79a33155598079a32', 79.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (191, '144f6c169af64ed19e27347900febac1', 'ac2345f5ecb446e79a33155598079a32', 79.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (192, 'febefdc301844bff9f40127c2fd8846b', 'b0a765e9e417457f9738be66fcdee3f2', 74.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (193, '832308d2000d4d1fbf31c8d791497cf3', 'b0a765e9e417457f9738be66fcdee3f2', 74.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (194, '1017f93fc41c45a6a3b8078ba3e778e5', 'b0a765e9e417457f9738be66fcdee3f2', 74.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (195, '144f6c169af64ed19e27347900febac1', 'b0a765e9e417457f9738be66fcdee3f2', 74.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (196, 'febefdc301844bff9f40127c2fd8846b', 'fd37d1bf188f44a588efa1761b47ae2d', 74.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (197, '832308d2000d4d1fbf31c8d791497cf3', 'fd37d1bf188f44a588efa1761b47ae2d', 74.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (198, '1017f93fc41c45a6a3b8078ba3e778e5', 'fd37d1bf188f44a588efa1761b47ae2d', 74.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (199, '144f6c169af64ed19e27347900febac1', 'fd37d1bf188f44a588efa1761b47ae2d', 74.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (200, 'febefdc301844bff9f40127c2fd8846b', '738a0e245d6e47f9b6501bc6e0a5c0f4', 110.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (201, '832308d2000d4d1fbf31c8d791497cf3', '738a0e245d6e47f9b6501bc6e0a5c0f4', 110.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (202, '1017f93fc41c45a6a3b8078ba3e778e5', '738a0e245d6e47f9b6501bc6e0a5c0f4', 110.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (203, '144f6c169af64ed19e27347900febac1', '738a0e245d6e47f9b6501bc6e0a5c0f4', 110.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (204, 'febefdc301844bff9f40127c2fd8846b', 'f6197fe4652a4ed09786dbd94da2a0af', 110.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (205, '832308d2000d4d1fbf31c8d791497cf3', 'f6197fe4652a4ed09786dbd94da2a0af', 110.40, 994, 1, '2023-03-31 09:59:27', '2023-05-15 08:55:58');
INSERT INTO `goods_store` VALUES (206, '1017f93fc41c45a6a3b8078ba3e778e5', 'f6197fe4652a4ed09786dbd94da2a0af', 110.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (207, '144f6c169af64ed19e27347900febac1', 'f6197fe4652a4ed09786dbd94da2a0af', 110.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (208, 'febefdc301844bff9f40127c2fd8846b', 'dbc9c69427b44e16bce77052b610c20b', 110.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (209, '832308d2000d4d1fbf31c8d791497cf3', 'dbc9c69427b44e16bce77052b610c20b', 110.40, 996, 1, '2023-03-31 09:59:27', '2023-05-15 08:55:58');
INSERT INTO `goods_store` VALUES (210, '1017f93fc41c45a6a3b8078ba3e778e5', 'dbc9c69427b44e16bce77052b610c20b', 110.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (211, '144f6c169af64ed19e27347900febac1', 'dbc9c69427b44e16bce77052b610c20b', 110.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (212, 'febefdc301844bff9f40127c2fd8846b', 'eb88efd402304cbd80ada5c92e78c904', 91.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (213, '832308d2000d4d1fbf31c8d791497cf3', 'eb88efd402304cbd80ada5c92e78c904', 91.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (214, '1017f93fc41c45a6a3b8078ba3e778e5', 'eb88efd402304cbd80ada5c92e78c904', 91.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (215, '144f6c169af64ed19e27347900febac1', 'eb88efd402304cbd80ada5c92e78c904', 91.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (216, 'febefdc301844bff9f40127c2fd8846b', '737a730e730d4882830af119a29537bb', 191.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (217, '832308d2000d4d1fbf31c8d791497cf3', '737a730e730d4882830af119a29537bb', 191.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (218, '1017f93fc41c45a6a3b8078ba3e778e5', '737a730e730d4882830af119a29537bb', 191.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (219, '144f6c169af64ed19e27347900febac1', '737a730e730d4882830af119a29537bb', 191.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (220, 'febefdc301844bff9f40127c2fd8846b', 'fa61586d51134267832c37ac94f11be8', 33.60, 1004, 1, '2023-03-31 09:59:27', '2023-04-06 16:55:51');
INSERT INTO `goods_store` VALUES (221, '832308d2000d4d1fbf31c8d791497cf3', 'fa61586d51134267832c37ac94f11be8', 33.60, 999, 1, '2023-03-31 09:59:27', '2023-04-06 16:51:33');
INSERT INTO `goods_store` VALUES (222, '1017f93fc41c45a6a3b8078ba3e778e5', 'fa61586d51134267832c37ac94f11be8', 33.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (223, '144f6c169af64ed19e27347900febac1', 'fa61586d51134267832c37ac94f11be8', 33.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (224, 'febefdc301844bff9f40127c2fd8846b', 'b96d0b9010cb4ef69a027341785f0f09', 56.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (225, '832308d2000d4d1fbf31c8d791497cf3', 'b96d0b9010cb4ef69a027341785f0f09', 56.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (226, '1017f93fc41c45a6a3b8078ba3e778e5', 'b96d0b9010cb4ef69a027341785f0f09', 56.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (227, '144f6c169af64ed19e27347900febac1', 'b96d0b9010cb4ef69a027341785f0f09', 56.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (228, 'febefdc301844bff9f40127c2fd8846b', 'c096e1e874004c4eb39602fbbef5d477', 56.64, 0, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (229, '832308d2000d4d1fbf31c8d791497cf3', 'c096e1e874004c4eb39602fbbef5d477', 56.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (230, '1017f93fc41c45a6a3b8078ba3e778e5', 'c096e1e874004c4eb39602fbbef5d477', 56.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (231, '144f6c169af64ed19e27347900febac1', 'c096e1e874004c4eb39602fbbef5d477', 57.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (232, 'febefdc301844bff9f40127c2fd8846b', '2e2ed6cd61904623817a822cb6426a05', 56.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (233, '832308d2000d4d1fbf31c8d791497cf3', '2e2ed6cd61904623817a822cb6426a05', 56.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (234, '1017f93fc41c45a6a3b8078ba3e778e5', '2e2ed6cd61904623817a822cb6426a05', 56.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (235, '144f6c169af64ed19e27347900febac1', '2e2ed6cd61904623817a822cb6426a05', 56.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (236, 'febefdc301844bff9f40127c2fd8846b', '925eda52282b45deb9f74711a56261d1', 90.72, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (237, '832308d2000d4d1fbf31c8d791497cf3', '925eda52282b45deb9f74711a56261d1', 90.72, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (238, '1017f93fc41c45a6a3b8078ba3e778e5', '925eda52282b45deb9f74711a56261d1', 90.72, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (239, '144f6c169af64ed19e27347900febac1', '925eda52282b45deb9f74711a56261d1', 90.72, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (240, 'febefdc301844bff9f40127c2fd8846b', '498b07069d2e4031a62b34927e8f16f0', 90.72, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (241, '832308d2000d4d1fbf31c8d791497cf3', '498b07069d2e4031a62b34927e8f16f0', 90.72, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (242, '1017f93fc41c45a6a3b8078ba3e778e5', '498b07069d2e4031a62b34927e8f16f0', 90.72, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (243, '144f6c169af64ed19e27347900febac1', '498b07069d2e4031a62b34927e8f16f0', 90.72, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (244, 'febefdc301844bff9f40127c2fd8846b', 'f45ba8fc7a6e4dbba87cd561ae226b78', 62.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (245, '832308d2000d4d1fbf31c8d791497cf3', 'f45ba8fc7a6e4dbba87cd561ae226b78', 62.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (246, '1017f93fc41c45a6a3b8078ba3e778e5', 'f45ba8fc7a6e4dbba87cd561ae226b78', 62.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (247, '144f6c169af64ed19e27347900febac1', 'f45ba8fc7a6e4dbba87cd561ae226b78', 62.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (248, 'febefdc301844bff9f40127c2fd8846b', 'aa7840d2a64142e7a42b65422c292380', 62.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (249, '832308d2000d4d1fbf31c8d791497cf3', 'aa7840d2a64142e7a42b65422c292380', 62.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (250, '1017f93fc41c45a6a3b8078ba3e778e5', 'aa7840d2a64142e7a42b65422c292380', 62.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (251, '144f6c169af64ed19e27347900febac1', 'aa7840d2a64142e7a42b65422c292380', 62.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (252, 'febefdc301844bff9f40127c2fd8846b', 'bb36194793044e72aea8b8225fa3464a', 100.32, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (253, '832308d2000d4d1fbf31c8d791497cf3', 'bb36194793044e72aea8b8225fa3464a', 100.32, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (254, '1017f93fc41c45a6a3b8078ba3e778e5', 'bb36194793044e72aea8b8225fa3464a', 100.32, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (255, '144f6c169af64ed19e27347900febac1', 'bb36194793044e72aea8b8225fa3464a', 100.32, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (256, 'febefdc301844bff9f40127c2fd8846b', '07287166fa454be18e8c6142efb46a39', 100.32, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (257, '832308d2000d4d1fbf31c8d791497cf3', '07287166fa454be18e8c6142efb46a39', 100.32, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (258, '1017f93fc41c45a6a3b8078ba3e778e5', '07287166fa454be18e8c6142efb46a39', 100.32, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (259, '144f6c169af64ed19e27347900febac1', '07287166fa454be18e8c6142efb46a39', 100.32, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (260, 'febefdc301844bff9f40127c2fd8846b', 'c3d58cd202154790af95c542e1a8b54c', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (261, '832308d2000d4d1fbf31c8d791497cf3', 'c3d58cd202154790af95c542e1a8b54c', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (262, '1017f93fc41c45a6a3b8078ba3e778e5', 'c3d58cd202154790af95c542e1a8b54c', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (263, '144f6c169af64ed19e27347900febac1', 'c3d58cd202154790af95c542e1a8b54c', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (264, 'febefdc301844bff9f40127c2fd8846b', 'b0267fc794ef4a66855652443083c795', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (265, '832308d2000d4d1fbf31c8d791497cf3', 'b0267fc794ef4a66855652443083c795', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (266, '1017f93fc41c45a6a3b8078ba3e778e5', 'b0267fc794ef4a66855652443083c795', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (267, '144f6c169af64ed19e27347900febac1', 'b0267fc794ef4a66855652443083c795', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (268, 'febefdc301844bff9f40127c2fd8846b', 'cffedbeca45f4f7bba8df8e06ad647c3', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (269, '832308d2000d4d1fbf31c8d791497cf3', 'cffedbeca45f4f7bba8df8e06ad647c3', 24.00, 998, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (270, '1017f93fc41c45a6a3b8078ba3e778e5', 'cffedbeca45f4f7bba8df8e06ad647c3', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (271, '144f6c169af64ed19e27347900febac1', 'cffedbeca45f4f7bba8df8e06ad647c3', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (272, 'febefdc301844bff9f40127c2fd8846b', 'f2fb8c9a456c465da7172f065de8704a', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (273, '832308d2000d4d1fbf31c8d791497cf3', 'f2fb8c9a456c465da7172f065de8704a', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (274, '1017f93fc41c45a6a3b8078ba3e778e5', 'f2fb8c9a456c465da7172f065de8704a', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (275, '144f6c169af64ed19e27347900febac1', 'f2fb8c9a456c465da7172f065de8704a', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (276, 'febefdc301844bff9f40127c2fd8846b', 'c41624f99cdd46d5a56b5c2c190eb02c', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (277, '832308d2000d4d1fbf31c8d791497cf3', 'c41624f99cdd46d5a56b5c2c190eb02c', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (278, '1017f93fc41c45a6a3b8078ba3e778e5', 'c41624f99cdd46d5a56b5c2c190eb02c', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (279, '144f6c169af64ed19e27347900febac1', 'c41624f99cdd46d5a56b5c2c190eb02c', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (280, 'febefdc301844bff9f40127c2fd8846b', '858be1887e544acb95172f0f0d5fc1fd', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (281, '832308d2000d4d1fbf31c8d791497cf3', '858be1887e544acb95172f0f0d5fc1fd', 24.00, 997, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (282, '1017f93fc41c45a6a3b8078ba3e778e5', '858be1887e544acb95172f0f0d5fc1fd', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (283, '144f6c169af64ed19e27347900febac1', '858be1887e544acb95172f0f0d5fc1fd', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (284, 'febefdc301844bff9f40127c2fd8846b', 'd0b97ae7961d4a5cb099c4c709c8aa8a', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (285, '832308d2000d4d1fbf31c8d791497cf3', 'd0b97ae7961d4a5cb099c4c709c8aa8a', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (286, '1017f93fc41c45a6a3b8078ba3e778e5', 'd0b97ae7961d4a5cb099c4c709c8aa8a', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (287, '144f6c169af64ed19e27347900febac1', 'd0b97ae7961d4a5cb099c4c709c8aa8a', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (288, 'febefdc301844bff9f40127c2fd8846b', 'f54b150480af4c1788baba75fd401f2f', 40.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (289, '832308d2000d4d1fbf31c8d791497cf3', 'f54b150480af4c1788baba75fd401f2f', 40.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (290, '1017f93fc41c45a6a3b8078ba3e778e5', 'f54b150480af4c1788baba75fd401f2f', 40.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (291, '144f6c169af64ed19e27347900febac1', 'f54b150480af4c1788baba75fd401f2f', 40.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (292, 'febefdc301844bff9f40127c2fd8846b', 'f07a7f33ca7c465b84508c17844d3155', 40.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (293, '832308d2000d4d1fbf31c8d791497cf3', 'f07a7f33ca7c465b84508c17844d3155', 40.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (294, '1017f93fc41c45a6a3b8078ba3e778e5', 'f07a7f33ca7c465b84508c17844d3155', 40.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (295, '144f6c169af64ed19e27347900febac1', 'f07a7f33ca7c465b84508c17844d3155', 40.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (296, 'febefdc301844bff9f40127c2fd8846b', '15729d1dc0834cd0b04276055b2d30ad', 23.04, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (297, '832308d2000d4d1fbf31c8d791497cf3', '15729d1dc0834cd0b04276055b2d30ad', 23.04, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (298, '1017f93fc41c45a6a3b8078ba3e778e5', '15729d1dc0834cd0b04276055b2d30ad', 23.04, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (299, '144f6c169af64ed19e27347900febac1', '15729d1dc0834cd0b04276055b2d30ad', 23.04, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (300, 'febefdc301844bff9f40127c2fd8846b', '3f17ce33f4b44ce28e9ba171d0f4f526', 86.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (301, '832308d2000d4d1fbf31c8d791497cf3', '3f17ce33f4b44ce28e9ba171d0f4f526', 86.40, 996, 1, '2023-03-31 09:59:27', '2023-04-07 15:11:29');
INSERT INTO `goods_store` VALUES (302, '1017f93fc41c45a6a3b8078ba3e778e5', '3f17ce33f4b44ce28e9ba171d0f4f526', 86.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (303, '144f6c169af64ed19e27347900febac1', '3f17ce33f4b44ce28e9ba171d0f4f526', 86.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (304, 'febefdc301844bff9f40127c2fd8846b', '2e7a3125f438410e9c1c3b40a7c170fa', 96.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (305, '832308d2000d4d1fbf31c8d791497cf3', '2e7a3125f438410e9c1c3b40a7c170fa', 96.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (306, '1017f93fc41c45a6a3b8078ba3e778e5', '2e7a3125f438410e9c1c3b40a7c170fa', 96.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (307, '144f6c169af64ed19e27347900febac1', '2e7a3125f438410e9c1c3b40a7c170fa', 96.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (308, 'febefdc301844bff9f40127c2fd8846b', '1e8e882c45824ddcae8e4a21c8610fa8', 96.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (309, '832308d2000d4d1fbf31c8d791497cf3', '1e8e882c45824ddcae8e4a21c8610fa8', 96.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (310, '1017f93fc41c45a6a3b8078ba3e778e5', '1e8e882c45824ddcae8e4a21c8610fa8', 96.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (311, '144f6c169af64ed19e27347900febac1', '1e8e882c45824ddcae8e4a21c8610fa8', 96.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (312, 'febefdc301844bff9f40127c2fd8846b', '5d10a42b834a4dc48ca23676b2902887', 14.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (313, '832308d2000d4d1fbf31c8d791497cf3', '5d10a42b834a4dc48ca23676b2902887', 14.40, 996, 1, '2023-03-31 09:59:27', '2023-04-07 12:34:15');
INSERT INTO `goods_store` VALUES (314, '1017f93fc41c45a6a3b8078ba3e778e5', 'c21ef37b6f714ed3b9ad6bacbace6fcd', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (315, '144f6c169af64ed19e27347900febac1', 'c21ef37b6f714ed3b9ad6bacbace6fcd', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (316, 'febefdc301844bff9f40127c2fd8846b', '4cc384339b404dae870983efd3394057', 19.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (317, '832308d2000d4d1fbf31c8d791497cf3', '4cc384339b404dae870983efd3394057', 19.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (318, '1017f93fc41c45a6a3b8078ba3e778e5', '4cc384339b404dae870983efd3394057', 19.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (319, '144f6c169af64ed19e27347900febac1', '4cc384339b404dae870983efd3394057', 19.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (320, 'febefdc301844bff9f40127c2fd8846b', '249da0b225aa49ce8a7e9482fda971a7', 19.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (321, '832308d2000d4d1fbf31c8d791497cf3', '249da0b225aa49ce8a7e9482fda971a7', 19.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (322, '1017f93fc41c45a6a3b8078ba3e778e5', '9fbe4575ea7a4f8e862b8395c3df9362', 31.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (323, '144f6c169af64ed19e27347900febac1', '9fbe4575ea7a4f8e862b8395c3df9362', 31.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (324, 'febefdc301844bff9f40127c2fd8846b', '9fbe4575ea7a4f8e862b8395c3df9362', 31.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (325, '832308d2000d4d1fbf31c8d791497cf3', '9fbe4575ea7a4f8e862b8395c3df9362', 31.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (326, '1017f93fc41c45a6a3b8078ba3e778e5', 'ee41b9fbdbc045fb96fe5916e757cfc6', 57.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (327, '144f6c169af64ed19e27347900febac1', 'ee41b9fbdbc045fb96fe5916e757cfc6', 57.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (328, 'febefdc301844bff9f40127c2fd8846b', '5ddc33b8d5f9423fb4860616bf489fbf', 48.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (329, '832308d2000d4d1fbf31c8d791497cf3', '5ddc33b8d5f9423fb4860616bf489fbf', 48.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (330, '1017f93fc41c45a6a3b8078ba3e778e5', '5ddc33b8d5f9423fb4860616bf489fbf', 48.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (331, '144f6c169af64ed19e27347900febac1', '5ddc33b8d5f9423fb4860616bf489fbf', 48.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (332, 'febefdc301844bff9f40127c2fd8846b', 'f92798bbd3014da4b246dc8f857a5fa2', 14.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (333, '832308d2000d4d1fbf31c8d791497cf3', 'f92798bbd3014da4b246dc8f857a5fa2', 14.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (334, '1017f93fc41c45a6a3b8078ba3e778e5', '7d29a9e9c54e4ce5ab593945a8c94edf', 31.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (335, '144f6c169af64ed19e27347900febac1', '7d29a9e9c54e4ce5ab593945a8c94edf', 31.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (336, 'febefdc301844bff9f40127c2fd8846b', '7d29a9e9c54e4ce5ab593945a8c94edf', 31.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (337, '832308d2000d4d1fbf31c8d791497cf3', '7d29a9e9c54e4ce5ab593945a8c94edf', 31.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (338, '1017f93fc41c45a6a3b8078ba3e778e5', '16caad5d3da943fdba228944f9249019', 18.24, 994, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (339, '144f6c169af64ed19e27347900febac1', '16caad5d3da943fdba228944f9249019', 18.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (340, 'febefdc301844bff9f40127c2fd8846b', '16caad5d3da943fdba228944f9249019', 18.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (341, '832308d2000d4d1fbf31c8d791497cf3', '16caad5d3da943fdba228944f9249019', 18.24, 996, 1, '2023-03-31 09:59:27', '2023-05-05 11:38:19');
INSERT INTO `goods_store` VALUES (342, '1017f93fc41c45a6a3b8078ba3e778e5', '9bcf993ac2394fe797580f38b1cfb1a8', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (343, '144f6c169af64ed19e27347900febac1', '9bcf993ac2394fe797580f38b1cfb1a8', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (344, 'febefdc301844bff9f40127c2fd8846b', '9bcf993ac2394fe797580f38b1cfb1a8', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (345, '832308d2000d4d1fbf31c8d791497cf3', '9bcf993ac2394fe797580f38b1cfb1a8', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (346, '1017f93fc41c45a6a3b8078ba3e778e5', '490ebcbe76834f659f0d2e95ce3342e7', 63.36, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (347, '144f6c169af64ed19e27347900febac1', '490ebcbe76834f659f0d2e95ce3342e7', 63.36, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (348, 'febefdc301844bff9f40127c2fd8846b', '490ebcbe76834f659f0d2e95ce3342e7', 63.36, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (349, '832308d2000d4d1fbf31c8d791497cf3', '490ebcbe76834f659f0d2e95ce3342e7', 63.36, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (350, '1017f93fc41c45a6a3b8078ba3e778e5', '07e3fab379244ff5b6f78f6ad41e75ad', 12.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (351, '144f6c169af64ed19e27347900febac1', '07e3fab379244ff5b6f78f6ad41e75ad', 12.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (352, 'febefdc301844bff9f40127c2fd8846b', '07e3fab379244ff5b6f78f6ad41e75ad', 12.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (353, '832308d2000d4d1fbf31c8d791497cf3', '07e3fab379244ff5b6f78f6ad41e75ad', 12.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (354, '1017f93fc41c45a6a3b8078ba3e778e5', '4582ea22e68647368a7f0ffcb25efe2c', 23.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (355, '144f6c169af64ed19e27347900febac1', '4582ea22e68647368a7f0ffcb25efe2c', 23.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (356, 'febefdc301844bff9f40127c2fd8846b', '4582ea22e68647368a7f0ffcb25efe2c', 23.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (357, '832308d2000d4d1fbf31c8d791497cf3', '4582ea22e68647368a7f0ffcb25efe2c', 23.52, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (358, '1017f93fc41c45a6a3b8078ba3e778e5', '6d78ac772ec84a149d6df33e53699c3b', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (359, '144f6c169af64ed19e27347900febac1', '6d78ac772ec84a149d6df33e53699c3b', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (360, 'febefdc301844bff9f40127c2fd8846b', '6d78ac772ec84a149d6df33e53699c3b', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (361, '832308d2000d4d1fbf31c8d791497cf3', '6d78ac772ec84a149d6df33e53699c3b', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (362, '1017f93fc41c45a6a3b8078ba3e778e5', '5e4251cbcf77491994f812f650b99388', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (363, '144f6c169af64ed19e27347900febac1', '5e4251cbcf77491994f812f650b99388', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (364, 'febefdc301844bff9f40127c2fd8846b', '5e4251cbcf77491994f812f650b99388', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (365, '832308d2000d4d1fbf31c8d791497cf3', '5e4251cbcf77491994f812f650b99388', 21.60, 999, 1, '2023-03-31 09:59:27', '2023-05-17 13:24:24');
INSERT INTO `goods_store` VALUES (366, '1017f93fc41c45a6a3b8078ba3e778e5', '870bdc36ff044951b6650ceef339ac8f', 42.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (367, '144f6c169af64ed19e27347900febac1', '870bdc36ff044951b6650ceef339ac8f', 42.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (368, 'febefdc301844bff9f40127c2fd8846b', '870bdc36ff044951b6650ceef339ac8f', 42.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (369, '832308d2000d4d1fbf31c8d791497cf3', '870bdc36ff044951b6650ceef339ac8f', 42.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (370, '1017f93fc41c45a6a3b8078ba3e778e5', 'a16a86ecd361421f92e61bf3b0ae768c', 42.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (371, '144f6c169af64ed19e27347900febac1', 'a16a86ecd361421f92e61bf3b0ae768c', 42.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (372, 'febefdc301844bff9f40127c2fd8846b', 'a16a86ecd361421f92e61bf3b0ae768c', 42.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (373, '832308d2000d4d1fbf31c8d791497cf3', 'a16a86ecd361421f92e61bf3b0ae768c', 42.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (374, '1017f93fc41c45a6a3b8078ba3e778e5', 'f446cce840784008b072bcf6b0650ee0', 272.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (375, '144f6c169af64ed19e27347900febac1', 'f446cce840784008b072bcf6b0650ee0', 272.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (376, 'febefdc301844bff9f40127c2fd8846b', 'f446cce840784008b072bcf6b0650ee0', 272.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (377, '832308d2000d4d1fbf31c8d791497cf3', 'f446cce840784008b072bcf6b0650ee0', 272.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (378, '1017f93fc41c45a6a3b8078ba3e778e5', 'd519569e8d87410b8521435a26cb9933', 16.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (379, '144f6c169af64ed19e27347900febac1', 'd519569e8d87410b8521435a26cb9933', 16.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (380, 'febefdc301844bff9f40127c2fd8846b', 'd519569e8d87410b8521435a26cb9933', 16.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (381, '832308d2000d4d1fbf31c8d791497cf3', 'd519569e8d87410b8521435a26cb9933', 16.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (382, '1017f93fc41c45a6a3b8078ba3e778e5', '706040790f5e4ff69a20eedb38c56549', 32.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (383, '144f6c169af64ed19e27347900febac1', '706040790f5e4ff69a20eedb38c56549', 32.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (384, 'febefdc301844bff9f40127c2fd8846b', '706040790f5e4ff69a20eedb38c56549', 32.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (385, '832308d2000d4d1fbf31c8d791497cf3', '706040790f5e4ff69a20eedb38c56549', 32.64, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (386, '1017f93fc41c45a6a3b8078ba3e778e5', 'cc02635cdfdc4defbad3a0d6c1b41964', 43.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (387, '144f6c169af64ed19e27347900febac1', 'cc02635cdfdc4defbad3a0d6c1b41964', 43.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (388, 'febefdc301844bff9f40127c2fd8846b', 'cc02635cdfdc4defbad3a0d6c1b41964', 43.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (389, '832308d2000d4d1fbf31c8d791497cf3', 'cc02635cdfdc4defbad3a0d6c1b41964', 43.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (390, '1017f93fc41c45a6a3b8078ba3e778e5', 'd4d1a3bd9e994f64ad7c2ee8eff69ee1', 66.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (391, '144f6c169af64ed19e27347900febac1', 'd4d1a3bd9e994f64ad7c2ee8eff69ee1', 66.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (392, 'febefdc301844bff9f40127c2fd8846b', 'd4d1a3bd9e994f64ad7c2ee8eff69ee1', 66.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (393, '832308d2000d4d1fbf31c8d791497cf3', 'd4d1a3bd9e994f64ad7c2ee8eff69ee1', 66.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (394, '1017f93fc41c45a6a3b8078ba3e778e5', '308502b9df0149b7ad07919fcfbc7127', 29.28, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (395, '144f6c169af64ed19e27347900febac1', '308502b9df0149b7ad07919fcfbc7127', 29.28, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (396, 'febefdc301844bff9f40127c2fd8846b', '308502b9df0149b7ad07919fcfbc7127', 29.28, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (397, '832308d2000d4d1fbf31c8d791497cf3', '308502b9df0149b7ad07919fcfbc7127', 29.28, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (398, '1017f93fc41c45a6a3b8078ba3e778e5', '8efd0cddfba14d469f68d3fbdf7bd146', 112.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (399, '144f6c169af64ed19e27347900febac1', '8efd0cddfba14d469f68d3fbdf7bd146', 112.80, 0, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (400, 'febefdc301844bff9f40127c2fd8846b', '8efd0cddfba14d469f68d3fbdf7bd146', 112.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (401, '832308d2000d4d1fbf31c8d791497cf3', '8efd0cddfba14d469f68d3fbdf7bd146', 112.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (402, '1017f93fc41c45a6a3b8078ba3e778e5', 'ff8392fcfbbe4bc3b7243218996fb1de', 54.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (403, '144f6c169af64ed19e27347900febac1', 'ff8392fcfbbe4bc3b7243218996fb1de', 54.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (404, 'febefdc301844bff9f40127c2fd8846b', 'ff8392fcfbbe4bc3b7243218996fb1de', 54.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (405, '832308d2000d4d1fbf31c8d791497cf3', 'ff8392fcfbbe4bc3b7243218996fb1de', 54.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (406, '1017f93fc41c45a6a3b8078ba3e778e5', '0fb231f449844c65be8e4ea6d012bacf', 27.84, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (407, '144f6c169af64ed19e27347900febac1', '0fb231f449844c65be8e4ea6d012bacf', 27.84, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (408, 'febefdc301844bff9f40127c2fd8846b', '0fb231f449844c65be8e4ea6d012bacf', 27.84, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (409, '832308d2000d4d1fbf31c8d791497cf3', '0fb231f449844c65be8e4ea6d012bacf', 27.84, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (410, '1017f93fc41c45a6a3b8078ba3e778e5', '79f68fc662d94ffaae352be967633760', 18.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (411, '144f6c169af64ed19e27347900febac1', '79f68fc662d94ffaae352be967633760', 18.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (412, 'febefdc301844bff9f40127c2fd8846b', '79f68fc662d94ffaae352be967633760', 18.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (413, '832308d2000d4d1fbf31c8d791497cf3', '79f68fc662d94ffaae352be967633760', 18.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (414, '1017f93fc41c45a6a3b8078ba3e778e5', '01833ffb827d4da48dfe8a2528c0020e', 18.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (415, '144f6c169af64ed19e27347900febac1', '01833ffb827d4da48dfe8a2528c0020e', 18.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (416, 'febefdc301844bff9f40127c2fd8846b', '01833ffb827d4da48dfe8a2528c0020e', 18.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (417, '832308d2000d4d1fbf31c8d791497cf3', '01833ffb827d4da48dfe8a2528c0020e', 18.24, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (418, '1017f93fc41c45a6a3b8078ba3e778e5', '58686e68a3db47f3a723a1acb08257eb', 220.80, 997, 1, '2023-03-31 09:59:27', '2023-04-12 10:04:29');
INSERT INTO `goods_store` VALUES (419, '144f6c169af64ed19e27347900febac1', '58686e68a3db47f3a723a1acb08257eb', 220.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (420, 'febefdc301844bff9f40127c2fd8846b', '58686e68a3db47f3a723a1acb08257eb', 220.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (421, '832308d2000d4d1fbf31c8d791497cf3', '58686e68a3db47f3a723a1acb08257eb', 220.80, 991, 1, '2023-03-31 09:59:27', '2023-05-05 11:35:40');
INSERT INTO `goods_store` VALUES (422, '1017f93fc41c45a6a3b8078ba3e778e5', '940b6061d34a45f69d3ae7d1e5c8d925', 316.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (423, '144f6c169af64ed19e27347900febac1', '940b6061d34a45f69d3ae7d1e5c8d925', 316.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (424, 'febefdc301844bff9f40127c2fd8846b', '940b6061d34a45f69d3ae7d1e5c8d925', 316.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (425, '832308d2000d4d1fbf31c8d791497cf3', '940b6061d34a45f69d3ae7d1e5c8d925', 316.80, 999, 1, '2023-03-31 09:59:27', '2023-04-12 09:28:10');
INSERT INTO `goods_store` VALUES (426, '1017f93fc41c45a6a3b8078ba3e778e5', 'd69b08093ecb46ed9f94e59a7a1e5246', 316.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (427, '144f6c169af64ed19e27347900febac1', 'd69b08093ecb46ed9f94e59a7a1e5246', 316.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (428, 'febefdc301844bff9f40127c2fd8846b', 'd69b08093ecb46ed9f94e59a7a1e5246', 316.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (429, '832308d2000d4d1fbf31c8d791497cf3', 'd69b08093ecb46ed9f94e59a7a1e5246', 316.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (430, '1017f93fc41c45a6a3b8078ba3e778e5', 'f5aa6e9f55cb4014a13cef7fd0c4127c', 225.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (431, '144f6c169af64ed19e27347900febac1', 'f5aa6e9f55cb4014a13cef7fd0c4127c', 225.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (432, 'febefdc301844bff9f40127c2fd8846b', 'f5aa6e9f55cb4014a13cef7fd0c4127c', 225.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (433, '832308d2000d4d1fbf31c8d791497cf3', 'f5aa6e9f55cb4014a13cef7fd0c4127c', 225.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (434, '1017f93fc41c45a6a3b8078ba3e778e5', '3a166b2cf6204424be4019e9dda7d001', 225.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (435, '144f6c169af64ed19e27347900febac1', '3a166b2cf6204424be4019e9dda7d001', 225.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (436, 'febefdc301844bff9f40127c2fd8846b', '3a166b2cf6204424be4019e9dda7d001', 225.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (437, '832308d2000d4d1fbf31c8d791497cf3', '3a166b2cf6204424be4019e9dda7d001', 225.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (438, '1017f93fc41c45a6a3b8078ba3e778e5', 'ec290854177e4b17a00acdb6c2d81677', 201.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (439, '144f6c169af64ed19e27347900febac1', 'ec290854177e4b17a00acdb6c2d81677', 201.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (440, 'febefdc301844bff9f40127c2fd8846b', 'ec290854177e4b17a00acdb6c2d81677', 201.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (441, '832308d2000d4d1fbf31c8d791497cf3', 'ec290854177e4b17a00acdb6c2d81677', 201.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (442, '1017f93fc41c45a6a3b8078ba3e778e5', 'b1f367961f754c87b9ceed9a40887b21', 201.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (443, '144f6c169af64ed19e27347900febac1', 'b1f367961f754c87b9ceed9a40887b21', 201.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (444, 'febefdc301844bff9f40127c2fd8846b', 'b1f367961f754c87b9ceed9a40887b21', 201.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (445, '832308d2000d4d1fbf31c8d791497cf3', 'b1f367961f754c87b9ceed9a40887b21', 201.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (446, '1017f93fc41c45a6a3b8078ba3e778e5', '9f6e9d7263bd4e4d88ea90b0be58179e', 249.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (447, '144f6c169af64ed19e27347900febac1', '9f6e9d7263bd4e4d88ea90b0be58179e', 249.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (448, 'febefdc301844bff9f40127c2fd8846b', '9f6e9d7263bd4e4d88ea90b0be58179e', 249.60, 997, 1, '2023-03-31 09:59:27', '2023-05-15 14:15:58');
INSERT INTO `goods_store` VALUES (449, '832308d2000d4d1fbf31c8d791497cf3', '9f6e9d7263bd4e4d88ea90b0be58179e', 249.60, 999, 1, '2023-03-31 09:59:27', '2023-05-17 08:53:28');
INSERT INTO `goods_store` VALUES (450, '1017f93fc41c45a6a3b8078ba3e778e5', '3e221bcfbccc456383da96f9e33efa0c', 249.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (451, '144f6c169af64ed19e27347900febac1', '3e221bcfbccc456383da96f9e33efa0c', 249.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (452, 'febefdc301844bff9f40127c2fd8846b', '3e221bcfbccc456383da96f9e33efa0c', 249.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (453, '832308d2000d4d1fbf31c8d791497cf3', '3e221bcfbccc456383da96f9e33efa0c', 249.60, 995, 1, '2023-03-31 09:59:27', '2023-05-16 15:27:31');
INSERT INTO `goods_store` VALUES (454, '1017f93fc41c45a6a3b8078ba3e778e5', 'd6b7310e8bbf4bab81f177144fcad301', 249.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (455, '144f6c169af64ed19e27347900febac1', 'd6b7310e8bbf4bab81f177144fcad301', 249.60, 998, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (456, 'febefdc301844bff9f40127c2fd8846b', 'd6b7310e8bbf4bab81f177144fcad301', 249.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (457, '832308d2000d4d1fbf31c8d791497cf3', 'd6b7310e8bbf4bab81f177144fcad301', 249.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (458, '1017f93fc41c45a6a3b8078ba3e778e5', 'ebb769fc418d4f4e9c3dff35d3ddcc3c', 134.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (459, '144f6c169af64ed19e27347900febac1', 'ebb769fc418d4f4e9c3dff35d3ddcc3c', 134.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (460, 'febefdc301844bff9f40127c2fd8846b', 'ebb769fc418d4f4e9c3dff35d3ddcc3c', 134.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (461, '832308d2000d4d1fbf31c8d791497cf3', 'ebb769fc418d4f4e9c3dff35d3ddcc3c', 134.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (462, '1017f93fc41c45a6a3b8078ba3e778e5', 'f47fddee2b75447a85f7959c683f0071', 134.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (463, '144f6c169af64ed19e27347900febac1', 'f47fddee2b75447a85f7959c683f0071', 134.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (464, 'febefdc301844bff9f40127c2fd8846b', 'f47fddee2b75447a85f7959c683f0071', 134.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (465, '832308d2000d4d1fbf31c8d791497cf3', 'f47fddee2b75447a85f7959c683f0071', 134.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (466, '1017f93fc41c45a6a3b8078ba3e778e5', 'fd45827cb8da46158d3074045b34beb3', 216.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (467, '144f6c169af64ed19e27347900febac1', 'fd45827cb8da46158d3074045b34beb3', 216.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (468, 'febefdc301844bff9f40127c2fd8846b', 'fd45827cb8da46158d3074045b34beb3', 216.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (469, '832308d2000d4d1fbf31c8d791497cf3', 'fd45827cb8da46158d3074045b34beb3', 216.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (470, '1017f93fc41c45a6a3b8078ba3e778e5', 'e65f694df46a4937b94f25115ec02f83', 216.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (471, '144f6c169af64ed19e27347900febac1', 'e65f694df46a4937b94f25115ec02f83', 216.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (472, 'febefdc301844bff9f40127c2fd8846b', 'e65f694df46a4937b94f25115ec02f83', 216.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (473, '832308d2000d4d1fbf31c8d791497cf3', 'e65f694df46a4937b94f25115ec02f83', 216.00, 995, 1, '2023-03-31 09:59:27', '2023-04-07 10:01:16');
INSERT INTO `goods_store` VALUES (474, '1017f93fc41c45a6a3b8078ba3e778e5', '158cfae18bc844879d15177f3e886033', 19.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (475, '144f6c169af64ed19e27347900febac1', '158cfae18bc844879d15177f3e886033', 19.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (476, 'febefdc301844bff9f40127c2fd8846b', '158cfae18bc844879d15177f3e886033', 19.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (477, '832308d2000d4d1fbf31c8d791497cf3', '158cfae18bc844879d15177f3e886033', 19.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (478, '1017f93fc41c45a6a3b8078ba3e778e5', '428db7b0a52b45d39f8495b394f38a43', 19.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (479, '144f6c169af64ed19e27347900febac1', '428db7b0a52b45d39f8495b394f38a43', 19.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (480, 'febefdc301844bff9f40127c2fd8846b', '428db7b0a52b45d39f8495b394f38a43', 19.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (481, '832308d2000d4d1fbf31c8d791497cf3', '428db7b0a52b45d39f8495b394f38a43', 19.20, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (482, '1017f93fc41c45a6a3b8078ba3e778e5', '398e03385e48418880ee520a322a2174', 57.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (483, '144f6c169af64ed19e27347900febac1', '398e03385e48418880ee520a322a2174', 57.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (484, 'febefdc301844bff9f40127c2fd8846b', '398e03385e48418880ee520a322a2174', 57.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (485, '832308d2000d4d1fbf31c8d791497cf3', '398e03385e48418880ee520a322a2174', 57.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (486, '1017f93fc41c45a6a3b8078ba3e778e5', '2858bfb6b4fa4ecfb9a44fcca8d104e3', 14.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (487, '144f6c169af64ed19e27347900febac1', '2858bfb6b4fa4ecfb9a44fcca8d104e3', 14.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (488, 'febefdc301844bff9f40127c2fd8846b', '2858bfb6b4fa4ecfb9a44fcca8d104e3', 14.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (489, '832308d2000d4d1fbf31c8d791497cf3', '2858bfb6b4fa4ecfb9a44fcca8d104e3', 14.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (490, '1017f93fc41c45a6a3b8078ba3e778e5', '4f3a80a835064f889b72dbf240f999f3', 48.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (491, '144f6c169af64ed19e27347900febac1', 'fc8b011e9d744b95850c82bea2f5b024', 4.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (492, 'febefdc301844bff9f40127c2fd8846b', 'fc8b011e9d744b95850c82bea2f5b024', 4.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (493, '832308d2000d4d1fbf31c8d791497cf3', 'fc8b011e9d744b95850c82bea2f5b024', 4.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (494, '1017f93fc41c45a6a3b8078ba3e778e5', 'fc8b011e9d744b95850c82bea2f5b024', 4.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (495, '144f6c169af64ed19e27347900febac1', 'f603355a7c224374ad21003a90bc2786', 4.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (496, 'febefdc301844bff9f40127c2fd8846b', '88ec044990f0463b9e59536725044c0b', 206.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (497, '832308d2000d4d1fbf31c8d791497cf3', '88ec044990f0463b9e59536725044c0b', 206.40, 1000, 1, '2023-03-31 09:59:27', '2023-04-07 10:04:19');
INSERT INTO `goods_store` VALUES (498, '1017f93fc41c45a6a3b8078ba3e778e5', '88ec044990f0463b9e59536725044c0b', 206.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (499, '144f6c169af64ed19e27347900febac1', '88ec044990f0463b9e59536725044c0b', 206.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (500, 'febefdc301844bff9f40127c2fd8846b', 'ad9633af46594e6faa6af98b894b816d', 63.36, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (501, '832308d2000d4d1fbf31c8d791497cf3', 'ad9633af46594e6faa6af98b894b816d', 63.36, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (502, '1017f93fc41c45a6a3b8078ba3e778e5', 'ad9633af46594e6faa6af98b894b816d', 63.36, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (503, '144f6c169af64ed19e27347900febac1', 'ad9633af46594e6faa6af98b894b816d', 63.36, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (504, 'febefdc301844bff9f40127c2fd8846b', '03690d93b1c849ff83651315a36b4389', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (505, '832308d2000d4d1fbf31c8d791497cf3', '03690d93b1c849ff83651315a36b4389', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (506, '1017f93fc41c45a6a3b8078ba3e778e5', '03690d93b1c849ff83651315a36b4389', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (507, '144f6c169af64ed19e27347900febac1', '03690d93b1c849ff83651315a36b4389', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (508, 'febefdc301844bff9f40127c2fd8846b', 'c54ff1fa8f8443e789226f2e287a36ce', 36.00, 997, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (509, '832308d2000d4d1fbf31c8d791497cf3', 'c54ff1fa8f8443e789226f2e287a36ce', 36.00, 1001, 1, '2023-03-31 09:59:27', '2023-04-06 16:55:51');
INSERT INTO `goods_store` VALUES (510, '1017f93fc41c45a6a3b8078ba3e778e5', 'c54ff1fa8f8443e789226f2e287a36ce', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (511, '144f6c169af64ed19e27347900febac1', 'dcfd16649e6046819bc5b921655e8932', 21.60, 998, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (512, 'febefdc301844bff9f40127c2fd8846b', 'dcfd16649e6046819bc5b921655e8932', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (513, '832308d2000d4d1fbf31c8d791497cf3', 'dcfd16649e6046819bc5b921655e8932', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (514, '1017f93fc41c45a6a3b8078ba3e778e5', 'dcfd16649e6046819bc5b921655e8932', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (515, '144f6c169af64ed19e27347900febac1', '81bf2df010f948c4ace9bf50c06139db', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (516, 'febefdc301844bff9f40127c2fd8846b', '81bf2df010f948c4ace9bf50c06139db', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (517, '832308d2000d4d1fbf31c8d791497cf3', '81bf2df010f948c4ace9bf50c06139db', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (518, '1017f93fc41c45a6a3b8078ba3e778e5', '81bf2df010f948c4ace9bf50c06139db', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (519, '144f6c169af64ed19e27347900febac1', '62346c8600b74dfa859501a08612997e', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (520, 'febefdc301844bff9f40127c2fd8846b', '235102a6cbdb4360a81c130efcdf3164', 100.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (521, '832308d2000d4d1fbf31c8d791497cf3', '235102a6cbdb4360a81c130efcdf3164', 100.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (522, '1017f93fc41c45a6a3b8078ba3e778e5', '235102a6cbdb4360a81c130efcdf3164', 100.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (523, '144f6c169af64ed19e27347900febac1', '235102a6cbdb4360a81c130efcdf3164', 100.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (524, 'febefdc301844bff9f40127c2fd8846b', '6fbd6fdf20e9481481f755d5868df2db', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (525, '832308d2000d4d1fbf31c8d791497cf3', '6fbd6fdf20e9481481f755d5868df2db', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (526, '1017f93fc41c45a6a3b8078ba3e778e5', '6fbd6fdf20e9481481f755d5868df2db', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (527, '144f6c169af64ed19e27347900febac1', '6fbd6fdf20e9481481f755d5868df2db', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (528, 'febefdc301844bff9f40127c2fd8846b', '16984d51e4eb4f7480f38046163e5441', 86.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (529, '832308d2000d4d1fbf31c8d791497cf3', '16984d51e4eb4f7480f38046163e5441', 86.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (530, '1017f93fc41c45a6a3b8078ba3e778e5', '16984d51e4eb4f7480f38046163e5441', 86.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (531, '144f6c169af64ed19e27347900febac1', '16984d51e4eb4f7480f38046163e5441', 86.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (532, 'febefdc301844bff9f40127c2fd8846b', 'f40d936790dd4ee4925216e8f8e66c32', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (533, '832308d2000d4d1fbf31c8d791497cf3', 'f40d936790dd4ee4925216e8f8e66c32', 36.00, 997, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (534, '1017f93fc41c45a6a3b8078ba3e778e5', 'f40d936790dd4ee4925216e8f8e66c32', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (535, '144f6c169af64ed19e27347900febac1', 'f40d936790dd4ee4925216e8f8e66c32', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (536, 'febefdc301844bff9f40127c2fd8846b', 'a15c8e814493432398ded321cce8e16b', 21.60, 0, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (537, '832308d2000d4d1fbf31c8d791497cf3', 'a15c8e814493432398ded321cce8e16b', 21.60, 0, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (538, '1017f93fc41c45a6a3b8078ba3e778e5', 'a15c8e814493432398ded321cce8e16b', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (539, '144f6c169af64ed19e27347900febac1', 'a15c8e814493432398ded321cce8e16b', 21.60, 998, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (540, 'febefdc301844bff9f40127c2fd8846b', 'c273a02ab88c411bbc2d25f02ac79a6c', 96.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (541, '832308d2000d4d1fbf31c8d791497cf3', 'c273a02ab88c411bbc2d25f02ac79a6c', 96.00, 999, 1, '2023-03-31 09:59:27', '2023-05-08 17:09:08');
INSERT INTO `goods_store` VALUES (542, '1017f93fc41c45a6a3b8078ba3e778e5', 'c273a02ab88c411bbc2d25f02ac79a6c', 96.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (543, '144f6c169af64ed19e27347900febac1', 'c273a02ab88c411bbc2d25f02ac79a6c', 96.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (544, 'febefdc301844bff9f40127c2fd8846b', 'ad30d3aa49954d70811fd9fb746a63a3', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (545, '832308d2000d4d1fbf31c8d791497cf3', 'ad30d3aa49954d70811fd9fb746a63a3', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (546, '1017f93fc41c45a6a3b8078ba3e778e5', 'ad30d3aa49954d70811fd9fb746a63a3', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (547, '144f6c169af64ed19e27347900febac1', 'ad30d3aa49954d70811fd9fb746a63a3', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (548, 'febefdc301844bff9f40127c2fd8846b', 'a5f28fb59d8d4acf899aad74b98be51d', 76.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (549, '832308d2000d4d1fbf31c8d791497cf3', 'a5f28fb59d8d4acf899aad74b98be51d', 76.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (550, '1017f93fc41c45a6a3b8078ba3e778e5', 'a5f28fb59d8d4acf899aad74b98be51d', 76.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (551, '144f6c169af64ed19e27347900febac1', 'a5f28fb59d8d4acf899aad74b98be51d', 76.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (552, 'febefdc301844bff9f40127c2fd8846b', 'ef7292010dea4c2fbc1ddecb7c7fda09', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (553, '832308d2000d4d1fbf31c8d791497cf3', 'ef7292010dea4c2fbc1ddecb7c7fda09', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (554, '1017f93fc41c45a6a3b8078ba3e778e5', 'ef7292010dea4c2fbc1ddecb7c7fda09', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (555, '144f6c169af64ed19e27347900febac1', 'ef7292010dea4c2fbc1ddecb7c7fda09', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (556, 'febefdc301844bff9f40127c2fd8846b', 'f5de4558b2e2477489856dfeb29ec445', 144.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (557, '832308d2000d4d1fbf31c8d791497cf3', 'f5de4558b2e2477489856dfeb29ec445', 144.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (558, '1017f93fc41c45a6a3b8078ba3e778e5', 'f5de4558b2e2477489856dfeb29ec445', 144.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (559, '144f6c169af64ed19e27347900febac1', 'f5de4558b2e2477489856dfeb29ec445', 144.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (560, 'febefdc301844bff9f40127c2fd8846b', '932ef51a35b842b9b443c9123c6c344a', 144.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (561, '832308d2000d4d1fbf31c8d791497cf3', '932ef51a35b842b9b443c9123c6c344a', 144.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (562, '1017f93fc41c45a6a3b8078ba3e778e5', '932ef51a35b842b9b443c9123c6c344a', 144.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (563, '144f6c169af64ed19e27347900febac1', '932ef51a35b842b9b443c9123c6c344a', 144.00, 997, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (564, 'febefdc301844bff9f40127c2fd8846b', '8ed404859d2d4fc89da2c20221ce292d', 86.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (565, '832308d2000d4d1fbf31c8d791497cf3', '8ed404859d2d4fc89da2c20221ce292d', 86.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (566, '1017f93fc41c45a6a3b8078ba3e778e5', '8ed404859d2d4fc89da2c20221ce292d', 86.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (567, '144f6c169af64ed19e27347900febac1', '8ed404859d2d4fc89da2c20221ce292d', 86.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (568, 'febefdc301844bff9f40127c2fd8846b', '21fdbab24ce44787a80bebd15a8ad1cf', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (569, '832308d2000d4d1fbf31c8d791497cf3', '21fdbab24ce44787a80bebd15a8ad1cf', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (570, '1017f93fc41c45a6a3b8078ba3e778e5', '21fdbab24ce44787a80bebd15a8ad1cf', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (571, '144f6c169af64ed19e27347900febac1', '21fdbab24ce44787a80bebd15a8ad1cf', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (572, 'febefdc301844bff9f40127c2fd8846b', '08e9e5efa26c47a5a8f51c1f9208a978', 124.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (573, '832308d2000d4d1fbf31c8d791497cf3', '08e9e5efa26c47a5a8f51c1f9208a978', 124.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (574, '1017f93fc41c45a6a3b8078ba3e778e5', '08e9e5efa26c47a5a8f51c1f9208a978', 124.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (575, '144f6c169af64ed19e27347900febac1', '08e9e5efa26c47a5a8f51c1f9208a978', 124.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (576, 'febefdc301844bff9f40127c2fd8846b', '452af6779cc540b4bfdc679a45a05ba5', 105.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (577, '832308d2000d4d1fbf31c8d791497cf3', '452af6779cc540b4bfdc679a45a05ba5', 105.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (578, '1017f93fc41c45a6a3b8078ba3e778e5', '452af6779cc540b4bfdc679a45a05ba5', 105.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (579, '144f6c169af64ed19e27347900febac1', '452af6779cc540b4bfdc679a45a05ba5', 105.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (580, 'febefdc301844bff9f40127c2fd8846b', '94ecfd156cfb4b969c47c87b5ab6cea7', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (581, '832308d2000d4d1fbf31c8d791497cf3', '94ecfd156cfb4b969c47c87b5ab6cea7', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (582, '1017f93fc41c45a6a3b8078ba3e778e5', '94ecfd156cfb4b969c47c87b5ab6cea7', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (583, '144f6c169af64ed19e27347900febac1', '94ecfd156cfb4b969c47c87b5ab6cea7', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (584, 'febefdc301844bff9f40127c2fd8846b', 'f99e482d695f493291b0238311893ba4', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (585, '832308d2000d4d1fbf31c8d791497cf3', 'f99e482d695f493291b0238311893ba4', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (586, '1017f93fc41c45a6a3b8078ba3e778e5', 'f99e482d695f493291b0238311893ba4', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (587, '144f6c169af64ed19e27347900febac1', 'f99e482d695f493291b0238311893ba4', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (588, 'febefdc301844bff9f40127c2fd8846b', 'e020ee8bf7d94a5f8f4739a85e2c116d', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (589, '832308d2000d4d1fbf31c8d791497cf3', 'e020ee8bf7d94a5f8f4739a85e2c116d', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (590, '1017f93fc41c45a6a3b8078ba3e778e5', 'e020ee8bf7d94a5f8f4739a85e2c116d', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (591, '144f6c169af64ed19e27347900febac1', 'e020ee8bf7d94a5f8f4739a85e2c116d', 21.60, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (592, 'febefdc301844bff9f40127c2fd8846b', 'c36861dc5ce443f3addde97a827baf5e', 26.40, 988, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (593, '832308d2000d4d1fbf31c8d791497cf3', 'c36861dc5ce443f3addde97a827baf5e', 26.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (594, '1017f93fc41c45a6a3b8078ba3e778e5', 'c36861dc5ce443f3addde97a827baf5e', 26.40, 997, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (595, '144f6c169af64ed19e27347900febac1', 'c36861dc5ce443f3addde97a827baf5e', 26.40, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (596, 'febefdc301844bff9f40127c2fd8846b', '7f0743fffc10486e995f27d869360772', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (597, '832308d2000d4d1fbf31c8d791497cf3', '7f0743fffc10486e995f27d869360772', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (598, '1017f93fc41c45a6a3b8078ba3e778e5', '7f0743fffc10486e995f27d869360772', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (599, '144f6c169af64ed19e27347900febac1', '7f0743fffc10486e995f27d869360772', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (600, 'febefdc301844bff9f40127c2fd8846b', 'b7b5b0f4c42341fe9a58c9ebbea02283', 124.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (601, '832308d2000d4d1fbf31c8d791497cf3', 'b7b5b0f4c42341fe9a58c9ebbea02283', 124.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (602, '1017f93fc41c45a6a3b8078ba3e778e5', 'b7b5b0f4c42341fe9a58c9ebbea02283', 124.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (603, '144f6c169af64ed19e27347900febac1', 'b7b5b0f4c42341fe9a58c9ebbea02283', 124.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (604, 'febefdc301844bff9f40127c2fd8846b', '7ff47f4b0bdd46a88b3f74880c88e57a', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (605, '832308d2000d4d1fbf31c8d791497cf3', '7ff47f4b0bdd46a88b3f74880c88e57a', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (606, '1017f93fc41c45a6a3b8078ba3e778e5', '7ff47f4b0bdd46a88b3f74880c88e57a', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (607, '144f6c169af64ed19e27347900febac1', '7ff47f4b0bdd46a88b3f74880c88e57a', 36.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (608, 'febefdc301844bff9f40127c2fd8846b', '77a058537afe49f7b555cc0f38817d3d', 96.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (609, '832308d2000d4d1fbf31c8d791497cf3', '77a058537afe49f7b555cc0f38817d3d', 96.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (610, '1017f93fc41c45a6a3b8078ba3e778e5', '77a058537afe49f7b555cc0f38817d3d', 96.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (611, '144f6c169af64ed19e27347900febac1', '77a058537afe49f7b555cc0f38817d3d', 96.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (612, 'febefdc301844bff9f40127c2fd8846b', '72b4dbed72eb408f91e621768792a84f', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (613, '832308d2000d4d1fbf31c8d791497cf3', '72b4dbed72eb408f91e621768792a84f', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (614, '1017f93fc41c45a6a3b8078ba3e778e5', '72b4dbed72eb408f91e621768792a84f', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (615, '144f6c169af64ed19e27347900febac1', '72b4dbed72eb408f91e621768792a84f', 24.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (616, 'febefdc301844bff9f40127c2fd8846b', 'f59bb7ca872643848eb36a0e6542fe14', 576.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (617, '832308d2000d4d1fbf31c8d791497cf3', 'ecd53aa96b3343c9bc1c92bd0e4f4b9b', 336.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (618, '1017f93fc41c45a6a3b8078ba3e778e5', 'c4d1d09b7e5843288e1a8ec465acfc30', 576.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (619, '144f6c169af64ed19e27347900febac1', '9db13050033f45a697e0e3ee0fecd86c', 576.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (620, 'febefdc301844bff9f40127c2fd8846b', '4e6313c62aeb467cba6d4de70db9a3d6', 432.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (621, '832308d2000d4d1fbf31c8d791497cf3', 'f0a65a0c8dc04b5b850ed7835ce11088', 1008.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (622, '1017f93fc41c45a6a3b8078ba3e778e5', '3da4fed1ddf94183a60558d0bcced790', 1008.00, 997, 1, '2023-03-31 09:59:27', '2023-05-06 11:31:16');
INSERT INTO `goods_store` VALUES (623, '144f6c169af64ed19e27347900febac1', '40f42ffefb914adcaadf4d7596df1665', 864.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (624, 'febefdc301844bff9f40127c2fd8846b', '4c346154c2c94e7ba7186448f51553a1', 336.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (625, '832308d2000d4d1fbf31c8d791497cf3', '12cb06f4fa1948078636a50717f743fe', 336.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (626, '1017f93fc41c45a6a3b8078ba3e778e5', '4fbefb6948ae4ef58cc827762575656e', 1344.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (627, '144f6c169af64ed19e27347900febac1', 'f53536c6bb2c42f6b931569b4df916b9', 1800.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (628, 'febefdc301844bff9f40127c2fd8846b', '8e4ca6d93f6e4382aa3eff8838ca9543', 1008.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (629, '832308d2000d4d1fbf31c8d791497cf3', 'dfad8b082aeb4edbaf1400d83c8b0af1', 1008.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (630, '1017f93fc41c45a6a3b8078ba3e778e5', '7ce97d9f1f974916856c33b0788fc0a5', 864.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (631, '144f6c169af64ed19e27347900febac1', '56385112f1164bdd84b0f8b048ae0053', 336.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (632, 'febefdc301844bff9f40127c2fd8846b', 'e32ee11e1fb94d1092dae98f217056ee', 576.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (633, '832308d2000d4d1fbf31c8d791497cf3', '705bbf9e904f46d782e77d4f0425df23', 864.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (634, '1017f93fc41c45a6a3b8078ba3e778e5', '834b81d55f404723ba2ea0629d92d0c6', 4.80, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (635, '144f6c169af64ed19e27347900febac1', 'aebbb174db5240c29a26534c96994e63', 192.00, 999, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (636, 'febefdc301844bff9f40127c2fd8846b', 'aebbb174db5240c29a26534c96994e63', 10.00, 20, 1, '2023-03-31 09:59:27', NULL);
INSERT INTO `goods_store` VALUES (637, '832308d2000d4d1fbf31c8d791497cf3', 'db5f0db8275e480db4b9e9445f7c8a02', 500.00, 199, 1, '2023-03-31 09:59:27', NULL);

-- ----------------------------
-- Table structure for order_detail
-- ----------------------------
DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `store_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `goods_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `goods_amount` int(11) NULL DEFAULT NULL,
  `goods_price` double NULL DEFAULT NULL,
  `total_price` double NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `orders_orderitem_idx`(`order_no`) USING BTREE,
  INDEX `goods_orderitem_idx`(`goods_no`) USING BTREE,
  INDEX `store_order_detail`(`store_no`) USING BTREE,
  CONSTRAINT `goods_order_detail` FOREIGN KEY (`goods_no`) REFERENCES `goods` (`goods_no`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `store_order_detail` FOREIGN KEY (`store_no`) REFERENCES `store` (`store_no`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 64 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_detail
-- ----------------------------
INSERT INTO `order_detail` VALUES (16, '2b5b13a405b244d38d2e04298c0ba16b', '832308d2000d4d1fbf31c8d791497cf3', '88ec044990f0463b9e59536725044c0b', 2, 206.4, 412.8, '2023-04-06 09:25:25', NULL);
INSERT INTO `order_detail` VALUES (17, '2b5b13a405b244d38d2e04298c0ba16b', '832308d2000d4d1fbf31c8d791497cf3', 'c54ff1fa8f8443e789226f2e287a36ce', 2, 36, 72, '2023-04-06 09:25:25', NULL);
INSERT INTO `order_detail` VALUES (18, '2b5b13a405b244d38d2e04298c0ba16b', 'febefdc301844bff9f40127c2fd8846b', 'fa61586d51134267832c37ac94f11be8', 5, 33.6, 168, '2023-04-06 09:25:25', NULL);
INSERT INTO `order_detail` VALUES (19, '20230406112159167713109', '832308d2000d4d1fbf31c8d791497cf3', '9bc484e25e18466a86489e068a246460', 2, 93.6, 187.2, '2023-04-06 11:21:59', NULL);
INSERT INTO `order_detail` VALUES (20, '20230406112248993277853', '832308d2000d4d1fbf31c8d791497cf3', 'fa61586d51134267832c37ac94f11be8', 2, 33.6, 67.2, '2023-04-06 11:22:49', NULL);
INSERT INTO `order_detail` VALUES (21, '20230406112336234476651', '832308d2000d4d1fbf31c8d791497cf3', '32ecd0a88f1c40fe82e40ce796572e34', 2, 81.6, 163.2, '2023-04-06 11:23:36', NULL);
INSERT INTO `order_detail` VALUES (22, '20230407100112344971595', '832308d2000d4d1fbf31c8d791497cf3', '58686e68a3db47f3a723a1acb08257eb', 2, 220.8, 441.6, '2023-04-07 10:01:16', NULL);
INSERT INTO `order_detail` VALUES (23, '20230407100112344971595', '832308d2000d4d1fbf31c8d791497cf3', 'e65f694df46a4937b94f25115ec02f83', 4, 216, 864, '2023-04-07 10:01:16', NULL);
INSERT INTO `order_detail` VALUES (24, '20230407100343821306817', '832308d2000d4d1fbf31c8d791497cf3', '88ec044990f0463b9e59536725044c0b', 1, 206.4, 206.4, '2023-04-07 10:04:19', NULL);
INSERT INTO `order_detail` VALUES (25, '20230407102057822539718', '832308d2000d4d1fbf31c8d791497cf3', '492970f5f780430e9351ded7bf1a80c7', 5, 157.92, 789.6, '2023-04-07 10:20:59', NULL);
INSERT INTO `order_detail` VALUES (26, '20230407103551300611961', '832308d2000d4d1fbf31c8d791497cf3', '492970f5f780430e9351ded7bf1a80c7', 5, 157.92, 789.6, '2023-04-07 10:35:51', NULL);
INSERT INTO `order_detail` VALUES (27, '20230407103817462735212', '832308d2000d4d1fbf31c8d791497cf3', 'ce0dd5e0d77a47c48f37301fcabf06c5', 2, 146.4, 292.8, '2023-04-07 10:38:17', NULL);
INSERT INTO `order_detail` VALUES (28, '20230407103923607457950', '832308d2000d4d1fbf31c8d791497cf3', '1e0752b8c9ad42479fcebc9bbfd1f872', 2, 383.52, 767.04, '2023-04-07 10:39:23', NULL);
INSERT INTO `order_detail` VALUES (29, '20230407103958537861553', '832308d2000d4d1fbf31c8d791497cf3', '1e0752b8c9ad42479fcebc9bbfd1f872', 2, 383.52, 767.04, '2023-04-07 10:39:58', NULL);
INSERT INTO `order_detail` VALUES (30, '20230407104350922324493', '832308d2000d4d1fbf31c8d791497cf3', '1e0752b8c9ad42479fcebc9bbfd1f872', 1, 383.52, 383.52, '2023-04-07 10:43:51', NULL);
INSERT INTO `order_detail` VALUES (31, '20230407104432944887456', '832308d2000d4d1fbf31c8d791497cf3', '1e0752b8c9ad42479fcebc9bbfd1f872', 1, 383.52, 383.52, '2023-04-07 10:44:33', NULL);
INSERT INTO `order_detail` VALUES (32, '20230407105410153542060', '832308d2000d4d1fbf31c8d791497cf3', '1e0752b8c9ad42479fcebc9bbfd1f872', 2, 383.52, 767.04, '2023-04-07 10:54:10', NULL);
INSERT INTO `order_detail` VALUES (33, '20230407110158549655117', '832308d2000d4d1fbf31c8d791497cf3', '1e0752b8c9ad42479fcebc9bbfd1f872', 1, 383.52, 383.52, '2023-04-07 11:01:58', NULL);
INSERT INTO `order_detail` VALUES (34, '20230407123415488237048', '832308d2000d4d1fbf31c8d791497cf3', 'd0bb67447493418c8ada18bfb11e1649', 2, 357.6, 715.2, '2023-04-07 12:34:15', NULL);
INSERT INTO `order_detail` VALUES (35, '20230407123415488237048', '832308d2000d4d1fbf31c8d791497cf3', '5d10a42b834a4dc48ca23676b2902887', 2, 14.4, 28.8, '2023-04-07 12:34:15', NULL);
INSERT INTO `order_detail` VALUES (36, '20230407151129144659180', '832308d2000d4d1fbf31c8d791497cf3', '3f17ce33f4b44ce28e9ba171d0f4f526', 2, 86.4, 172.8, '2023-04-07 15:11:29', NULL);
INSERT INTO `order_detail` VALUES (37, '20230412091736870922231', '832308d2000d4d1fbf31c8d791497cf3', '58686e68a3db47f3a723a1acb08257eb', 3, 220.8, 662.4, '2023-04-12 09:17:37', NULL);
INSERT INTO `order_detail` VALUES (38, '20230412091920794141133', '832308d2000d4d1fbf31c8d791497cf3', '58686e68a3db47f3a723a1acb08257eb', 5, 220.8, 1104, '2023-04-12 09:19:20', NULL);
INSERT INTO `order_detail` VALUES (39, '20230412091920794141133', '832308d2000d4d1fbf31c8d791497cf3', '940b6061d34a45f69d3ae7d1e5c8d925', 2, 316.8, 633.6, '2023-04-12 09:19:20', NULL);
INSERT INTO `order_detail` VALUES (40, '20230412094812483511688', '832308d2000d4d1fbf31c8d791497cf3', '58686e68a3db47f3a723a1acb08257eb', 2, 220.8, 441.6, '2023-04-12 09:48:13', NULL);
INSERT INTO `order_detail` VALUES (41, '20230412100428721789128', '1017f93fc41c45a6a3b8078ba3e778e5', '58686e68a3db47f3a723a1acb08257eb', 2, 220.8, 441.6, '2023-04-12 10:04:29', NULL);
INSERT INTO `order_detail` VALUES (42, '20230412101823346467033', '832308d2000d4d1fbf31c8d791497cf3', '58686e68a3db47f3a723a1acb08257eb', 4, 220.8, 883.2, '2023-04-12 10:18:23', NULL);
INSERT INTO `order_detail` VALUES (43, '20230412101823346467033', '832308d2000d4d1fbf31c8d791497cf3', 'd0bb67447493418c8ada18bfb11e1649', 1, 357.6, 357.6, '2023-04-12 10:18:23', NULL);
INSERT INTO `order_detail` VALUES (44, '20230417172731870833188', '832308d2000d4d1fbf31c8d791497cf3', '16caad5d3da943fdba228944f9249019', 2, 18.24, 36.48, '2023-04-17 17:27:33', NULL);
INSERT INTO `order_detail` VALUES (45, '20230505100324549822445', '832308d2000d4d1fbf31c8d791497cf3', '58686e68a3db47f3a723a1acb08257eb', 2, 220.8, 441.6, '2023-05-05 10:03:25', NULL);
INSERT INTO `order_detail` VALUES (47, '20230505110945507504666', '832308d2000d4d1fbf31c8d791497cf3', '58686e68a3db47f3a723a1acb08257eb', 3, 220.8, 662.4, '2023-05-05 11:09:46', NULL);
INSERT INTO `order_detail` VALUES (48, '20230505112455889768000', '832308d2000d4d1fbf31c8d791497cf3', '58686e68a3db47f3a723a1acb08257eb', 1, 220.8, 220.8, '2023-05-05 11:24:56', NULL);
INSERT INTO `order_detail` VALUES (49, '20230505113553133877935', '832308d2000d4d1fbf31c8d791497cf3', '16caad5d3da943fdba228944f9249019', 2, 18.24, 36.48, '2023-05-05 11:35:53', NULL);
INSERT INTO `order_detail` VALUES (50, '20230505113808691863645', '832308d2000d4d1fbf31c8d791497cf3', '16caad5d3da943fdba228944f9249019', 2, 18.24, 36.48, '2023-05-05 11:38:09', NULL);
INSERT INTO `order_detail` VALUES (51, '20230506111754768307783', '1017f93fc41c45a6a3b8078ba3e778e5', '3da4fed1ddf94183a60558d0bcced790', 2, 1008, 2016, '2023-05-06 11:17:55', NULL);
INSERT INTO `order_detail` VALUES (52, '20230506113115751172274', '1017f93fc41c45a6a3b8078ba3e778e5', '3da4fed1ddf94183a60558d0bcced790', 2, 1008, 2016, '2023-05-06 11:31:16', NULL);
INSERT INTO `order_detail` VALUES (54, '2023050816590756799386', '832308d2000d4d1fbf31c8d791497cf3', 'c273a02ab88c411bbc2d25f02ac79a6c', 5, 96, 480, '2023-05-08 16:59:07', NULL);
INSERT INTO `order_detail` VALUES (55, '20230515085557309353102', '832308d2000d4d1fbf31c8d791497cf3', 'f6197fe4652a4ed09786dbd94da2a0af', 5, 110.4, 552, '2023-05-15 08:55:58', NULL);
INSERT INTO `order_detail` VALUES (56, '20230515085557309353102', '832308d2000d4d1fbf31c8d791497cf3', 'dbc9c69427b44e16bce77052b610c20b', 3, 110.4, 331.2, '2023-05-15 08:55:59', NULL);
INSERT INTO `order_detail` VALUES (57, '20230515085557309353102', '144f6c169af64ed19e27347900febac1', 'e849f2c9d4174c6a8bc5b75424c16bcd', 2, 43.2, 86.4, '2023-05-15 08:55:59', NULL);
INSERT INTO `order_detail` VALUES (58, '20230515141557609612615', '1017f93fc41c45a6a3b8078ba3e778e5', '012da18c4d34427e8deadcaf12b285f4', 3, 57.12, 171.36, '2023-05-15 14:15:58', NULL);
INSERT INTO `order_detail` VALUES (59, '20230515141557609612615', 'febefdc301844bff9f40127c2fd8846b', '9f6e9d7263bd4e4d88ea90b0be58179e', 2, 249.6, 499.2, '2023-05-15 14:15:59', NULL);
INSERT INTO `order_detail` VALUES (60, '20230516152731134692355', '832308d2000d4d1fbf31c8d791497cf3', '3e221bcfbccc456383da96f9e33efa0c', 4, 249.6, 998.4, '2023-05-16 15:27:32', NULL);
INSERT INTO `order_detail` VALUES (61, '20230517084659212572298', '832308d2000d4d1fbf31c8d791497cf3', '9f6e9d7263bd4e4d88ea90b0be58179e', 4, 249.6, 998.4, '2023-05-17 08:47:00', NULL);
INSERT INTO `order_detail` VALUES (62, '20230517131422759758361', '832308d2000d4d1fbf31c8d791497cf3', '5e4251cbcf77491994f812f650b99388', 4, 21.6, 86.4, '2023-05-17 13:14:23', NULL);
INSERT INTO `order_detail` VALUES (63, '20230518165648695318981', '832308d2000d4d1fbf31c8d791497cf3', '492970f5f780430e9351ded7bf1a80c7', 2, 157.92, 315.84, '2023-05-18 16:56:50', NULL);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `user_id` int(50) NOT NULL,
  `total_price` double NULL DEFAULT NULL,
  `payment_type` int(11) NULL DEFAULT NULL,
  `payment_subtype` int(11) NULL DEFAULT NULL,
  `delivery_type` int(11) NULL DEFAULT NULL,
  `order_state` int(11) NULL DEFAULT NULL COMMENT '1：未付款  1：已付款  3：已发货   4：已签收',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_order_idx`(`user_id`) USING BTREE,
  INDEX `order_no`(`order_no`) USING BTREE,
  CONSTRAINT `user_order` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (4, '2b5b13a405b244d38d2e04298c0ba16b', 1, 652.8, 1, 1, 2, -9, '2023-04-06 09:25:25', '2023-04-06 16:55:51');
INSERT INTO `orders` VALUES (5, '20230406112159167713109', 1, 187.2, 1, 1, 2, -1, '2023-04-05 11:21:59', NULL);
INSERT INTO `orders` VALUES (6, '20230406112248993277853', 1, 67.2, 1, 1, 2, -9, '2023-04-04 11:22:49', '2023-04-06 16:51:48');
INSERT INTO `orders` VALUES (7, '20230406112336234476651', 1, 163.2, 1, 1, 2, -1, '2023-04-06 11:23:36', NULL);
INSERT INTO `orders` VALUES (8, '20230407100112344971595', 1, 1305.6, 1, 1, 2, -1, '2023-04-07 10:01:12', NULL);
INSERT INTO `orders` VALUES (9, '20230407100343821306817', 1, 206.4, 1, 1, 2, 2, '2023-04-07 10:03:43', '2023-04-07 15:10:24');
INSERT INTO `orders` VALUES (10, '20230407102057822539718', 1, 789.6, 1, 1, 2, 2, '2023-04-07 10:20:57', '2023-04-07 14:59:39');
INSERT INTO `orders` VALUES (11, '20230407103551300611961', 1, 789.6, 1, 1, 2, 2, '2023-04-07 10:35:51', '2023-04-07 14:58:18');
INSERT INTO `orders` VALUES (12, '20230407103817462735212', 1, 292.8, 1, 1, 2, 2, '2023-04-07 10:38:17', '2023-04-07 14:56:37');
INSERT INTO `orders` VALUES (13, '20230407103923607457950', 1, 767.04, 1, 1, 2, -9, '2023-04-07 10:39:23', '2023-04-07 10:57:14');
INSERT INTO `orders` VALUES (14, '20230407103958537861553', 1, 767.04, 1, 1, 2, -9, '2023-04-07 10:39:58', '2023-04-07 10:51:58');
INSERT INTO `orders` VALUES (15, '20230407104350922324493', 1, 383.52, 1, 1, 2, -9, '2023-04-07 10:43:50', '2023-04-07 10:53:33');
INSERT INTO `orders` VALUES (16, '20230407104432944887456', 1, 383.52, 1, 1, 2, -9, '2023-04-07 10:44:32', '2023-04-07 10:51:28');
INSERT INTO `orders` VALUES (17, '20230407105410153542060', 1, 767.04, 1, 1, 2, -9, '2023-04-07 10:54:10', '2023-04-07 10:55:02');
INSERT INTO `orders` VALUES (18, '20230407110158549655117', 1, 383.52, 1, 1, 2, -9, '2023-04-07 11:01:58', '2023-04-07 11:02:22');
INSERT INTO `orders` VALUES (19, '20230407123415488237048', 1, 744, 1, 1, 2, 3, '2023-04-07 12:34:15', NULL);
INSERT INTO `orders` VALUES (20, '20230407151129144659180', 1, 172.8, 1, 1, 2, 2, '2023-04-07 15:11:29', '2023-04-07 15:11:36');
INSERT INTO `orders` VALUES (21, '20230412091736870922231', 1, 662.4, 1, 1, 2, -9, '2023-04-12 09:17:36', '2023-04-12 09:18:30');
INSERT INTO `orders` VALUES (22, '20230412091920794141133', 1, 1737.6, 1, 1, 2, -9, '2023-04-12 09:19:20', '2023-04-12 09:28:10');
INSERT INTO `orders` VALUES (23, '20230412094812483511688', 1, 441.6, 1, 1, 2, 9, '2023-04-12 09:48:13', '2023-04-12 09:49:06');
INSERT INTO `orders` VALUES (24, '20230412100428721789128', 1, 441.6, 1, 1, 2, 9, '2023-04-12 10:04:28', '2023-04-12 10:14:03');
INSERT INTO `orders` VALUES (25, '20230412101823346467033', 1, 1240.8, 1, 1, 2, 9, '2023-04-12 10:18:23', '2023-04-12 10:25:28');
INSERT INTO `orders` VALUES (26, '20230417172731870833188', 1, 36.48, 1, 1, 2, 2, '2023-05-05 10:02:32', '2023-05-06 11:17:13');
INSERT INTO `orders` VALUES (27, '20230505100324549822445', 1, 441.6, 1, 1, 2, -9, '2023-05-05 10:03:25', '2023-05-05 10:53:52');
INSERT INTO `orders` VALUES (29, '20230505110945507504666', 1, 662.4, 1, 1, 2, -9, '2023-05-05 11:09:46', '2023-05-05 11:35:40');
INSERT INTO `orders` VALUES (30, '20230505112455889768000', 1, 220.8, 1, 1, 2, -9, '2023-05-05 11:24:56', '2023-05-05 11:25:07');
INSERT INTO `orders` VALUES (31, '20230505113553133877935', 1, 36.48, 1, 1, 2, -9, '2023-05-05 11:35:53', '2023-05-05 11:35:55');
INSERT INTO `orders` VALUES (32, '20230505113808691863645', 1, 36.48, 1, 1, 2, -9, '2023-05-05 11:38:09', '2023-05-05 11:38:19');
INSERT INTO `orders` VALUES (33, '20230506111754768307783', 1, 2016, 1, 1, 2, -9, '2023-05-06 11:17:55', '2023-05-06 11:28:23');
INSERT INTO `orders` VALUES (34, '20230506113115751172274', 1, 2016, 1, 1, 2, 2, '2023-05-06 11:31:16', '2023-05-06 11:31:23');
INSERT INTO `orders` VALUES (36, '2023050816590756799386', 1, 480, 1, 1, 2, -9, '2023-05-08 16:59:07', '2023-05-08 17:09:08');
INSERT INTO `orders` VALUES (37, '20230515085557309353102', 1, 969.6, 1, 1, 2, 2, '2023-05-15 08:55:58', '2023-05-15 08:56:27');
INSERT INTO `orders` VALUES (38, '20230515141557609612615', 1, 670.56, 1, 1, 2, 2, '2023-05-15 14:15:58', '2023-05-15 14:17:26');
INSERT INTO `orders` VALUES (39, '20230516152731134692355', 1, 998.4, 1, 1, 2, 2, '2023-05-16 15:27:31', '2023-05-16 15:28:03');
INSERT INTO `orders` VALUES (40, '20230517084659212572298', 1, 998.4, 1, 1, 2, -9, '2023-05-17 08:47:00', '2023-05-17 08:53:28');
INSERT INTO `orders` VALUES (41, '20230517131422759758361', 1, 86.4, 1, 1, 2, -9, '2023-05-17 13:14:23', '2023-05-17 13:24:24');
INSERT INTO `orders` VALUES (42, '20230518165648695318981', 1, 315.84, 1, 1, 2, 2, '2023-05-18 16:56:49', '2023-05-18 16:56:57');

-- ----------------------------
-- Table structure for special
-- ----------------------------
DROP TABLE IF EXISTS `special`;
CREATE TABLE `special`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '特殊版块ID',
  `special_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '特殊版块名称',
  `special_status` int(1) NULL DEFAULT NULL COMMENT '1营业，2停业',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of special
-- ----------------------------
INSERT INTO `special` VALUES (1, '劳动节促销', 1, '2023-04-27 15:10:53', '2023-04-28 08:35:46');
INSERT INTO `special` VALUES (2, '热门推荐', 1, '2023-04-28 08:35:40', NULL);

-- ----------------------------
-- Table structure for special_goods
-- ----------------------------
DROP TABLE IF EXISTS `special_goods`;
CREATE TABLE `special_goods`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '特殊模块商品',
  `special_id` int(20) NULL DEFAULT NULL COMMENT '模块商品',
  `goods_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品编号',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `special_specialgoods_idx`(`special_id`) USING BTREE,
  INDEX `goods_specialgoods_idx`(`goods_no`) USING BTREE,
  CONSTRAINT `goods_specialgoods` FOREIGN KEY (`goods_no`) REFERENCES `goods` (`goods_no`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `special_specialgoods` FOREIGN KEY (`special_id`) REFERENCES `special` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of special_goods
-- ----------------------------
INSERT INTO `special_goods` VALUES (4, 1, '1c0a017886bd49b9b81cc28660a0767a', '2023-04-27 16:13:00', NULL);
INSERT INTO `special_goods` VALUES (5, 1, '421bbac8c94341158841c032d9894e49', '2023-04-27 16:14:32', NULL);
INSERT INTO `special_goods` VALUES (6, 2, '029db30046e04b3e9324d31cf55e35f5', '2023-04-28 08:36:25', NULL);
INSERT INTO `special_goods` VALUES (7, 2, '08e9e5efa26c47a5a8f51c1f9208a978', '2023-04-28 08:36:25', NULL);
INSERT INTO `special_goods` VALUES (8, 2, '452af6779cc540b4bfdc679a45a05ba5', '2023-04-28 08:36:25', NULL);
INSERT INTO `special_goods` VALUES (9, 2, '94ecfd156cfb4b969c47c87b5ab6cea7', '2023-04-28 08:36:25', NULL);
INSERT INTO `special_goods` VALUES (10, 2, 'f99e482d695f493291b0238311893ba4', '2023-04-28 08:36:25', NULL);
INSERT INTO `special_goods` VALUES (11, 2, 'c36861dc5ce443f3addde97a827baf5e', '2023-04-28 08:36:25', NULL);
INSERT INTO `special_goods` VALUES (12, 2, 'b7b5b0f4c42341fe9a58c9ebbea02283', '2023-04-28 08:36:25', NULL);
INSERT INTO `special_goods` VALUES (13, 1, 'c54ff1fa8f8443e789226f2e287a36ce', '2023-04-28 08:36:49', NULL);
INSERT INTO `special_goods` VALUES (14, 1, 'dcfd16649e6046819bc5b921655e8932', '2023-04-28 08:36:49', NULL);
INSERT INTO `special_goods` VALUES (15, 1, '81bf2df010f948c4ace9bf50c06139db', '2023-04-28 08:36:49', NULL);
INSERT INTO `special_goods` VALUES (16, 1, '62346c8600b74dfa859501a08612997e', '2023-04-28 08:36:49', NULL);
INSERT INTO `special_goods` VALUES (17, 1, '235102a6cbdb4360a81c130efcdf3164', '2023-04-28 08:36:49', NULL);
INSERT INTO `special_goods` VALUES (18, 1, '6fbd6fdf20e9481481f755d5868df2db', '2023-04-28 08:36:49', NULL);
INSERT INTO `special_goods` VALUES (19, 1, '16984d51e4eb4f7480f38046163e5441', '2023-04-28 08:36:49', NULL);
INSERT INTO `special_goods` VALUES (20, 1, 'f40d936790dd4ee4925216e8f8e66c32', '2023-04-28 08:36:49', NULL);
INSERT INTO `special_goods` VALUES (21, 1, 'c273a02ab88c411bbc2d25f02ac79a6c', '2023-04-28 08:36:49', NULL);
INSERT INTO `special_goods` VALUES (22, 1, 'a15c8e814493432398ded321cce8e16b', '2023-04-28 08:36:49', NULL);

-- ----------------------------
-- Table structure for store
-- ----------------------------
DROP TABLE IF EXISTS `store`;
CREATE TABLE `store`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '门店id',
  `area_id` int(10) NULL DEFAULT NULL COMMENT '区域id',
  `store_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '门店编号',
  `store_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '门店名称',
  `store_address` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '详细地址',
  `max_longitude` double(20, 8) NULL DEFAULT NULL COMMENT '最大经度',
  `max_latitude` double(20, 8) NULL DEFAULT NULL COMMENT '最大纬度',
  `min_longitude` double(20, 8) NULL DEFAULT NULL COMMENT '最小经度',
  `min_latitude` double(20, 8) NULL DEFAULT NULL COMMENT '最小纬度',
  `longitude` double(20, 8) NULL DEFAULT NULL COMMENT '经度',
  `latitude` double(20, 8) NULL DEFAULT NULL COMMENT '纬度',
  `store_introduce` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '门店介绍',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '开始营业时间',
  `close_time` datetime(0) NULL DEFAULT NULL COMMENT '停止营业时间',
  `store_status` int(1) NULL DEFAULT NULL COMMENT '1营业，2停业',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `storeno_idx`(`store_no`) USING BTREE,
  INDEX `areas_store_idx`(`area_id`) USING BTREE,
  CONSTRAINT `areas_storecity` FOREIGN KEY (`area_id`) REFERENCES `area` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of store
-- ----------------------------
INSERT INTO `store` VALUES (1, 3, '832308d2000d4d1fbf31c8d791497cf3', '自提点01', '测试', 120.11950000, 30.30540000, 120.11000000, 30.29540000, 116.39712800, 39.91652700, '测试', '2023-04-25 13:13:08', '2023-04-25 13:13:08', 1, '2023-04-25 13:13:42', NULL);
INSERT INTO `store` VALUES (2, 3, '1017f93fc41c45a6a3b8078ba3e778e5', '自提点02', '测试', 120.11950000, 30.30540000, 120.11000000, 30.29540000, 118.95927000, 42.26581000, '测试', '2023-04-23 00:00:00', '2023-04-25 13:13:08', 1, '2023-04-25 13:13:42', '2023-04-26 13:23:25');
INSERT INTO `store` VALUES (3, 4, '144f6c169af64ed19e27347900febac1', '自提点03', '测试', 120.38800000, 36.09070000, 120.37850000, 36.08370000, 121.52550000, 38.95223000, '测试', '2023-04-25 13:13:08', '2023-04-25 13:13:08', 1, '2023-04-25 13:13:42', NULL);
INSERT INTO `store` VALUES (4, 4, 'febefdc301844bff9f40127c2fd8846b', '自提点04', '测试', 113.73880000, 34.79550000, 113.70290000, 34.79190000, 121.48941000, 31.40527000, '测试', '2023-04-25 13:13:08', '2023-04-25 13:13:08', 1, '2023-04-25 13:13:42', NULL);
INSERT INTO `store` VALUES (6, 9, '0b19c9f126b048ba9d283b889ad7af0c', '大连东软教育科技集团', '大连东软教育科技集团', NULL, NULL, NULL, NULL, 121.53471900, 38.89095000, '大连东软教育科技集团', '2023-04-01 00:00:00', '2023-04-30 00:00:00', 1, '2023-04-26 13:34:08', '2023-04-26 13:34:12');

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log`  (
  `branch_id` bigint(20) NOT NULL COMMENT 'branch transaction id',
  `xid` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'global transaction id',
  `context` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'undo_log context,such as serialization',
  `rollback_info` longblob NOT NULL COMMENT 'rollback info',
  `log_status` int(11) NOT NULL COMMENT '0:normal status,1:defense status',
  `log_created` datetime(6) NOT NULL COMMENT 'create datetime',
  `log_modified` datetime(6) NOT NULL COMMENT 'modify datetime',
  UNIQUE INDEX `ux_undo_log`(`xid`, `branch_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'AT transaction mode undo table' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '18611111111', '超级管理员', '6512bd43d9caa6e02c990b0a82652dca', '1', 'mazhihao@neuddu.com', '/avatar/f3df511e79794dbea73089673381ab31.jpg', '2023-05-18 16:55:57', 1, '2023-03-24 09:08:55', '2023-05-16 15:29:04');
INSERT INTO `user` VALUES (12, '18622222222', '新用户64380901', '6512bd43d9caa6e02c990b0a82652dca', '1', 'mazhihao@neuedu.com', '/avatar/fcaab14cd5b54dc99e44f44fc64eb52e.jpg', '2023-05-16 15:30:16', 1, '2023-04-13 17:28:59', '2023-04-13 17:29:53');

-- ----------------------------
-- Table structure for wallet
-- ----------------------------
DROP TABLE IF EXISTS `wallet`;
CREATE TABLE `wallet`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '我的钱包id',
  `user_id` int(50) NOT NULL COMMENT '用户id',
  `wallet_balance` double NULL DEFAULT 0 COMMENT '我的余额',
  `wallet_password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '钱包密码',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ix_user_id`(`user_id`) USING BTREE,
  CONSTRAINT `user_wallet` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wallet
-- ----------------------------
INSERT INTO `wallet` VALUES (1, 1, 79266.62, '4297f44b13955235245b2497399d7a93', '2023-03-24 09:08:55', '2023-05-18 16:56:56');
INSERT INTO `wallet` VALUES (15, 12, 11311, '6512bd43d9caa6e02c990b0a82652dca', '2023-04-13 17:28:59', '2023-04-14 10:27:53');

-- ----------------------------
-- View structure for max_sales_goods_view
-- ----------------------------
DROP VIEW IF EXISTS `max_sales_goods_view`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `max_sales_goods_view` AS select `dt`.`goods_no` AS `goods_no`,sum(`dt`.`goods_amount`) AS `count` from (`order_detail` `dt` left join `orders` `od` on((`dt`.`order_no` = `od`.`order_no`))) where (`od`.`order_state` <> '-9') group by `dt`.`goods_no` order by sum(`dt`.`goods_amount`) desc;

-- ----------------------------
-- View structure for min_price_goods_view
-- ----------------------------
DROP VIEW IF EXISTS `min_price_goods_view`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `min_price_goods_view` AS select `goods_store`.`goods_no` AS `goods_no`,min(`goods_store`.`goods_price`) AS `goods_price`,`goods_store`.`store_no` AS `store_no` from `goods_store` where (`goods_store`.`goods_type` = 1) group by `goods_store`.`goods_no`;

-- ----------------------------
-- Table structure for goods_click_record
-- ----------------------------
DROP TABLE IF EXISTS `goods_click_record`;
CREATE TABLE `goods_click_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int NOT NULL COMMENT '用户id',
  `goods_no` varchar(32) NOT NULL COMMENT '商品编号',
  `click_count` int NOT NULL DEFAULT 1 COMMENT '累计点击次数',
  `last_click_time` datetime NOT NULL COMMENT '最近点击时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_click_user_goods` (`user_id`, `goods_no`) USING BTREE,
  INDEX `idx_click_goods` (`goods_no`) USING BTREE,
  INDEX `idx_click_time` (`last_click_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '用户商品点击汇总';

SET FOREIGN_KEY_CHECKS = 1;
