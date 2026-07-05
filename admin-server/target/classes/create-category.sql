-- 与商城原始结构保持一致，商品表的 category_id 关联 goods_category.id。
CREATE TABLE IF NOT EXISTS goods_category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) DEFAULT '' COMMENT '类别名称',
    category_type INT DEFAULT 0 COMMENT '类别类型',
    parent_id INT DEFAULT 0 COMMENT '父级ID',
    create_time DATETIME DEFAULT NULL,
    update_time DATETIME DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品类别';
