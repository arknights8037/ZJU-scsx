package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 商品分类实体，对应 goods_category 表（后台与居民端共用）。 */
@Data
// 商品和居民端都关联 goods_category，后台必须使用同一张表。
@TableName("goods_category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Integer id; // 分类ID（主键，自增）
    private String categoryName; // 分类名称
    private Integer categoryType; // 分类类型（0商品分类 1其他）
    private Integer parentId; // 父级分类ID
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
