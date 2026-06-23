package com.smartcommunity.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.Store;
import com.smartcommunity.admin.mapper.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/store")
@RequiredArgsConstructor
public class StoreController {
    private final StoreMapper storeMapper;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Store> p = storeMapper.selectPage(new Page<>(page, size), null);
        return Result.ok(p);
    }

    @PostMapping
    public Result<Void> save(@RequestBody Store s) { storeMapper.insertOrUpdate(s); return Result.ok(); }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) { storeMapper.deleteById(id); return Result.ok(); }
}
