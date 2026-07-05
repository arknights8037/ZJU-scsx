package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 促销与商品的关联记录，对应 special_goods 表（促销活动的参与商品）。 */
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
