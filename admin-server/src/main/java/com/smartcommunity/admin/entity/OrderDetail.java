package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 订单明细实体，对应 order_detail 表（一个订单包含多个商品明细）。 */
@Data
@TableName("order_detail")
public class OrderDetail {
    @TableId(type = IdType.AUTO)
    private Integer id; // 明细ID（主键，自增）
    private String orderNo; // 订单编号
    private String storeNo; // 门店编号
    private String storeName; // 门店名称（快照）
    private String goodsNo; // 商品编号
    private String goodsName; // 商品名称（快照）
    private String goodsPicture; // 商品图片URL（快照）
    private Integer goodsAmount; // 商品数量
    private Double goodsPrice; // 商品单价
    private Double totalPrice; // 该商品小计金额
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
