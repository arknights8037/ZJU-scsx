package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 车位绑定车辆实体，对应 parking_bind 表（一个车位可绑定多个车牌）。 */
@Data
@TableName("parking_bind")
public class ParkingBind {
    @TableId(type = IdType.AUTO)
    private Integer id; // 绑定ID（主键，自增）
    private Integer parkingId; // 车位ID
    private String licensePlate; // 车牌号
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
