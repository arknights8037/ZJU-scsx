package com.smartcommunity.mall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** 月度销售统计数据。 */
@Data
@AllArgsConstructor
public class MonthSalesStat {

    private Integer month; // 月份（1-12）
    private Double price; // 当月销售额
}
