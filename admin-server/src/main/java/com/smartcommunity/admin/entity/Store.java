package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("store")
public class Store {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer areaId;
    private String storeNo;
    private String storeName;
    private String storeAddress;
    private Double longitude;
    private Double latitude;
    private String storeIntroduce;
    private LocalDateTime startTime;
    private LocalDateTime closeTime;
    private Integer storeStatus;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
