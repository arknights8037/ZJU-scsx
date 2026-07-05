-- ============================================================
-- 统一后端增量结构脚本
-- 只补充当前代码需要但老师原始库可能缺失的表，不清空已有数据。
-- ============================================================

-- 与商城原始结构保持一致，商品表的 category_id 关联 goods_category.id。
CREATE TABLE IF NOT EXISTS goods_category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) DEFAULT '' COMMENT '类别名称',
    category_type INT DEFAULT 0 COMMENT '类别类型',
    parent_id INT DEFAULT 0 COMMENT '父级ID',
    create_time DATETIME DEFAULT NULL,
    update_time DATETIME DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品类别';

-- 商品点击汇总表。居民端推荐与统计使用。
CREATE TABLE IF NOT EXISTS goods_click_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  user_id INT NOT NULL COMMENT '用户id',
  goods_no VARCHAR(32) NOT NULL COMMENT '商品编号',
  click_count INT NOT NULL DEFAULT 1 COMMENT '累计点击次数',
  last_click_time DATETIME NOT NULL COMMENT '最近点击时间',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  update_time DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_click_user_goods (user_id, goods_no),
  KEY idx_click_goods (goods_no),
  KEY idx_click_time (last_click_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户商品点击汇总';

-- 门店-商品关系表。决定某个门店是否售卖某商品，以及该门店价格和库存。
CREATE TABLE IF NOT EXISTS goods_store (
  id INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  store_no VARCHAR(50) NOT NULL COMMENT '门店编号',
  goods_no VARCHAR(50) NOT NULL COMMENT '商品编号',
  goods_price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '门店销售价',
  goods_stock INT NOT NULL DEFAULT 0 COMMENT '门店库存',
  goods_type INT DEFAULT 1 COMMENT '门店商品类型',
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_goods_store_pair (store_no, goods_no),
  KEY idx_goods_store_goods (goods_no),
  KEY idx_goods_store_store (store_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店商品价格库存关系';

-- 居民住户扩展资料。user 表继续保存登录账号，这张表保存社区业务所需的楼栋、单元和门牌号。
CREATE TABLE IF NOT EXISTS user_resident_profile (
  id INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  user_id INT NOT NULL COMMENT '用户ID',
  owner_name VARCHAR(50) DEFAULT NULL COMMENT '业主姓名',
  building_no VARCHAR(50) DEFAULT NULL COMMENT '楼栋号',
  unit_no VARCHAR(50) DEFAULT NULL COMMENT '单元号',
  room_no VARCHAR(50) DEFAULT NULL COMMENT '门牌号',
  full_address VARCHAR(200) DEFAULT NULL COMMENT '完整住址',
  emergency_contact VARCHAR(50) DEFAULT NULL COMMENT '紧急联系人',
  emergency_phone VARCHAR(30) DEFAULT NULL COMMENT '紧急联系电话',
  create_time DATETIME DEFAULT NULL COMMENT '创建时间',
  update_time DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_resident_profile_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='居民住户扩展资料';

-- 居民虚拟钱包及交易流水；只用于演示，不接入真实支付渠道。
CREATE TABLE IF NOT EXISTS wallet (
  id INT NOT NULL AUTO_INCREMENT COMMENT '钱包ID',
  user_id INT NOT NULL COMMENT '用户ID',
  wallet_balance DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '虚拟余额',
  wallet_password VARCHAR(100) DEFAULT NULL COMMENT '兼容旧字段',
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_wallet_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='居民虚拟钱包';

CREATE TABLE IF NOT EXISTS wallet_transaction (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  transaction_no VARCHAR(40) NOT NULL COMMENT '交易流水号',
  user_id INT NOT NULL COMMENT '用户ID',
  transaction_type VARCHAR(24) NOT NULL COMMENT '交易类型',
  amount DECIMAL(12,2) NOT NULL COMMENT '带正负号的变动金额',
  balance_after DECIMAL(12,2) NOT NULL COMMENT '交易后余额',
  counterparty_user_id INT DEFAULT NULL,
  counterparty_name VARCHAR(80) DEFAULT NULL,
  remark VARCHAR(200) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_wallet_transaction_user_time (user_id, create_time),
  KEY idx_wallet_transaction_no (transaction_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='虚拟钱包交易流水';

-- 独立个人消费台账。记录支出，但不会直接增减钱包余额。
CREATE TABLE IF NOT EXISTS personal_expense (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '账单ID',
  expense_no VARCHAR(40) NOT NULL COMMENT '账单编号',
  user_id INT NOT NULL COMMENT '用户ID',
  source_type VARCHAR(20) NOT NULL COMMENT 'MANUAL手工/ORDER商城订单',
  source_no VARCHAR(50) NOT NULL COMMENT '来源业务编号',
  category VARCHAR(40) NOT NULL COMMENT '支出分类',
  expense_title VARCHAR(120) NOT NULL COMMENT '支出名称',
  amount DECIMAL(12,2) NOT NULL COMMENT '支出金额',
  expense_time DATETIME NOT NULL COMMENT '支出时间',
  remark VARCHAR(300) DEFAULT NULL COMMENT '备注',
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_personal_expense_source (user_id, source_type, source_no),
  KEY idx_personal_expense_user_time (user_id, expense_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个人消费账单';

-- 促销扩展配置表。原 special 表继续保存名称和启停状态，避免破坏旧项目结构。
CREATE TABLE IF NOT EXISTS special_rule (
  id INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  special_id INT NOT NULL COMMENT '促销活动ID',
  special_subtitle VARCHAR(100) DEFAULT NULL COMMENT '活动副标题',
  badge_text VARCHAR(20) DEFAULT NULL COMMENT '商品角标',
  promotion_type VARCHAR(20) NOT NULL DEFAULT 'DISCOUNT' COMMENT 'DISCOUNT折扣 REDUCE直减',
  discount_value DECIMAL(10,2) NOT NULL DEFAULT 9.00 COMMENT '折扣值或直减金额',
  start_time DATETIME DEFAULT NULL COMMENT '开始时间',
  end_time DATETIME DEFAULT NULL COMMENT '结束时间',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
  max_items INT NOT NULL DEFAULT 4 COMMENT '居民端最多展示商品数',
  create_time DATETIME DEFAULT NULL COMMENT '创建时间',
  update_time DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_special_rule_special (special_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='促销扩展规则';
