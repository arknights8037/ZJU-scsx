package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品-门店关联（价格/库存）
 */
@Data
@TableName("goods_store")
public class GoodsStore {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String storeNo;
    private String goodsNo;
    private BigDecimal goodsPrice;
    private Integer goodsStock;
    private Integer goodsType;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
