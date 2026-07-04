package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
// 商品和居民端都关联 goods_category，后台必须使用同一张表。
@TableName("goods_category")
public class Category {
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
