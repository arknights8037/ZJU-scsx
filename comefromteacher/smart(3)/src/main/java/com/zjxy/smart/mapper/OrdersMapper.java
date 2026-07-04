package com.zjxy.smart.mapper;

import com.zjxy.smart.form.OrdersForm;
import com.zjxy.smart.model.Orders;

import java.util.List;

public interface OrdersMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Orders record);

    int insertSelective(Orders record);

    Orders selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Orders record);

    int updateByPrimaryKey(Orders record);

    List<Orders> selByUser(OrdersForm ordersForm);
}