package com.smartcommunity.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("menu")
public class Menu {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String menuName;
    private Integer parentId;
    private String menuComponent;
    private String menuIcon;
    private String menuPath;
    private Integer menuState;
    @TableField(exist = false)
    private List<Menu> children;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
