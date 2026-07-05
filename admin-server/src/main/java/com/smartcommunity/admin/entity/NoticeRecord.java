package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 公告阅读记录实体，对应 notice_record 表（记录用户已读状态）。 */
@Data
@TableName("notice_record")
public class NoticeRecord {
    @TableId(type = IdType.AUTO)
    private Integer id; // 记录ID（主键，自增）
    private Integer noticeId; // 公告ID
    private Integer userId; // 用户ID
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
