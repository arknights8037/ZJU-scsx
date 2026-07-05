package com.smartcommunity.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.mall.dto.PersonalExpenseForm;
import com.smartcommunity.mall.dto.PersonalExpenseSummary;
import com.smartcommunity.mall.entity.PersonalExpense;
import com.smartcommunity.mall.entity.Orders;
import com.smartcommunity.mall.entity.Charge;
import com.smartcommunity.mall.mapper.OrdersMapper;
import com.smartcommunity.mall.mapper.ChargeMapper;
import com.smartcommunity.mall.mapper.PersonalExpenseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

/**
 * 居民端个人消费台账服务。
 *
 * 独立于钱包系统，仅记录消费流水，不读取或修改钱包余额。
 * 支持自动补录历史订单和社区缴费数据，初次使用时将已付款订单同步到消费台账。
 */
@Service
@RequiredArgsConstructor
public class PersonalExpenseService {

    private final PersonalExpenseMapper expenseMapper;
    private final OrdersMapper ordersMapper;
    private final ChargeMapper chargeMapper;

    /**
     * 分页查询用户个人消费记录。
     * 首次查询时会自动补录历史订单和社区缴费数据。
     *
     * @param userId 用户 ID
     * @param page   页码
     * @param size   每页条数
     * @return 消费记录分页结果
     */
    @Transactional
    public Page<PersonalExpense> page(Integer userId, int page, int size) {
        backfillHistoricalExpenses(userId);
        return expenseMapper.selectPage(new Page<>(Math.max(page, 1), Math.min(Math.max(size, 1), 50)),
                new LambdaQueryWrapper<PersonalExpense>()
                        .eq(PersonalExpense::getUserId, userId)
                        .orderByDesc(PersonalExpense::getExpenseTime)
                        .orderByDesc(PersonalExpense::getId));
    }

    /**
     * 获取用户消费汇总：累计支出、本月支出、总笔数。
     *
     * @param userId 用户 ID
     * @return 消费汇总对象
     */
    @Transactional
    public PersonalExpenseSummary summary(Integer userId) {
        backfillHistoricalExpenses(userId);
        List<PersonalExpense> records = expenseMapper.selectList(new LambdaQueryWrapper<PersonalExpense>()
                .eq(PersonalExpense::getUserId, userId));
        YearMonth currentMonth = YearMonth.now();
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal month = BigDecimal.ZERO;
        for (PersonalExpense record : records) {
            BigDecimal amount = record.getAmount() == null ? BigDecimal.ZERO : record.getAmount();
            total = total.add(amount);
            if (record.getExpenseTime() != null && YearMonth.from(record.getExpenseTime()).equals(currentMonth)) {
                month = month.add(amount);
            }
        }
        return new PersonalExpenseSummary(total.setScale(2), month.setScale(2), records.size());
    }

    /**
     * 手动创建一条个人消费记录（非商城/社区缴费来源）。
     *
     * @param userId 用户 ID
     * @param form   手工支出表单
     * @return 创建的消费记录
     */
    @Transactional
    public PersonalExpense createManual(Integer userId, PersonalExpenseForm form) {
        if (form == null || !StringUtils.hasText(form.getExpenseTitle())) {
            throw new RuntimeException("请填写支出名称");
        }
        PersonalExpense expense = new PersonalExpense();
        String expenseNo = expenseNo();
        expense.setExpenseNo(expenseNo);
        expense.setUserId(userId);
        expense.setSourceType("MANUAL");
        expense.setSourceNo(expenseNo);
        expense.setCategory(text(form.getCategory(), "其他支出"));
        expense.setExpenseTitle(form.getExpenseTitle().trim());
        expense.setAmount(normalizeAmount(form.getAmount()));
        expense.setExpenseTime(form.getExpenseTime() == null ? LocalDateTime.now() : form.getExpenseTime());
        expense.setRemark(trimToNull(form.getRemark()));
        expenseMapper.insert(expense);
        return expense;
    }

    /**
     * 删除用户手动创建的消费记录。商城订单和社区缴费自动记录不可手动删除。
     *
     * @param userId 用户 ID
     * @param id     记录 ID
     */
    public void deleteManual(Integer userId, Long id) {
        PersonalExpense expense = expenseMapper.selectOne(new LambdaQueryWrapper<PersonalExpense>()
                .eq(PersonalExpense::getId, id)
                .eq(PersonalExpense::getUserId, userId)
                .last("limit 1"));
        if (expense == null) throw new RuntimeException("账单不存在");
        if (!"MANUAL".equals(expense.getSourceType())) throw new RuntimeException("商城账单不能手工删除");
        expenseMapper.deleteById(id);
    }

    /** 支付成功后自动写入商城支出；唯一索引确保同一订单不会重复记账。 */
    public void recordOrderExpense(Integer userId, String orderNo, BigDecimal amount) {
        PersonalExpense expense = new PersonalExpense();
        expense.setExpenseNo(expenseNo());
        expense.setUserId(userId);
        expense.setSourceType("ORDER");
        expense.setSourceNo(orderNo);
        expense.setCategory("商城购物");
        expense.setExpenseTitle("商城订单消费");
        expense.setAmount(amount.setScale(2, RoundingMode.HALF_UP));
        expense.setExpenseTime(LocalDateTime.now());
        expense.setRemark("订单号 " + orderNo);
        insertIfMissing(expense);
    }

