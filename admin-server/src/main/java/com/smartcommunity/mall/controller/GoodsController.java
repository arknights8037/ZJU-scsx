package com.smartcommunity.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.mall.common.PageResult;
import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.entity.*;
import com.smartcommunity.mall.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.math.BigDecimal;

/**
 * 商品控制器，提供商品分页查询、详情、点击记录、推荐、图片和门店关联查询功能。
 * 请求路径前缀：/api/goods
 */
@RestController
@RequestMapping("/api/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    /**
     * 分页查询商品列表，支持按分类、关键词、价格区间、库存状态和排序方式筛选。
     *
     * @param page       页码，默认第1页
     * @param size       每页条数，默认10条
     * @param categoryId 商品分类ID（可选）
     * @param keyword    搜索关键词（可选，按商品名称模糊匹配）
     * @param minPrice   最低价格（可选）
     * @param maxPrice   最高价格（可选）
     * @param hasStock   是否有货（可选）
     * @param sort       排序方式（可选，如 price_asc、price_desc 等）
     * @return 商品分页结果的通用响应
     */
    @GetMapping("/page")
    public Result<PageResult<Goods>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean hasStock,
            @RequestParam(required = false) String sort) {
        Page<Goods> result = goodsService.page(page, size, categoryId, keyword, minPrice, maxPrice, hasStock, sort);
        return Result.ok(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    /**
     * 根据商品编号获取商品详情。
     *
     * @param goodsNo 商品编号
     * @return 商品详情的通用响应
     */
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

    /**
     * 获取推荐商品列表，基于用户的浏览和购买历史。
     *
     * @param userId         当前登录用户ID（可选，未登录用户使用默认推荐）
     * @param excludeGoodsNo 需要排除的商品编号（可选，如当前查看的商品）
     * @param size           推荐数量，默认6个
     * @return 推荐商品列表的通用响应
     */
    @GetMapping("/recommend")
    public Result<List<Goods>> recommend(
            @RequestAttribute(required = false) Integer userId,
            @RequestParam(required = false) String excludeGoodsNo,
            @RequestParam(defaultValue = "6") Integer size) {
        return Result.ok(goodsService.recommend(userId, excludeGoodsNo, size));
    }

    /**
     * 获取指定商品的图片列表。
     *
     * @param goodsNo 商品编号
     * @return 商品图片列表的通用响应
     */
    @GetMapping("/{goodsNo}/pictures")
    public Result<List<GoodsPicture>> pictures(@PathVariable String goodsNo) {
        return Result.ok(goodsService.getPictures(goodsNo));
    }

    /**
     * 获取指定商品在各门店的库存和价格信息。
     *
     * @param goodsNo 商品编号
     * @return 商品门店关联信息列表的通用响应
     */
    @GetMapping("/{goodsNo}/stores")
    public Result<List<GoodsStore>> storeGoods(@PathVariable String goodsNo) {
        return Result.ok(goodsService.getStoreGoods(goodsNo));
    }
}
