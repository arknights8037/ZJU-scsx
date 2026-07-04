package com.smartcommunity.mall.dto;

import lombok.Data;

/**
 * 商品全站点击次数，只承接推荐查询的统计结果。
 */
@Data
public class GoodsClickStat {
    private String goodsNo;
    private Long clickCount;
}
