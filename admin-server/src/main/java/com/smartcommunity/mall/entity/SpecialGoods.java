package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/** 促销商品关联实体，对应 special_goods 表（促销与商品的多对多关系）。 */
@Data
@TableName("special_goods")
public class SpecialGoods {

    @TableId(type = IdType.AUTO)
    private Integer id; // 关联ID（主键，自增）

    private Integer specialId; // 促销活动ID
    private String goodsNo; // 商品编号

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
