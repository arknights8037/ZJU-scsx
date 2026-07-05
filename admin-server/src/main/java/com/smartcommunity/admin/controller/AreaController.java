package com.smartcommunity.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.Area;
import com.smartcommunity.admin.mapper.AreaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 区域管理控制器，提供区域的增删查以及子区域查询。
 * 请求路径前缀：/api/admin/area
 */
@RestController
@RequestMapping("/api/admin/area")
@RequiredArgsConstructor
public class AreaController {
    private final AreaMapper areaMapper;

    /**
     * 获取所有区域列表。
     *
     * @return 区域列表响应
     */
    @GetMapping("/list")
    public Result<List<Area>> list() { return Result.ok(areaMapper.selectList(null)); }

    /**
     * 新增或更新区域。
     *
     * @param a 区域实体
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> save(@RequestBody Area a) { areaMapper.insertOrUpdate(a); return Result.ok(); }

    /**
     * 根据父级 ID 查询子区域列表（用于级联选择器）。
     *
     * @param parentId 父区域 ID
     * @return 子区域列表
     */
    @GetMapping("/children")
    public Result<List<Area>> children(@RequestParam Integer parentId) {
        return Result.ok(areaMapper.selectList(
                new LambdaQueryWrapper<Area>().eq(Area::getParentId, parentId)));
    }
}
