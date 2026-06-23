package com.smartcommunity.admin.controller;

import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.Special;
import com.smartcommunity.admin.mapper.SpecialMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/special")
@RequiredArgsConstructor
public class SpecialController {
    private final SpecialMapper specialMapper;

    @GetMapping("/list")
    public Result<List<Special>> list() { return Result.ok(specialMapper.selectList(null)); }

    @PostMapping
    public Result<Void> save(@RequestBody Special s) { specialMapper.insertOrUpdate(s); return Result.ok(); }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) { specialMapper.deleteById(id); return Result.ok(); }
}
