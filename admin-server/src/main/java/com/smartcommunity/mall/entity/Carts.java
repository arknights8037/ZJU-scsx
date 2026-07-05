package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/** 购物车实体，对应 carts 表（居民端购物车商品记录）。 */
@Data
@TableName("carts")
public class Carts {

    @TableId(type = IdType.AUTO)
    private Integer id; // 购物车记录ID（主键，自增）

    private Integer userId; // 用户ID
    private String storeNo; // 门店编号
    private String goodsNo; // 商品编号
    private Integer amount; // 商品数量

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间

    @TableField(exist = false)
    private Goods goods; // 关联商品信息（非数据库字段）

    @TableField(exist = false)
    private Store store; // 关联门店信息（非数据库字段）

    @TableField(exist = false)
    private GoodsStore goodsStore; // 关联门店商品信息（非数据库字段）
}
