-- 促销扩展配置表。原 special 表继续保存名称和启停状态，避免破坏旧项目结构。
CREATE TABLE IF NOT EXISTS `special_rule` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `special_id` int NOT NULL COMMENT '促销活动ID',
  `special_subtitle` varchar(100) DEFAULT NULL COMMENT '活动副标题',
  `badge_text` varchar(20) DEFAULT NULL COMMENT '商品角标',
  `promotion_type` varchar(20) NOT NULL DEFAULT 'DISCOUNT' COMMENT 'DISCOUNT折扣 REDUCE直减',
  `discount_value` decimal(10,2) NOT NULL DEFAULT 9.00 COMMENT '折扣值或直减金额',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
  `max_items` int NOT NULL DEFAULT 4 COMMENT '居民端最多展示商品数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_special_rule_special` (`special_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='促销扩展规则';
