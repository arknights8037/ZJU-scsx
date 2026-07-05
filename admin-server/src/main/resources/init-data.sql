-- ============================================================
-- 智慧社区管理后台 — 幂等初始化数据
-- 兼容旧库：不 DELETE，不清空老师原始数据；只修正菜单路由并补齐缺失默认项。
-- 实际启动初始化入口为 data.sql，本文件保留为可读备份。
-- ============================================================

SET NAMES utf8mb4;

INSERT INTO menu (id, menu_name, parent_id, menu_component, menu_icon, menu_path, menu_state) VALUES
(1,  '首页',     0, NULL, 'HomeFilled',     '/admin/dashboard',           1),
(2,  '系统管理', 0, NULL, 'Setting',        '',                           1),
(3,  '角色管理', 2, NULL, 'UserFilled',     '/admin/auth/role',           1),
(4,  '用户管理', 2, NULL, 'Avatar',         '/admin/auth/user',           1),
(5,  '商品管理', 0, NULL, 'Goods',          '',                           1),
(6,  '类别管理', 5, NULL, 'Grid',           '/admin/goods/category',      1),
(7,  '商品管理', 5, NULL, 'GoodsFilled',    '/admin/goods/index',         1),
(8,  '区域门店', 0, NULL, 'Shop',           '',                           1),
(9,  '区域管理', 8, NULL, 'Location',       '/admin/area/index',          1),
(10, '门店管理', 8, NULL, 'OfficeBuilding', '/admin/area/store',          1),
(11, '订单管理', 0, NULL, 'List',           '/admin/order/index',         1),
(12, '促销管理', 0, NULL, 'Present',        '/admin/special/index',       1),
(13, '社区管理', 0, NULL, 'Platform',       '',                           1),
(14, '通知公告', 13, NULL, 'Bell',          '/admin/community/notice',    1),
(15, '车位管理', 13, NULL, 'Van',           '/admin/community/parking',   1),
(16, '访客记录', 13, NULL, 'User',          '/admin/community/visitor',   1),
(17, '报事报修', 13, NULL, 'Warning',       '/admin/community/complaint', 1),
(18, '缴纳记录', 13, NULL, 'Money',         '/admin/community/charge',    1),
(19, '订单仪表盘', 11, NULL, 'DataAnalysis','/admin/order/dashboard',     1),
(20, '订单列表', 11, NULL, 'Tickets',       '/admin/order/index',         1),
(21, '系统日志', 2, NULL, 'Document',       '/admin/system/log',          1)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  menu_icon = VALUES(menu_icon),
  menu_path = VALUES(menu_path),
  menu_state = VALUES(menu_state);

INSERT INTO role (id, role_name, role_desc, role_state)
VALUES (1, '超级管理员', '拥有所有权限', 1)
ON DUPLICATE KEY UPDATE role_desc = VALUES(role_desc), role_state = VALUES(role_state);

INSERT INTO role_menu (role_id, menu_id)
SELECT 1, seed.menu_id
FROM (
  SELECT 1 menu_id UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL
  SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL
  SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL
  SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20 UNION ALL SELECT 21
) seed
WHERE NOT EXISTS (
  SELECT 1 FROM role_menu existing
  WHERE existing.role_id = 1 AND existing.menu_id = seed.menu_id
);

INSERT INTO user (phone, user_name, user_password, sex, user_status, create_time)
SELECT '13800000000', '系统管理员', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '男', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM user WHERE phone = '13800000000');

INSERT INTO user_resident_profile
  (user_id, owner_name, building_no, unit_no, room_no, full_address, emergency_contact, emergency_phone, create_time, update_time)
SELECT u.id, '系统管理员', '1栋', '1单元', '101', '智慧社区 1栋1单元101', '物业前台', '13800000000', NOW(), NOW()
FROM user u
WHERE u.phone = '13800000000'
  AND NOT EXISTS (
    SELECT 1 FROM user_resident_profile existing
    WHERE existing.user_id = u.id
  );

INSERT INTO role_user (user_id, role_id)
SELECT u.id, 1
FROM user u
WHERE u.phone = '13800000000'
  AND NOT EXISTS (
    SELECT 1 FROM role_user existing
    WHERE existing.user_id = u.id AND existing.role_id = 1
  );
