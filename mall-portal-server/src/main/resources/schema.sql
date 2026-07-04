-- 商品点击汇总表。
-- CREATE TABLE IF NOT EXISTS 不会清空已有记录，可在每次启动时安全执行。
CREATE TABLE IF NOT EXISTS `goods_click_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int NOT NULL COMMENT '用户id',
  `goods_no` varchar(32) NOT NULL COMMENT '商品编号',
  `click_count` int NOT NULL DEFAULT 1 COMMENT '累计点击次数',
  `last_click_time` datetime NOT NULL COMMENT '最近点击时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_click_user_goods` (`user_id`, `goods_no`),
  KEY `idx_click_goods` (`goods_no`),
  KEY `idx_click_time` (`last_click_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户商品点击汇总';