    /**
     * 记录社区缴费支出到个人消费台账。
     *
     * @param userId 用户 ID
     * @param charge 缴费账单
     */
    public void recordChargeExpense(Integer userId, Charge charge) {
        if (charge == null || charge.getChargeNo() == null) return;
        PersonalExpense expense = new PersonalExpense();
        expense.setExpenseNo(expenseNo());
        expense.setUserId(userId);
        expense.setSourceType("CHARGE");
        expense.setSourceNo(charge.getChargeNo());
        expense.setCategory("社区缴费");
        expense.setExpenseTitle(text(charge.getChargeName(), "社区账单缴费"));
        expense.setAmount(BigDecimal.valueOf(charge.getTotalPrice() == null ? 0D : charge.getTotalPrice())
                .setScale(2, RoundingMode.HALF_UP));
        expense.setExpenseTime(firstTime(charge.getUpdateTime(), charge.getCreateTime(), LocalDateTime.now()));
        expense.setRemark("账单号 " + charge.getChargeNo());
        insertIfMissing(expense);
    }

    /** 订单退款后写入负向账单，用于冲减个人支出统计；不直接操作钱包。 */
    public void recordOrderRefund(Integer userId, String orderNo, BigDecimal amount) {
        if (userId == null || !StringUtils.hasText(orderNo) || amount == null) return;
        BigDecimal refundAmount = amount.setScale(2, RoundingMode.HALF_UP);
        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) return;
        PersonalExpense expense = new PersonalExpense();
        expense.setExpenseNo(expenseNo());
        expense.setUserId(userId);
        expense.setSourceType("ORDER_REFUND");
        expense.setSourceNo(orderNo);
        expense.setCategory("商城退款");
        expense.setExpenseTitle("商城订单退款");
        expense.setAmount(refundAmount.negate());
        expense.setExpenseTime(LocalDateTime.now());
        expense.setRemark("退款订单号 " + orderNo);
        insertIfMissing(expense);
    }

    /**
     * 首次打开账单时补录旧数据：已付款商城订单和已缴社区账单。
     * 这样新功能上线前已经产生的支出也能在个人账单中看到。
     */
    public void backfillHistoricalExpenses(Integer userId) {
        ordersMapper.selectList(new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getUserId, userId)
                        .in(Orders::getOrderState, List.of(2, 3, 4, 5, 6, 9)))
                .forEach(order -> {
                    PersonalExpense expense = new PersonalExpense();
                    expense.setExpenseNo(expenseNo());
                    expense.setUserId(userId);
                    expense.setSourceType("ORDER");
                    expense.setSourceNo(order.getOrderNo());
                    expense.setCategory("商城购物");
                    expense.setExpenseTitle("商城历史订单消费");
                    expense.setAmount(BigDecimal.valueOf(order.getTotalPrice() == null ? 0D : order.getTotalPrice())
                            .setScale(2, RoundingMode.HALF_UP));
                    expense.setExpenseTime(firstTime(order.getCreateTime(), order.getUpdateTime(), LocalDateTime.now()));
                    expense.setRemark("订单号 " + order.getOrderNo());
                    insertIfMissing(expense);
                });
        ordersMapper.selectList(new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getUserId, userId)
                        .eq(Orders::getOrderState, 6))
                .forEach(order -> {
                    BigDecimal refundAmount = order.getRefundAmount() == null
                            ? BigDecimal.valueOf(order.getTotalPrice() == null ? 0D : order.getTotalPrice())
                            : order.getRefundAmount();
                    recordOrderRefund(userId, order.getOrderNo(), refundAmount);
                });
        chargeMapper.selectList(new LambdaQueryWrapper<Charge>()
                        .eq(Charge::getUserId, userId)
                        .eq(Charge::getChargeStatus, 1))
                .forEach(charge -> recordChargeExpense(userId, charge));
    }

    /**
     * 按 sourceType + sourceNo 判断是否已存在，不存在则插入。
     * 用于幂等写入，避免重复记账。
     *
     * @param expense 消费记录
     */
    private void insertIfMissing(PersonalExpense expense) {
        if (expense.getAmount() == null || expense.getAmount().compareTo(BigDecimal.ZERO) == 0) return;
        Long exists = expenseMapper.selectCount(new LambdaQueryWrapper<PersonalExpense>()
                .eq(PersonalExpense::getUserId, expense.getUserId())
                .eq(PersonalExpense::getSourceType, expense.getSourceType())
                .eq(PersonalExpense::getSourceNo, expense.getSourceNo()));
        if (exists != null && exists > 0) return;
        try {
            expenseMapper.insert(expense);
        } catch (DuplicateKeyException ignored) {
            // 并发回填或接口重试时不重复生成消费账单。
        }
    }

    /**
     * 校验并标准化支出金额：大于 0 且不超过 100 万元。
     *
     * @param value 原始金额
     * @return 标准化后的金额
     */
    private BigDecimal normalizeAmount(BigDecimal value) {
        if (value == null) throw new RuntimeException("请输入支出金额");
        BigDecimal amount = value.setScale(2, RoundingMode.HALF_UP);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new RuntimeException("支出金额必须大于0元");
        if (amount.compareTo(new BigDecimal("1000000")) > 0) throw new RuntimeException("单笔支出金额过大");
        return amount;
    }

    /**
     * 生成唯一消费记录编号（PE + UUID）。
     *
     * @return 编号字符串
     */
    private String expenseNo() {
        return "PE" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    private String text(String value, String fallback) {
        return StringUtils.hasText(value) ? value.trim() : fallback;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    /**
     * 取第一个非 null 的时间，全部为 null 时返回 fallback。
     *
     * @param first    首选时间
     * @param second   备选时间
     * @param fallback 兜底时间
     * @return 非 null 的时间
     */
    private LocalDateTime firstTime(LocalDateTime first, LocalDateTime second, LocalDateTime fallback) {
        if (first != null) return first;
        if (second != null) return second;
        return fallback;
    }
}
