package com.smartcommunity.mall.controller;

import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.entity.GoodsFavorites;
import com.smartcommunity.mall.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final GoodsService goodsService;

    @GetMapping("/list")
    public Result<List<GoodsFavorites>> list(@RequestParam Integer userId) {
        return Result.ok(goodsService.getUserFavorites(userId));
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestParam Integer userId,
                            @RequestParam String goodsNo,
                            @RequestParam(required = false) String storeNo) {
        goodsService.addFavorite(userId, goodsNo, storeNo);
        return Result.ok();
    }

    @DeleteMapping("/remove")
    public Result<Void> remove(@RequestParam Integer userId, @RequestParam String goodsNo) {
        goodsService.removeFavorite(userId, goodsNo);
        return Result.ok();
    }
}
