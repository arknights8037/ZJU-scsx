package com.smartcommunity.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.dto.LegacyOrderForm;
import com.smartcommunity.mall.dto.LegacyPageResult;
import com.smartcommunity.mall.dto.MonthSalesStat;
import com.smartcommunity.mall.entity.Carts;
import com.smartcommunity.mall.entity.Goods;
import com.smartcommunity.mall.entity.Orders;
import com.smartcommunity.mall.entity.Store;
import com.smartcommunity.mall.mapper.GoodsMapper;
import com.smartcommunity.mall.mapper.StoreMapper;
import com.smartcommunity.mall.service.CartService;
import com.smartcommunity.mall.service.GoodsService;
import com.smartcommunity.mall.service.OrderService;
import com.smartcommunity.mall.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 兼容老师示例工程中的旧接口路径，保留新版 /api 接口作为主实现。
 */
@RestController
@RequiredArgsConstructor
public class LegacyMallController {

    private static final int TEACHER_PAGE_SIZE = 3;

    private final GoodsService goodsService;
    private final StoreService storeService;
    private final CartService cartService;
    private final OrderService orderService;
    private final GoodsMapper goodsMapper;
    private final StoreMapper storeMapper;

    @RequestMapping("/goods/selById/{id}")
    public Goods goodsById(@PathVariable Integer id) {
        return goodsMapper.selectById(id);
    }

    @RequestMapping("/goods/pageList/{pageNum}")
    public LegacyPageResult<Goods> goodsPage(@PathVariable Integer pageNum, @RequestBody(required = false) Goods goods) {
        Integer categoryId = goods == null ? null : goods.getCategoryId();
        String keyword = goods == null ? null : goods.getGoodsName();
        Page<Goods> page = goodsService.page(pageNum, TEACHER_PAGE_SIZE, categoryId, keyword);
        return LegacyPageResult.of(page);
    }

    @RequestMapping("/store/selById/{id}")
    public Store storeById(@PathVariable Integer id) {
        return storeMapper.selectById(id);
    }

    @RequestMapping("/store/selByNo/{storeNo}")
    public Store storeByNo(@PathVariable String storeNo) {
        return storeService.getByStoreNo(storeNo);
    }

    @RequestMapping("/store/list")
    public List<Store> storeList(@RequestBody(required = false) Store store) {
        if (store != null && store.getAreaId() != null) {
            return storeService.listByArea(store.getAreaId());
        }
        return storeService.listAll();
    }

    @RequestMapping("/carts/list/{userId}")
    public List<Carts> cartList(@PathVariable Integer userId) {
        return cartService.listDetailByUser(userId);
    }

    @RequestMapping("/carts/add")
    public Result<Void> cartAdd(@RequestBody Carts cart) {
        cartService.add(cart);
        return Result.ok();
    }

    @RequestMapping("/carts/update")
    public Result<Void> cartUpdate(@RequestBody Carts cart) {
        cartService.updateAmount(cart.getId(), cart.getAmount());
        return Result.ok();
    }

    @RequestMapping("/carts/delByUser/{userId}")
    public Result<Void> cartClear(@PathVariable Integer userId) {
        cartService.clearByUser(userId);
        return Result.ok();
    }

    @RequestMapping("/orders/add")
    public Result<Void> orderAdd(@RequestBody LegacyOrderForm form) {
        String soldOutGoods = orderService.checkLegacyStock(form);
        if (soldOutGoods != null) {
            return Result.fail(soldOutGoods);
        }
        Result<Void> result = Result.ok();
        result.setMsg(orderService.createLegacy(form));
        return result;
    }

    @RequestMapping("/orders/selByUser/{pageNum}")
    public LegacyPageResult<Orders> ordersByUser(@PathVariable Integer pageNum, @RequestBody LegacyOrderForm form) {
        Page<Orders> page = orderService.pageByUser(form.getUserId(), pageNum, TEACHER_PAGE_SIZE, form.getGoodsName());
        return LegacyPageResult.of(page);
    }

    @RequestMapping("/orders/selByMonth")
    public List<MonthSalesStat> monthSales(@RequestBody LegacyOrderForm form) {
        Integer year = form.getYear() == null ? java.time.LocalDate.now().getYear() : form.getYear();
        return orderService.monthSales(form.getUserId(), year);
    }
}
