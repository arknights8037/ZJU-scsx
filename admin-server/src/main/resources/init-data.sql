-- ============================================================
-- 智慧社区管理后台 — 初始化数据
-- ============================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM role_menu;
DELETE FROM role_user;
DELETE FROM menu;
DELETE FROM role WHERE id = 1;
DELETE FROM user WHERE id = 1;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. 菜单数据（路径匹配前端路由）
INSERT INTO menu (id, menu_name, parent_id, menu_component, menu_icon, menu_path, menu_state) VALUES
(1,  '首页',     NULL, NULL, 'HomeFilled',     '/dashboard',           1),
(2,  '系统管理', NULL, NULL, 'Setting',        '',                     1),
(3,  '角色管理', 2,    NULL, 'UserFilled',     '/auth/role',           1),
(4,  '用户管理', 2,    NULL, 'Avatar',         '/auth/user',           1),
(5,  '商品管理', NULL, NULL, 'Goods',          '',                     1),
(6,  '类别管理', 5,    NULL, 'Grid',           '/goods/category',      1),
(7,  '商品管理', 5,    NULL, 'GoodsFilled',    '/goods/index',         1),
(8,  '区域门店', NULL, NULL, 'Shop',           '',                     1),
(9,  '区域管理', 8,    NULL, 'Location',       '/area/index',          1),
(10, '门店管理', 8,    NULL, 'OfficeBuilding', '/area/store',          1),
(11, '订单管理', NULL, NULL, 'List',           '/order/index',         1),
(12, '促销管理', NULL, NULL, 'Present',        '/special/index',       1),
(13, '社区管理', NULL, NULL, 'Platform',       '',                     1),
(14, '通知公告', 13,   NULL, 'Bell',           '/community/notice',    1),
(15, '车位管理', 13,   NULL, 'Van',            '/community/parking',   1),
(16, '访客记录', 13,   NULL, 'User',           '/community/visitor',   1),
(17, '报事报修', 13,   NULL, 'Warning',        '/community/complaint', 1),
(18, '缴纳记录', 13,   NULL, 'Money',          '/community/charge',    1);

-- 2. 默认角色
INSERT INTO role (id, role_name, role_desc, role_state) VALUES
(1, '超级管理员', '拥有所有权限', 1);

-- 3. 角色-菜单关联
INSERT INTO role_menu (role_id, menu_id) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),
(1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18);

-- 4. 管理员用户 (手机: 13800000000  密码: 123456)
INSERT INTO user (id, phone, user_name, user_password, sex, user_status) VALUES
(1, '13800000000', '系统管理员', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '男', 1);

-- 5. 用户-角色关联
INSERT INTO role_user (user_id, role_id) VALUES (1, 1);
