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
