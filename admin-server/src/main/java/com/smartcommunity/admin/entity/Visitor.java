package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 访客记录实体，对应 visitor 表（业主邀请的访客信息）。 */
@Data
@TableName("visitor")
public class Visitor {
    @TableId(type = IdType.AUTO)
    private Integer id; // 记录ID（主键，自增）
    private Integer userId; // 业主用户ID
    private String visitorObjective; // 访客事由
    private LocalDateTime visitorTime; // 预计到访时间
    private Integer visitorStatus; // 访客状态（0待审核 1已通过 2已拒绝 3已过期）
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
