package com.smartcommunity.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** 后台全局搜索结果。 */
@Data
@AllArgsConstructor
public class GlobalSearchItem {
    private String type; // 结果类型（order/store/goods/user等）
    private String typeName; // 类型中文名称
    private String title; // 结果标题
    private String subtitle; // 结果副标题
    private String path; // 点击后跳转的路由路径
    private String keyword; // 匹配的关键词
}
