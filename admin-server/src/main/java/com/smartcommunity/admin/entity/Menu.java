package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/** 后台菜单实体，对应 menu 表（管理端左侧导航树）。 */
@Data
@TableName("menu")
public class Menu {
    @TableId(type = IdType.AUTO)
    private Integer id; // 菜单ID（主键，自增）
    private String menuName; // 菜单名称
    private Integer parentId; // 父级菜单ID
    private String menuComponent; // 前端组件路径
    private String menuIcon; // 菜单图标
    private String menuPath; // 前端路由路径
    private Integer menuState; // 菜单状态（0隐藏 1显示）
    @TableField(exist = false)
    private List<Menu> children; // 子菜单列表（非数据库字段）
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间
}
