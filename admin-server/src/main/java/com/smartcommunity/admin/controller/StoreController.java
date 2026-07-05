package com.smartcommunity.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.dto.AdminStoreView;
import com.smartcommunity.admin.dto.AdminStoreGoodsView;
import com.smartcommunity.admin.entity.Store;
import com.smartcommunity.admin.service.AdminStoreService;
import com.smartcommunity.mall.entity.GoodsStore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 店铺管理控制器，提供店铺的分页查询、新增/编辑、删除以及店铺商品关联管理功能。
 * 请求路径前缀：/api/admin/store
 */
@RestController
@RequestMapping("/api/admin/store")
@RequiredArgsConstructor
public class StoreController {
    private final AdminStoreService storeService;

    /**
     * 分页查询店铺列表，支持关键词、状态和区域筛选。
     *
     * @param page    页码，默认 1
     * @param size    每页条数，默认 10
     * @param keyword 搜索关键词（店铺名称等）
     * @param status  店铺状态筛选
     * @param areaId  区域 ID 筛选
     * @return 店铺分页视图数据
     */
    @GetMapping("/page")
    public Result<Page<AdminStoreView>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer areaId) {
        return Result.ok(storeService.page(page, size, keyword, status, areaId));
    }

    /**
     * 新增或更新店铺。
     *
     * @param store 店铺实体
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> save(@RequestBody Store store) {
        storeService.save(store);
        return Result.ok();
    }

    /**
     * 根据 ID 删除店铺。
     *
     * @param id 店铺 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        storeService.delete(id);
        return Result.ok();
    }

    /**
     * 获取指定店铺的商品列表。
     *
     * @param storeNo 店铺编号
     * @return 店铺商品视图列表
     */
    @GetMapping("/{storeNo}/goods")
    public Result<List<AdminStoreGoodsView>> storeGoods(@PathVariable String storeNo) {
        return Result.ok(storeService.storeGoods(storeNo));
    }

    /**
     * 为指定店铺新增商品关联。
     *
     * @param storeNo  店铺编号
     * @param relation 店铺-商品关联实体
     * @return 新增的店铺商品视图
     */
    @PostMapping("/{storeNo}/goods")
    public Result<AdminStoreGoodsView> saveStoreGoods(@PathVariable String storeNo, @RequestBody GoodsStore relation) {
        return Result.ok(storeService.saveStoreGoods(storeNo, relation));
    }

    /**
     * 删除指定店铺下的某个商品关联。
     *
     * @param storeNo 店铺编号
     * @param id      关联记录 ID
     * @return 操作结果
     */
    @DeleteMapping("/{storeNo}/goods/{id}")
    public Result<Void> deleteStoreGoods(@PathVariable String storeNo, @PathVariable Integer id) {
        storeService.deleteStoreGoods(storeNo, id);
        return Result.ok();
    }
}
