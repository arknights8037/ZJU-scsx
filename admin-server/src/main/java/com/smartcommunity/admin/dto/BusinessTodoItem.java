package com.smartcommunity.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/** 工作台中由真实业务数据生成的待处理事件。 */
@Data
@AllArgsConstructor
public class BusinessTodoItem {
    private String id; // 待办项ID
    private String type; // 待办类型（refund/repair/visitor/charge）
    private String typeName; // 类型中文名称
    private String title; // 待办标题
    private String description; // 待办描述
    private Integer priority; // 优先级（数值越大越紧急）
    private LocalDateTime eventTime; // 事件发生时间
    private String path; // 点击后跳转的路由路径
    private String keyword; // 搜索关键词
    private String actionLabel; // 操作按钮文字
}
