package com.smartcommunity.mall.controller;

import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.entity.Store;
import com.smartcommunity.mall.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 门店控制器
 */
@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/list")
    public Result<List<Store>> list(@RequestParam(required = false) Integer areaId) {
        if (areaId != null) {
            return Result.ok(storeService.listByArea(areaId));
        }
        return Result.ok(storeService.listAll());
    }

    @GetMapping("/{storeNo}")
    public Result<Store> detail(@PathVariable String storeNo) {
        return Result.ok(storeService.getByStoreNo(storeNo));
    }
}
