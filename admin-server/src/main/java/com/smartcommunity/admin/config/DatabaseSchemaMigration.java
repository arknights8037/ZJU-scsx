package com.smartcommunity.admin.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 运行时数据库增量迁移。
 *
 * 项目合并前后可能会复用老师提供的旧库，旧库的 order_detail 只有商品/门店编号。
 * 这里在服务启动时补充交易快照字段，避免历史订单展示被商品改名、下架或编号口径影响。
 */
@Component
@RequiredArgsConstructor
public class DatabaseSchemaMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 应用启动时执行的数据库迁移入口。
     * <p>
     * 依次检查并创建缺失的表、补充缺失的列，最后回填历史订单快照数据。
     *
     * @param args 启动参数
     */
    @Override
    public void run(ApplicationArguments args) {
        // 这些 DDL 都按"缺什么补什么"的方式执行，适合课堂项目反复启动和旧库升级。
        // 不做 DROP/DELETE，避免本地演示数据或老师给的原始数据被误清空。
        createSystemLogTableIfMissing();
        createWalletTablesIfMissing();
        createPersonalExpenseTableIfMissing();
        createGoodsStoreTableIfMissing();
        addColumnIfMissing("order_detail", "goods_name", "VARCHAR(200) DEFAULT NULL COMMENT '下单时商品名称快照'");
        addColumnIfMissing("order_detail", "goods_picture", "VARCHAR(500) DEFAULT NULL COMMENT '下单时商品图片快照'");
        addColumnIfMissing("order_detail", "store_name", "VARCHAR(200) DEFAULT NULL COMMENT '下单时门店名称快照'");
        addColumnIfMissing("orders", "refund_amount", "DECIMAL(10,2) DEFAULT NULL COMMENT '申请退款金额'");
        addColumnIfMissing("orders", "refund_reason", "VARCHAR(100) DEFAULT NULL COMMENT '退款原因'");
        addColumnIfMissing("orders", "refund_description", "VARCHAR(500) DEFAULT NULL COMMENT '退款说明'");
        addColumnIfMissing("orders", "refund_time", "DATETIME DEFAULT NULL COMMENT '申请退款时间'");
        addColumnIfMissing("orders", "refund_handled_time", "DATETIME DEFAULT NULL COMMENT '退款处理时间'");
        backfillOrderDetailSnapshots();
    }

    /**
     * 创建 system_log 系统操作日志表（如果不存在）。
     */
    private void createSystemLogTableIfMissing() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS system_log (
              id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
              user_id INT DEFAULT NULL COMMENT '操作用户ID',
              request_method VARCHAR(16) DEFAULT NULL COMMENT '请求方法',
              request_uri VARCHAR(300) DEFAULT NULL COMMENT '请求路径',
              query_string VARCHAR(500) DEFAULT NULL COMMENT '查询参数',
              operation_module VARCHAR(80) DEFAULT NULL COMMENT '操作模块',
              operation_name VARCHAR(220) DEFAULT NULL COMMENT '操作名称',
              client_ip VARCHAR(64) DEFAULT NULL COMMENT '客户端IP',
              status_code INT DEFAULT NULL COMMENT 'HTTP状态码',
              success TINYINT(1) DEFAULT NULL COMMENT '是否成功',
              error_message VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
              cost_ms BIGINT DEFAULT NULL COMMENT '耗时毫秒',
              create_time DATETIME DEFAULT NULL COMMENT '创建时间',
              PRIMARY KEY (id),
              KEY idx_system_log_time (create_time),
              KEY idx_system_log_user (user_id),
              KEY idx_system_log_success (success)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志'
            """);
    }

    /**
     * 创建 wallet（钱包）和 wallet_transaction（交易流水）表（如果不存在）。
     */
    private void createWalletTablesIfMissing() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS wallet (
              id INT NOT NULL AUTO_INCREMENT COMMENT '钱包ID',
              user_id INT NOT NULL COMMENT '用户ID',
              wallet_balance DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '虚拟余额',
              wallet_password VARCHAR(100) DEFAULT NULL COMMENT '兼容旧钱包密码字段',
              create_time DATETIME DEFAULT NULL COMMENT '创建时间',
              update_time DATETIME DEFAULT NULL COMMENT '更新时间',
              PRIMARY KEY (id),
              UNIQUE KEY uk_wallet_user (user_id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='居民虚拟钱包'
            """);
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS wallet_transaction (
              id BIGINT NOT NULL AUTO_INCREMENT COMMENT '流水ID',
              transaction_no VARCHAR(40) NOT NULL COMMENT '交易流水号',
              user_id INT NOT NULL COMMENT '用户ID',
              transaction_type VARCHAR(24) NOT NULL COMMENT 'RECHARGE/TRANSFER_IN/TRANSFER_OUT',
              amount DECIMAL(12,2) NOT NULL COMMENT '带正负号的变动金额',
              balance_after DECIMAL(12,2) NOT NULL COMMENT '交易后余额',
              counterparty_user_id INT DEFAULT NULL COMMENT '交易对方用户ID',
              counterparty_name VARCHAR(80) DEFAULT NULL COMMENT '交易对方名称快照',
              remark VARCHAR(200) DEFAULT NULL COMMENT '交易备注',
              create_time DATETIME DEFAULT NULL COMMENT '创建时间',
              PRIMARY KEY (id),
              KEY idx_wallet_transaction_user_time (user_id, create_time),
              KEY idx_wallet_transaction_no (transaction_no)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='虚拟钱包交易流水'
            """);
    }

    /**
     * 创建 personal_expense 个人消费账单表（如果不存在）。
     */
    private void createPersonalExpenseTableIfMissing() {
        jdbcTemplate.execute("""
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
              create_time DATETIME DEFAULT NULL COMMENT '创建时间',
              update_time DATETIME DEFAULT NULL COMMENT '更新时间',
              PRIMARY KEY (id),
              UNIQUE KEY uk_personal_expense_source (user_id, source_type, source_no),
              KEY idx_personal_expense_user_time (user_id, expense_time)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个人消费账单'
            """);
    }

    /**
     * 创建 goods_store 门店商品关系表（如果不存在）。
     */
    private void createGoodsStoreTableIfMissing() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS goods_store (
              id INT NOT NULL AUTO_INCREMENT COMMENT '主键',
              store_no VARCHAR(50) NOT NULL COMMENT '门店编号',
              goods_no VARCHAR(50) NOT NULL COMMENT '商品编号',
              goods_price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '门店销售价',
              goods_stock INT NOT NULL DEFAULT 0 COMMENT '门店库存',
              goods_type INT DEFAULT 1 COMMENT '门店商品类型',
              create_time DATETIME DEFAULT NULL COMMENT '创建时间',
              update_time DATETIME DEFAULT NULL COMMENT '更新时间',
              PRIMARY KEY (id),
              UNIQUE KEY uk_goods_store_pair (store_no, goods_no),
              KEY idx_goods_store_goods (goods_no),
              KEY idx_goods_store_store (store_no)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店商品价格库存关系'
            """);
    }

    /**
     * 为指定表添加列（如果列不存在）。
     * <p>
     * 先查 information_schema 再 ALTER，保证重复启动时迁移逻辑是幂等的。
     *
     * @param tableName  表名
     * @param columnName 列名
     * @param definition 列定义（类型 + 约束 + 注释）
     */
    private void addColumnIfMissing(String tableName, String columnName, String definition) {
        if (!tableExists(tableName)) {
            return;
        }
        // 先查 information_schema 再 ALTER，保证重复启动应用时迁移逻辑是幂等的。
        Integer count = jdbcTemplate.queryForObject("""
            SELECT COUNT(*)
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = ?
              AND COLUMN_NAME = ?
            """, Integer.class, tableName, columnName);
        if (count == null || count > 0) {
            return;
        }
        jdbcTemplate.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + definition);
    }

    /**
     * 回填 order_detail 表中缺失的商品名称、商品图片和门店名称快照。
     * <p>
     * 只回填空值，已保存的历史快照不会被覆盖。
     */
    private void backfillOrderDetailSnapshots() {
        if (!tableExists("order_detail") || !tableExists("goods") || !tableExists("store")) {
            return;
        }
        // 只回填空值：如果订单明细里已经保存了下单时快照，就以历史交易记录为准。
        jdbcTemplate.update("""
            UPDATE order_detail od
            LEFT JOIN goods g ON od.goods_no = g.goods_no
            LEFT JOIN store s ON od.store_no = s.store_no
            SET
              od.goods_name = COALESCE(NULLIF(od.goods_name, ''), g.goods_name, od.goods_no),
              od.goods_picture = COALESCE(NULLIF(od.goods_picture, ''), g.goods_picture),
              od.store_name = COALESCE(NULLIF(od.store_name, ''), s.store_name, od.store_no)
            WHERE od.goods_name IS NULL OR od.goods_name = ''
               OR od.goods_picture IS NULL OR od.goods_picture = ''
               OR od.store_name IS NULL OR od.store_name = ''
            """);
    }

    /**
     * 检查数据库中是否存在指定表。
     *
     * @param tableName 表名
     * @return 存在返回 true，否则返回 false
     */
    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject("""
            SELECT COUNT(*)
            FROM information_schema.TABLES
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = ?
            """, Integer.class, tableName);
        return count != null && count > 0;
    }
}
