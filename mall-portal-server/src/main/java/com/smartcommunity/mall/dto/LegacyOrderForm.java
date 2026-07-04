package com.smartcommunity.mall.dto;

import com.smartcommunity.mall.entity.Carts;
import lombok.Data;

import java.util.List;

@Data
public class LegacyOrderForm {

    private Integer userId;
    private Double totalPrice;
    private Integer year;
    private String goodsName;
    private List<Carts> records;
}
