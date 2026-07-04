package com.zjxy.smart.service;

import com.zjxy.smart.mapper.CartsMapper;
import com.zjxy.smart.model.Carts;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CartsService {

    @Autowired
    private CartsMapper cartsMapper;

    public void add(Carts carts){
        Carts carts1 = cartsMapper.selByGoods(carts);
        if(carts1==null){//购物车中没有此商品，则新增
            carts.setAmount(1);
            carts.setCreateTime(new Date());
            cartsMapper.insert(carts);
        }else{//购物车中有此商品，则数量+1
            carts1.setAmount(carts1.getAmount()+1);
            cartsMapper.updateByPrimaryKey(carts1);
        }
    }

    public List<Carts> selByUser(Integer userId) {
        return cartsMapper.selByUser(userId);
    }

    public void update(Carts carts){
        cartsMapper.updateByPrimaryKey(carts);
    }

    public void delByUser(Integer userId){
        cartsMapper.delByUser(userId);
    }

}
