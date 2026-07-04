package com.smartcommunity.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户点击商品的汇总记录。
 *
 * 一个用户和一个商品只有一行数据，重复浏览时只增加 clickCount。
 * 这种写法比“每点一次插一行”更适合课程项目，数据量小，也方便直接做推荐。
 */
@Data
@TableName("goods_click_record")
public class GoodsClickRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer userId;
    private String goodsNo;
    private Integer clickCount;
    private LocalDateTime lastClickTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
