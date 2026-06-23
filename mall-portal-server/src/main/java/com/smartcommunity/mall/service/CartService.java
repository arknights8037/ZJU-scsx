package com.smartcommunity.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcommunity.mall.entity.Carts;
import com.smartcommunity.mall.mapper.CartsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车服务
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartsMapper cartsMapper;

    public List<Carts> listByUser(Integer userId) {
        LambdaQueryWrapper<Carts> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Carts::getUserId, userId).orderByDesc(Carts::getCreateTime);
        return cartsMapper.selectList(wrapper);
    }

    public void add(Carts cart) {
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

    public void updateAmount(Integer id, Integer amount) {
        Carts cart = cartsMapper.selectById(id);
        if (cart != null) {
            cart.setAmount(amount);
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
}
