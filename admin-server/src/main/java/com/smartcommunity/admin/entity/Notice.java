package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 公告通知实体，对应 notice 表（系统公告、社区通知等）。 */
@Data
@TableName("notice")
public class Notice {
    @TableId(type = IdType.AUTO)
    private Integer id; // 公告ID（主键，自增）
    private String noticeTitle; // 公告标题
    private String noticeContent; // 公告内容
    private Integer noticeStatus; // 公告状态（0草稿 1已发布 2已下架）
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
