package com.smartcommunity.mall.controller;

import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.dto.FavoriteGoodsView;
import com.smartcommunity.mall.entity.GoodsFavorites;
import com.smartcommunity.mall.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品收藏控制器，提供收藏列表查看、添加收藏和取消收藏功能。
 * 请求路径前缀：/api/favorite
 */
@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final GoodsService goodsService;

    /**
     * 获取当前用户的收藏商品列表。
     *
     * @param userId 当前登录用户ID
     * @return 收藏商品视图列表的通用响应
     */
    @GetMapping("/list")
    public Result<List<FavoriteGoodsView>> list(@RequestAttribute Integer userId) {
        return Result.ok(goodsService.getUserFavorites(userId));
    }

    /**
     * 添加商品或门店到收藏。
     *
     * @param userId  当前登录用户ID
     * @param goodsNo 商品编号
     * @param storeNo 门店编号（可选，收藏门店时传入）
     * @return 空成功的通用响应
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestAttribute Integer userId,
                            @RequestParam String goodsNo,
                            @RequestParam(required = false) String storeNo) {
        goodsService.addFavorite(userId, goodsNo, storeNo);
        return Result.ok();
    }

    /**
     * 取消收藏某件商品。
     *
     * @param userId  当前登录用户ID
     * @param goodsNo 商品编号
     * @return 空成功的通用响应
     */
    @DeleteMapping("/remove")
    public Result<Void> remove(@RequestAttribute Integer userId, @RequestParam String goodsNo) {
        goodsService.removeFavorite(userId, goodsNo);
        return Result.ok();
    }
}
