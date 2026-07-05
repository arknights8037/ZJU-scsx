package com.smartcommunity.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcommunity.admin.dto.BusinessTodoItem;
import com.smartcommunity.admin.dto.BusinessTodoOverview;
import com.smartcommunity.admin.entity.Charge;
import com.smartcommunity.admin.entity.Complaint;
import com.smartcommunity.admin.entity.Orders;
import com.smartcommunity.admin.entity.Visitor;
import com.smartcommunity.admin.mapper.ChargeMapper;
import com.smartcommunity.admin.mapper.ComplaintMapper;
import com.smartcommunity.admin.mapper.OrdersMapper;
import com.smartcommunity.admin.mapper.VisitorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 从各业务表实时汇总管理员需要处理的事项。
 * 业务状态处理完成后，对应事项会自动从工作台消失。
 */
@Service
@RequiredArgsConstructor
public class BusinessTodoService {

    private static final int EACH_TYPE_LIMIT = 10;
    private static final int DISPLAY_LIMIT = 30;

    private final OrdersMapper ordersMapper;
    private final ComplaintMapper complaintMapper;
    private final VisitorMapper visitorMapper;
    private final ChargeMapper chargeMapper;

    /**
     * 汇总管理端工作台所有待办事项。
     * 从订单退款、报修处理、访客审核、未缴费账单四个维度统计数量，
     * 并返回最新/最高优先级的待办列表（最多 30 条）。
     *
     * @return 待办事项总览
     */
    public BusinessTodoOverview overview() {
        BusinessTodoOverview result = new BusinessTodoOverview();
        List<BusinessTodoItem> items = new ArrayList<>();

        // 数量统计用于工作台角标；列表只展示最新/最高优先级的事项，避免首页变成全量查询。
        result.setRefundCount(ordersMapper.selectCount(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getOrderState, 5)));
        result.setRepairCount(complaintMapper.selectCount(new LambdaQueryWrapper<Complaint>()
                .ne(Complaint::getComplaintStatus, 1)));
        result.setVisitorCount(visitorMapper.selectCount(new LambdaQueryWrapper<Visitor>()
                .ne(Visitor::getVisitorStatus, 1)));
        result.setUnpaidChargeCount(chargeMapper.selectCount(new LambdaQueryWrapper<Charge>()
                .ne(Charge::getChargeStatus, 1)));
        result.setTotalCount(result.getRefundCount() + result.getRepairCount()
                + result.getVisitorCount() + result.getUnpaidChargeCount());

