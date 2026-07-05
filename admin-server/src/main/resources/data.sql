-- ============================================================
-- 统一后端幂等初始化数据
-- 不清空已有业务数据；重复启动不会重复插入。
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
(11, '订单管理', 0, NULL, 'List',           '',                           1),
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

-- community 目录中的 10 张商品图对应的演示商品。
-- 每条 INSERT 都带 NOT EXISTS，所以后端重复启动不会重复添加商品。

INSERT INTO goods
    (goods_no, goods_name, category_id, goods_introduce, goods_content, goods_state,
     goods_picture, goods_market_price, create_time, update_time)
SELECT 'community_food_001', '五香牛肉干礼袋', 26, '五香卤制牛肉干，独立包装，适合作为社区团购零食',
       '<p>精选牛肉制作，咸香有嚼劲。图片为商品实拍展示，具体包装以实际发货为准。</p>',
       1, '/community-products/06f3924fa6422601.jpg', 39.90, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM goods WHERE goods_no = 'community_food_001');

INSERT INTO goods
    (goods_no, goods_name, category_id, goods_introduce, goods_content, goods_state,
     goods_picture, goods_market_price, create_time, update_time)
SELECT 'community_food_002', '老上海绿豆糕礼盒', 18, '传统绿豆糕点，口感细腻，家庭分享装',
       '<p>传统糕点风味，适合早餐、下午茶和节日伴手礼。</p>',
       1, '/community-products/17d5b5cb8360fca5.jpg', 36.90, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM goods WHERE goods_no = 'community_food_002');

INSERT INTO goods
    (goods_no, goods_name, category_id, goods_introduce, goods_content, goods_state,
     goods_picture, goods_market_price, create_time, update_time)
SELECT 'community_food_003', '黄老五花生酥组合装', 18, '花生酥、米花酥等多口味组合装',
       '<p>酥脆不粘牙，多种口味一次尝遍，适合全家分享。</p>',
       1, '/community-products/223b79d7eece55e3.jpg', 39.90, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM goods WHERE goods_no = 'community_food_003');

INSERT INTO goods
    (goods_no, goods_name, category_id, goods_introduce, goods_content, goods_state,
     goods_picture, goods_market_price, create_time, update_time)
SELECT 'community_food_004', '百草味缤纷零食箱', 26, '多口味休闲零食组合，办公室和家庭分享装',
       '<p>丰富品类搭配，开箱即食，适合聚会、追剧和日常囤货。</p>',
       1, '/community-products/676311a2fb3ce996.jpg', 69.90, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM goods WHERE goods_no = 'community_food_004');

INSERT INTO goods
    (goods_no, goods_name, category_id, goods_introduce, goods_content, goods_state,
     goods_picture, goods_market_price, create_time, update_time)
SELECT 'community_food_005', '老香斋鲜肉月饼', 18, '上海风味鲜肉月饼，酥皮肉香，家庭分享装',
       '<p>酥皮层次分明，鲜肉馅咸香可口，建议加热后食用。</p>',
       1, '/community-products/752c67ca1eed100a.jpg', 49.90, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM goods WHERE goods_no = 'community_food_005');

INSERT INTO goods
    (goods_no, goods_name, category_id, goods_introduce, goods_content, goods_state,
     goods_picture, goods_market_price, create_time, update_time)
SELECT 'community_food_006', '上海特色糕点礼盒', 18, '多款传统糕点组合，适合节日送礼和家庭分享',
       '<p>一盒包含多种经典中式糕点，礼袋包装，送礼自用都合适。</p>',
       1, '/community-products/8cd0971d28ce76d6.jpg', 89.00, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM goods WHERE goods_no = 'community_food_006');

INSERT INTO goods
    (goods_no, goods_name, category_id, goods_introduce, goods_content, goods_state,
     goods_picture, goods_market_price, create_time, update_time)
SELECT 'community_food_007', '手造红豆麻薯', 18, '软糯麻薯搭配细腻红豆馅，下午茶点心',
       '<p>外层软糯，内馅香甜，独立小包装方便随身携带。</p>',
       1, '/community-products/9e2dea54e83abc28.jpg', 29.90, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM goods WHERE goods_no = 'community_food_007');

INSERT INTO goods
    (goods_no, goods_name, category_id, goods_introduce, goods_content, goods_state,
     goods_picture, goods_market_price, create_time, update_time)
SELECT 'community_food_008', '香辣冷吃鱼', 26, '香辣入味的即食鱼类熟食，下饭解馋',
       '<p>川味香辣口感，开袋即食。对鱼类过敏者请谨慎购买。</p>',
       1, '/community-products/cbb516b801e097aa.jpg', 19.80, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM goods WHERE goods_no = 'community_food_008');

INSERT INTO goods
    (goods_no, goods_name, category_id, goods_introduce, goods_content, goods_state,
     goods_picture, goods_market_price, create_time, update_time)
SELECT 'community_food_009', '每日混合坚果礼盒', 26, '多种坚果科学搭配，独立小袋方便食用',
       '<p>包含多种常见坚果，适合早餐搭配和日常能量补充。</p>',
       1, '/community-products/cc1e20e459f46507.jpg', 59.90, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM goods WHERE goods_no = 'community_food_009');

INSERT INTO goods
    (goods_no, goods_name, category_id, goods_introduce, goods_content, goods_state,
     goods_picture, goods_market_price, create_time, update_time)
SELECT 'community_food_010', '黄老五休闲零食礼盒', 26, '酥糖、米花酥等多款休闲零食组合礼盒',
       '<p>多口味组合，包装便携，适合家庭分享或作为社区邻里伴手礼。</p>',
       1, '/community-products/f4f80c6f24031d2f.jpg', 79.90, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM goods WHERE goods_no = 'community_food_010');

-- 给每件演示商品绑定第一个启用的门店，保证详情页可以选择库存并加入购物车。
INSERT INTO goods_store (store_no, goods_no, goods_price, goods_stock, goods_type, create_time, update_time)
SELECT store_no, goods_no, goods_price, 100, 1, NOW(), NOW()
FROM (
    SELECT s.store_no, demo.goods_no, demo.goods_price
    FROM store s
    CROSS JOIN (
        SELECT 'community_food_001' goods_no, 35.90 goods_price UNION ALL
        SELECT 'community_food_002', 32.90 UNION ALL
        SELECT 'community_food_003', 35.90 UNION ALL
        SELECT 'community_food_004', 62.90 UNION ALL
        SELECT 'community_food_005', 45.90 UNION ALL
        SELECT 'community_food_006', 82.00 UNION ALL
        SELECT 'community_food_007', 26.90 UNION ALL
        SELECT 'community_food_008', 17.80 UNION ALL
        SELECT 'community_food_009', 54.90 UNION ALL
        SELECT 'community_food_010', 72.90
    ) demo
    WHERE s.store_status = 1
      AND s.id = (SELECT MIN(active_store.id) FROM store active_store WHERE active_store.store_status = 1)
) seed
WHERE NOT EXISTS (
    SELECT 1 FROM goods_store existing
    WHERE existing.store_no = seed.store_no AND existing.goods_no = seed.goods_no
);
