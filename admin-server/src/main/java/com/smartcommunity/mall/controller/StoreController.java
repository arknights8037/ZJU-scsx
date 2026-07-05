package com.smartcommunity.mall.controller;

import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.entity.Store;
import com.smartcommunity.mall.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 门店控制器，提供门店列表查询和门店详情查询功能。
 * 请求路径前缀：/api/store
 */
@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    /**
     * 获取门店列表，可按区域ID筛选。
     *
     * @param areaId 区域ID（可选），不为空时按区域筛选
     * @return 门店列表的通用响应
     */
    @GetMapping("/list")
    public Result<List<Store>> list(@RequestParam(required = false) Integer areaId) {
        // 区域ID不为空则按区域查询，否则查询全部
        if (areaId != null) {
            return Result.ok(storeService.listByArea(areaId));
        }
        return Result.ok(storeService.listAll());
    }

    /**
     * 根据门店编号获取门店详情。
     *
     * @param storeNo 门店编号
     * @return 门店详情的通用响应
     */
    @GetMapping("/{storeNo}")
    public Result<Store> detail(@PathVariable String storeNo) {
        return Result.ok(storeService.getByStoreNo(storeNo));
    }
}
