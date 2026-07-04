package com.smartcommunity.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.dto.AdminStoreView;
import com.smartcommunity.admin.entity.Store;
import com.smartcommunity.admin.service.AdminStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/store")
@RequiredArgsConstructor
public class StoreController {
    private final AdminStoreService storeService;

    @GetMapping("/page")
    public Result<Page<AdminStoreView>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer areaId) {
        return Result.ok(storeService.page(page, size, keyword, status, areaId));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Store store) {
        storeService.save(store);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        storeService.delete(id);
        return Result.ok();
    }
}
