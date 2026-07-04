package com.zjxy.smart.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjxy.smart.common.Define;
import com.zjxy.smart.common.Result;
import com.zjxy.smart.form.OrdersForm;
import com.zjxy.smart.mapper.CartsMapper;
import com.zjxy.smart.mapper.GoodsStoreMapper;
import com.zjxy.smart.mapper.OrderDetailMapper;
import com.zjxy.smart.mapper.OrdersMapper;
import com.zjxy.smart.model.Carts;
import com.zjxy.smart.model.GoodsStore;
import com.zjxy.smart.model.OrderDetail;
import com.zjxy.smart.model.Orders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private CartsMapper cartsMapper;

    @Autowired
    private GoodsStoreMapper goodsStoreMapper;

    /**
     * 检查商品库存
     * @return
     */
    public Result checkMount(OrdersForm ordersForm){
        Result result = new Result();
        for(Carts item: ordersForm.getRecords()){
            GoodsStore goodsStore = new GoodsStore();
            goodsStore.setStoreNo(item.getStoreNo());//门店编号
            goodsStore.setGoodsNo(item.getGoodsNo()); //商品编号
            GoodsStore goodsStore1 = goodsStoreMapper.selByNo(goodsStore);
            //库存
            Integer stock = goodsStore1.getGoodsStock();
            if(stock-item.getAmount()<0){
                result.setCode(500);
                result.setMsg(item.getGoods().getGoodsName());
                return result;
            }
        }
        result.setCode(200);
        return result;
    }

    public String add(OrdersForm ordersForm){
        String orderNo = Define.generatorCodeNo();
        ordersForm.setOrderNo(orderNo);
        ordersForm.setDeliveryType(1);
        ordersForm.setPaymentSubtype(1);
        ordersForm.setPaymentType(1);
        ordersForm.setOrderState(1);
        ordersForm.setCreateTime(new Date());

        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersForm, orders);
        //新增订单
        ordersMapper.insert(orders);
        for(Carts item: ordersForm.getRecords()){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderNo(orderNo);
            orderDetail.setStoreNo(item.getStoreNo());
            orderDetail.setGoodsNo(item.getGoodsNo());
            orderDetail.setGoodsAmount(item.getAmount());
            orderDetail.setGoodsPrice(item.getGoodsStore().getGoodsPrice().doubleValue());
            orderDetail.setTotalPrice(item.getGoodsStore().getGoodsPrice().doubleValue() * item.getAmount());
            orderDetail.setCreateTime(new Date());
            orderDetailMapper.insert(orderDetail);
            //删除购物车中此商品
            cartsMapper.deleteByPrimaryKey(item.getId());
            //更新库存
            GoodsStore goodsStore = new GoodsStore();
            goodsStore.setStoreNo(item.getStoreNo());//门店编号
            goodsStore.setGoodsNo(item.getGoodsNo()); //商品编号
            GoodsStore goodsStore1 = goodsStoreMapper.selByNo(goodsStore);
            goodsStore1.setGoodsStock(goodsStore1.getGoodsStock()-item.getAmount());
            goodsStoreMapper.updateByPrimaryKeySelective(goodsStore1);
        }
        return orderNo;
    }

    public PageInfo<Orders> selByUser(Integer pageNum,OrdersForm ordersForm){
        PageHelper.startPage(pageNum,Define.ADMIN_PAGE_SIZE);
        PageInfo<Orders> pageInfo = new PageInfo<>(ordersMapper.selByUser(ordersForm));
        return pageInfo;
    }

}