        ordersMapper.selectList(new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getOrderState, 5)
                        .orderByDesc(Orders::getRefundTime)
                        .last("limit " + EACH_TYPE_LIMIT))
                .forEach(order -> items.add(refundItem(order)));
        complaintMapper.selectList(new LambdaQueryWrapper<Complaint>()
                        .ne(Complaint::getComplaintStatus, 1)
                        .orderByDesc(Complaint::getCreateTime)
                        .last("limit " + EACH_TYPE_LIMIT))
                .forEach(complaint -> items.add(repairItem(complaint)));
        visitorMapper.selectList(new LambdaQueryWrapper<Visitor>()
                        .ne(Visitor::getVisitorStatus, 1)
                        .orderByDesc(Visitor::getCreateTime)
                        .last("limit " + EACH_TYPE_LIMIT))
                .forEach(visitor -> items.add(visitorItem(visitor)));
        chargeMapper.selectList(new LambdaQueryWrapper<Charge>()
                        .ne(Charge::getChargeStatus, 1)
                        .orderByDesc(Charge::getCreateTime)
                        .last("limit " + EACH_TYPE_LIMIT))
                .forEach(charge -> items.add(chargeItem(charge)));

        items.sort(Comparator.comparing(BusinessTodoItem::getPriority).reversed()
                .thenComparing(BusinessTodoItem::getEventTime,
                        Comparator.nullsLast(Comparator.reverseOrder())));
        // 先按紧急程度，再按时间排序；最多返回 30 条，保证前端工作台保持轻量。
        result.setItems(items.stream().limit(DISPLAY_LIMIT).toList());
        return result;
    }

    /**
     * 将订单退款记录转换为待办事项。
     *
     * @param order 退款订单
     * @return 待办事项
     */
    private BusinessTodoItem refundItem(Orders order) {
        String amount = money(order.getRefundAmount() == null ? order.getTotalPrice() : order.getRefundAmount());
        return new BusinessTodoItem(
                String.valueOf(order.getOrderNo()), "REFUND", "退款审核",
                "订单 " + order.getOrderNo(),
                text(order.getRefundReason(), "居民提交退款申请") + " · 退款 ¥" + amount,
                3, order.getRefundTime() == null ? order.getCreateTime() : order.getRefundTime(),
                "/admin/order/index", order.getOrderNo(), "审核退款");
    }

    /**
     * 将报修工单转换为待办事项。
     * 若内容包含"紧急"关键词，优先级提升为最高。
     *
     * @param complaint 投诉报修记录
     * @return 待办事项
     */
    private BusinessTodoItem repairItem(Complaint complaint) {
        String content = text(complaint.getComplaintContent(), "居民提交报修工单");
        int priority = content.contains("【紧急程度】紧急") || content.contains("紧急报修") ? 3 : 2;
        String summary = stripHtml(content);
        if (!StringUtils.hasText(summary)) summary = "居民上传了报修图片，请进入工单查看详情";
        return new BusinessTodoItem(
                String.valueOf(complaint.getId()), "REPAIR", "报修处理",
                "报修工单 #" + complaint.getId(), excerpt(summary), priority,
                complaint.getCreateTime(), "/admin/community/complaint", null, "标记已处理");
    }

    /**
     * 将访客记录转换为待办事项。
     *
     * @param visitor 访客记录
     * @return 待办事项
     */
    private BusinessTodoItem visitorItem(Visitor visitor) {
        return new BusinessTodoItem(
                String.valueOf(visitor.getId()), "VISITOR", "访客事项",
                "访客记录 #" + visitor.getId(),
                text(visitor.getVisitorObjective(), "未填写来访目的"), 2,
                visitor.getCreateTime() == null ? visitor.getVisitorTime() : visitor.getCreateTime(),
                "/admin/community/visitor", null, "确认完成");
    }

    /**
     * 将未缴费账单转换为待办事项。
     *
     * @param charge 缴费账单
     * @return 待办事项
     */
    private BusinessTodoItem chargeItem(Charge charge) {
        return new BusinessTodoItem(
                String.valueOf(charge.getId()), "CHARGE", "账单跟进",
                text(charge.getChargeName(), "社区账单") + " · " + text(charge.getChargeNo(), "无编号"),
                "待缴金额 ¥" + money(charge.getTotalPrice()), 1,
                charge.getCreateTime(), "/admin/community/charge", null, "查看账单");
    }

    /**
     * 截取文本前 90 个字符作为摘要。
     *
     * @param value 原始文本
     * @return 摘要文本
     */
    private String excerpt(String value) {
        String plain = value.replaceAll("[\\r\\n]+", " ").trim();
        return plain.length() > 90 ? plain.substring(0, 90) + "…" : plain;
    }

    /**
     * 去除 HTML 标签，将 &#60;br&#62; / &#60;/p&#62; 替换为换行符，保留纯文本。
     *
     * @param value 含 HTML 的原始文本
     * @return 纯文本
     */
    private String stripHtml(String value) {
        return value.replaceAll("(?i)<br\\s*/?>", "\n")
                .replaceAll("(?i)</p>", "\n")
                .replaceAll("<[^>]+>", "")
                .replace("&nbsp;", " ")
                .trim();
    }

    /**
     * 将金额格式化为保留两位小数的字符串。
     *
     * @param value 金额数值
     * @return 格式化后的字符串
     */
    private String money(Number value) {
        return String.format("%.2f", value == null ? 0D : value.doubleValue());
    }

    /**
     * 取第一个非空字符串，为空时使用 fallback。
     *
     * @param value    首选值
     * @param fallback 备选值
     * @return 非空字符串
     */
    private String text(String value, String fallback) {
        return StringUtils.hasText(value) ? value : fallback;
    }
}
