package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("goods")
public class Goods {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String goodsNo;
    private String goodsName;
    private Integer categoryId;
    private String goodsIntroduce;
    private String goodsContent;
    private Integer goodsState;
    private String goodsPicture;
    private BigDecimal goodsMarketPrice;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
