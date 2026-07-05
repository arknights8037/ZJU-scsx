package com.smartcommunity.admin.dto;

import lombok.Data;

import java.util.List;

/** 后台首页业务待办汇总。 */
@Data
public class BusinessTodoOverview {
    private long totalCount; // 待办事项总数
    private long refundCount; // 待处理退款数
    private long repairCount; // 待处理报修数
    private long visitorCount; // 待审核访客数
    private long unpaidChargeCount; // 待缴费账单数
    private List<BusinessTodoItem> items; // 待办事项列表
}
