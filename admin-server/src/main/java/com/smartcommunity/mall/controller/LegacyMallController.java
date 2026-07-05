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

    /**
     * 根据商品ID获取商品信息。
     *
     * @param id 商品ID
     * @return 商品实体
     */
    @RequestMapping("/goods/selById/{id}")
    public Goods goodsById(@PathVariable Integer id) {
        return goodsMapper.selectById(id);
    }

    /**
     * 分页查询商品列表（兼容旧版接口）。
     *
     * @param pageNum 页码
     * @param goods   查询条件（可选，含分类ID和商品名称关键词）
     * @return 兼容旧版的分页结果
     */
    @RequestMapping("/goods/pageList/{pageNum}")
    public LegacyPageResult<Goods> goodsPage(@PathVariable Integer pageNum, @RequestBody(required = false) Goods goods) {
        // 从请求体中提取筛选条件
        Integer categoryId = goods == null ? null : goods.getCategoryId();
        String keyword = goods == null ? null : goods.getGoodsName();
        Page<Goods> page = goodsService.page(pageNum, TEACHER_PAGE_SIZE, categoryId, keyword);
        return LegacyPageResult.of(page);
    }

    /**
     * 根据门店ID获取门店信息。
     *
     * @param id 门店ID
     * @return 门店实体
     */
    @RequestMapping("/store/selById/{id}")
    public Store storeById(@PathVariable Integer id) {
        return storeMapper.selectById(id);
    }

    /**
     * 根据门店编号获取门店信息。
     *
     * @param storeNo 门店编号
     * @return 门店实体
     */
    @RequestMapping("/store/selByNo/{storeNo}")
    public Store storeByNo(@PathVariable String storeNo) {
        return storeService.getByStoreNo(storeNo);
    }

    /**
     * 获取门店列表，可按区域筛选。
     *
     * @param store 查询条件（可选，含区域ID）
     * @return 门店列表
     */
    @RequestMapping("/store/list")
    public List<Store> storeList(@RequestBody(required = false) Store store) {
        // 区域ID不为空则按区域筛选
        if (store != null && store.getAreaId() != null) {
            return storeService.listByArea(store.getAreaId());
        }
        return storeService.listAll();
    }

    /**
     * 根据用户ID获取购物车列表。
     *
     * @param userId 用户ID
     * @return 购物车商品列表
     */
    @RequestMapping("/carts/list/{userId}")
    public List<Carts> cartList(@PathVariable Integer userId) {
        return cartService.listDetailByUser(userId);
    }

    /**
     * 向购物车添加商品。
     *
     * @param cart 购物车商品信息
     * @return 通用成功响应
     */
    @RequestMapping("/carts/add")
    public Result<Void> cartAdd(@RequestBody Carts cart) {
        cartService.add(cart);
        return Result.ok();
    }

    /**
     * 修改购物车中商品的数量。
     *
     * @param cart 购物车商品信息（含ID和更新后的数量）
     * @return 通用成功响应
     */
    @RequestMapping("/carts/update")
    public Result<Void> cartUpdate(@RequestBody Carts cart) {
        cartService.updateAmount(cart.getId(), cart.getAmount());
        return Result.ok();
    }

    /**
     * 清空指定用户的购物车。
     *
     * @param userId 用户ID
     * @return 通用成功响应
     */
    @RequestMapping("/carts/delByUser/{userId}")
    public Result<Void> cartClear(@PathVariable Integer userId) {
        cartService.clearByUser(userId);
        return Result.ok();
    }

    /**
     * 创建订单（兼容旧版接口，含库存校验）。
     *
     * @param form 旧版订单表单（含用户ID、商品列表等）
     * @return 通用响应，失败时返回缺货提示
     */
    @RequestMapping("/orders/add")
    public Result<Void> orderAdd(@RequestBody LegacyOrderForm form) {
        // 检查库存是否充足，返回缺货的商品名称
        String soldOutGoods = orderService.checkLegacyStock(form);
        if (soldOutGoods != null) {
            return Result.fail(soldOutGoods);
        }
        Result<Void> result = Result.ok();
        // 创建订单并返回订单号
        result.setMsg(orderService.createLegacy(form));
        return result;
    }

    /**
     * 分页查询指定用户的订单（兼容旧版接口）。
     *
     * @param pageNum 页码
     * @param form    查询条件（含用户ID和商品名称关键词）
     * @return 兼容旧版的分页订单结果
     */
    @RequestMapping("/orders/selByUser/{pageNum}")
    public LegacyPageResult<Orders> ordersByUser(@PathVariable Integer pageNum, @RequestBody LegacyOrderForm form) {
        Page<Orders> page = orderService.pageByUser(form.getUserId(), pageNum, TEACHER_PAGE_SIZE, form.getGoodsName());
        return LegacyPageResult.of(page);
    }

    /**
     * 查询指定用户各月的销售额统计。
     *
     * @param form 查询条件（含用户ID和年份，年份默认为当前年）
     * @return 月度销售额统计列表
     */
    @RequestMapping("/orders/selByMonth")
    public List<MonthSalesStat> monthSales(@RequestBody LegacyOrderForm form) {
        // 未指定年份时默认使用当前年份
        Integer year = form.getYear() == null ? java.time.LocalDate.now().getYear() : form.getYear();
        return orderService.monthSales(form.getUserId(), year);
    }
}
