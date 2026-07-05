package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 门店实体，对应 store 表（社区商户门店信息）。 */
@Data
@TableName("store")
public class Store {
    @TableId(type = IdType.AUTO)
    private Integer id; // 门店ID（主键，自增）
    private Integer areaId; // 所属区域ID
    private String storeNo; // 门店编号
    private String storeName; // 门店名称
    private String storeAddress; // 门店地址
    private Double longitude; // 经度
    private Double latitude; // 纬度
    private String storeIntroduce; // 门店简介
    private LocalDateTime startTime; // 营业开始时间
    private LocalDateTime closeTime; // 营业结束时间
    private Integer storeStatus; // 门店状态（0关闭 1营业）
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
