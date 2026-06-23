package com.smartcommunity.mall.controller;

import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.entity.Carts;
import com.smartcommunity.mall.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/list")
    public Result<List<Carts>> list(@RequestParam Integer userId) {
        return Result.ok(cartService.listByUser(userId));
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Carts cart) {
        cartService.add(cart);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestParam Integer amount) {
        cartService.updateAmount(id, amount);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Integer id) {
        cartService.remove(id);
        return Result.ok();
    }

    @DeleteMapping("/clear")
    public Result<Void> clear(@RequestParam Integer userId) {
        cartService.clearByUser(userId);
        return Result.ok();
    }
}
