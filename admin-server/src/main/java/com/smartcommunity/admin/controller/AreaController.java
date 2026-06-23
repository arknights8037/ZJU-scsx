package com.smartcommunity.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.Area;
import com.smartcommunity.admin.mapper.AreaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/area")
@RequiredArgsConstructor
public class AreaController {
    private final AreaMapper areaMapper;

    @GetMapping("/list")
    public Result<List<Area>> list() { return Result.ok(areaMapper.selectList(null)); }

    @PostMapping
    public Result<Void> save(@RequestBody Area a) { areaMapper.insertOrUpdate(a); return Result.ok(); }

    @GetMapping("/children")
    public Result<List<Area>> children(@RequestParam Integer parentId) {
        return Result.ok(areaMapper.selectList(
                new LambdaQueryWrapper<Area>().eq(Area::getParentId, parentId)));
    }
}
