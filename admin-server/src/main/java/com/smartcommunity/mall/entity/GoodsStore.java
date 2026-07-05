package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 商品-门店关联实体，对应 goods_store 表（门店商品定价与库存）。 */
@Data
@TableName("goods_store")
public class GoodsStore {

    @TableId(type = IdType.AUTO)
    private Integer id; // 关联ID（主键，自增）

    private String storeNo; // 门店编号
    private String goodsNo; // 商品编号
    private BigDecimal goodsPrice; // 门店售价
    private Integer goodsStock; // 门店库存
    private Integer goodsType; // 商品类型

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
