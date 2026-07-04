package com.zjxy.smart.service;

import com.zjxy.smart.mapper.OrderDetailMapper;
import com.zjxy.smart.model.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailService {

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    public List<OrderDetail> selByOrder(String orderNo){
        return orderDetailMapper.selByOrder(orderNo);
    }
}
