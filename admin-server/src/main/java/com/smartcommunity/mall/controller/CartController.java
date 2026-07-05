package com.smartcommunity.mall.controller;

import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.entity.Carts;
import com.smartcommunity.mall.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器，提供购物车商品查询、添加、修改数量、删除和清空功能。
 * 请求路径前缀：/api/cart
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 获取当前用户的购物车商品列表。
     *
     * @param userId 当前登录用户ID（从请求属性中获取）
     * @return 购物车商品列表的通用响应
     */
    @GetMapping("/list")
    public Result<List<Carts>> list(@RequestAttribute Integer userId) {
        return Result.ok(cartService.listDetailByUser(userId));
    }

    /**
     * 向购物车添加商品。
     *
     * @param userId 当前登录用户ID（从请求属性中获取）
     * @param cart   购物车商品信息（商品编号、数量等）
     * @return 空成功的通用响应
     */
    /**
     * 向购物车添加商品。
     *
     * @param userId 当前登录用户ID（从请求属性中获取）
     * @param cart   购物车商品信息（商品编号、数量等）
     * @return 空成功的通用响应
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestAttribute Integer userId, @RequestBody Carts cart) {
        cartService.add(userId, cart);
        return Result.ok();
    }

    /**
     * 修改购物车中某件商品的数量。
     *
     * @param userId 当前登录用户ID
     * @param id     购物车记录ID
     * @param amount 修改后的数量
     * @return 空成功的通用响应
     */
    @PutMapping("/{id}")
    public Result<Void> update(@RequestAttribute Integer userId, @PathVariable Integer id, @RequestParam Integer amount) {
        cartService.updateAmount(userId, id, amount);
        return Result.ok();
    }

    /**
     * 从购物车中删除某件商品。
     *
     * @param userId 当前登录用户ID
     * @param id     购物车记录ID
     * @return 空成功的通用响应
     */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@RequestAttribute Integer userId, @PathVariable Integer id) {
        cartService.remove(userId, id);
        return Result.ok();
    }

    /**
     * 清空当前用户的整个购物车。
     *
     * @param userId 当前登录用户ID
     * @return 空成功的通用响应
     */
    @DeleteMapping("/clear")
    public Result<Void> clear(@RequestAttribute Integer userId) {
        cartService.clearByUser(userId);
        return Result.ok();
    }
}
