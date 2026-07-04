package com.smartcommunity.mall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthSalesStat {

    private Integer month;
    private Double price;
}
