package com.smartcommunity.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.mall.common.PageResult;
import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.entity.*;
import com.smartcommunity.mall.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/api/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    @GetMapping("/page")
    public Result<PageResult<Goods>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword) {
        Page<Goods> result = goodsService.page(page, size, categoryId, keyword);
        return Result.ok(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    @GetMapping("/{goodsNo}")
    public Result<Goods> detail(@PathVariable String goodsNo) {
        return Result.ok(goodsService.getByGoodsNo(goodsNo));
    }

    /**
     * 记录当前登录用户的一次商品点击。未登录用户没有 userId，因此不写个人记录。
     */
    @PostMapping("/{goodsNo}/click")
    public Result<Void> recordClick(
            @PathVariable String goodsNo,
            @RequestAttribute(required = false) Integer userId) {
        goodsService.recordClick(userId, goodsNo);
        return Result.ok();
    }

    @GetMapping("/recommend")
    public Result<List<Goods>> recommend(
            @RequestAttribute(required = false) Integer userId,
            @RequestParam(required = false) String excludeGoodsNo,
            @RequestParam(defaultValue = "6") Integer size) {
        return Result.ok(goodsService.recommend(userId, excludeGoodsNo, size));
    }

    @GetMapping("/{goodsNo}/pictures")
    public Result<List<GoodsPicture>> pictures(@PathVariable String goodsNo) {
        return Result.ok(goodsService.getPictures(goodsNo));
    }

    @GetMapping("/{goodsNo}/stores")
    public Result<List<GoodsStore>> storeGoods(@PathVariable String goodsNo) {
        return Result.ok(goodsService.getStoreGoods(goodsNo));
    }
}
