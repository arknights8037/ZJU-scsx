package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品类别
 */
@Data
@TableName("goods_category")
public class GoodsCategory {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String categoryName;
    private Integer categoryType;
    private Integer parentId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
