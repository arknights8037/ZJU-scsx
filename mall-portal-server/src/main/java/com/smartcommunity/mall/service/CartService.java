package com.smartcommunity.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcommunity.mall.entity.Carts;
import com.smartcommunity.mall.entity.Goods;
import com.smartcommunity.mall.entity.GoodsStore;
import com.smartcommunity.mall.entity.Store;
import com.smartcommunity.mall.mapper.CartsMapper;
import com.smartcommunity.mall.mapper.GoodsMapper;
import com.smartcommunity.mall.mapper.GoodsStoreMapper;
import com.smartcommunity.mall.mapper.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 购物车服务。
 *
 * 普通接口只需要 carts 表本身的数据；老师示例页面还需要商品、门店、门店商品价格等详情，
 * 所以这里额外提供 listDetailByUser，把关联对象一起装到 Carts 的非数据库字段里。
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartsMapper cartsMapper;
    private final GoodsMapper goodsMapper;
    private final StoreMapper storeMapper;
    private final GoodsStoreMapper goodsStoreMapper;

    public List<Carts> listByUser(Integer userId) {
        LambdaQueryWrapper<Carts> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Carts::getUserId, userId).orderByDesc(Carts::getCreateTime);
        return cartsMapper.selectList(wrapper);
    }

    /**
     * 返回购物车详情。
     *
     * 注意：这里没有在循环里一条一条查商品，因为那样商品多了会产生很多 SQL。
     * 做法是先批量查出商品、门店、库存价格，再按 goodsNo/storeNo 组装回购物车。
     */
    public List<Carts> listDetailByUser(Integer userId) {
        List<Carts> carts = listByUser(userId);
        fillDetails(carts);
        return carts;
    }

    /**
     * 加入购物车。
     *
     * 如果同一用户、同一商品、同一门店已经有记录，就只累加数量；
     * 否则插入一条新购物车记录。
     */
    public void add(Carts cart) {
        if (cart.getAmount() == null || cart.getAmount() < 1) {
            cart.setAmount(1);
        }
        LambdaQueryWrapper<Carts> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Carts::getUserId, cart.getUserId())
               .eq(Carts::getGoodsNo, cart.getGoodsNo())
               .eq(Carts::getStoreNo, cart.getStoreNo());
        Carts existing = cartsMapper.selectOne(wrapper);
        if (existing != null) {
            existing.setAmount(existing.getAmount() + cart.getAmount());
            cartsMapper.updateById(existing);
        } else {
            cartsMapper.insert(cart);
        }
    }

    /**
     * 修改数量。数量异常时兜底成 1，避免出现 0 件或负数。
     */
    public void updateAmount(Integer id, Integer amount) {
        Carts cart = cartsMapper.selectById(id);
        if (cart != null) {
            cart.setAmount(amount == null || amount < 1 ? 1 : amount);
            cartsMapper.updateById(cart);
        }
    }

    public void remove(Integer id) {
        cartsMapper.deleteById(id);
    }

    public void clearByUser(Integer userId) {
        LambdaQueryWrapper<Carts> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Carts::getUserId, userId);
        cartsMapper.delete(wrapper);
    }

    /**
     * 批量补全购物车中的 goods、store、goodsStore 三个对象。
     *
     * 新人重点看这里：
     * 1. 先从购物车中收集所有 goodsNo 和 storeNo；
     * 2. 用 in(...) 一次性查出关联表数据；
     * 3. 转成 Map，最后再塞回每一条购物车。
     */
    private void fillDetails(List<Carts> carts) {
        if (carts.isEmpty()) {
            return;
        }
        Set<String> goodsNos = carts.stream().map(Carts::getGoodsNo).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<String> storeNos = carts.stream().map(Carts::getStoreNo).filter(Objects::nonNull).collect(Collectors.toSet());
        if (goodsNos.isEmpty() || storeNos.isEmpty()) {
            return;
        }

        Map<String, Goods> goodsMap = goodsMapper.selectList(new LambdaQueryWrapper<Goods>()
                .in(Goods::getGoodsNo, goodsNos))
            .stream()
            .collect(Collectors.toMap(Goods::getGoodsNo, Function.identity(), (a, b) -> a));
        Map<String, Store> storeMap = storeMapper.selectList(new LambdaQueryWrapper<Store>()
                .in(Store::getStoreNo, storeNos))
            .stream()
            .collect(Collectors.toMap(Store::getStoreNo, Function.identity(), (a, b) -> a));
        Map<String, GoodsStore> goodsStoreMap = goodsStoreMapper.selectList(new LambdaQueryWrapper<GoodsStore>()
                .in(GoodsStore::getGoodsNo, goodsNos)
                .in(GoodsStore::getStoreNo, storeNos))
            .stream()
            .collect(Collectors.toMap(this::goodsStoreKey, Function.identity(), (a, b) -> a));

        carts.forEach(cart -> {
            cart.setGoods(goodsMap.get(cart.getGoodsNo()));
            cart.setStore(storeMap.get(cart.getStoreNo()));
            cart.setGoodsStore(goodsStoreMap.get(goodsStoreKey(cart.getStoreNo(), cart.getGoodsNo())));
        });
    }

    /**
     * goods_store 表由 storeNo + goodsNo 共同确定一条记录，所以用这两个字段拼一个 Map 的 key。
     */
    private String goodsStoreKey(GoodsStore goodsStore) {
        return goodsStoreKey(goodsStore.getStoreNo(), goodsStore.getGoodsNo());
    }

    private String goodsStoreKey(String storeNo, String goodsNo) {
        return String.join(":", List.of(
            storeNo == null ? "" : storeNo,
            goodsNo == null ? "" : goodsNo
        ));
    }
}
