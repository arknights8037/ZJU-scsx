package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/** 商品类别实体，对应 goods_category 表。 */
@Data
@TableName("goods_category")
public class GoodsCategory {

    @TableId(type = IdType.AUTO)
    private Integer id; // 分类ID（主键，自增）

    private String categoryName; // 分类名称
    private Integer categoryType; // 分类类型
    private Integer parentId; // 父级分类ID

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
