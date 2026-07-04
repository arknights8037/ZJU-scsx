package com.zjxy.smart.control;

import com.github.pagehelper.PageInfo;
import com.zjxy.smart.common.Result;
import com.zjxy.smart.form.OrdersForm;
import com.zjxy.smart.model.Orders;
import com.zjxy.smart.service.OrdersService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Resource
    private OrdersService ordersService;

    @RequestMapping("add")
    public Result add(@RequestBody OrdersForm ordersForm) {
        Result result = ordersService.checkMount(ordersForm);
        if(result.getCode()==200){
            String orderNo = ordersService.add(ordersForm);
            result.setMsg(orderNo);
        }
        return result;
    }

    @RequestMapping("selByUser/{pageNum}")
    public PageInfo<Orders> selByUser(@PathVariable Integer pageNum, @RequestBody OrdersForm ordersForm){
        return  ordersService.selByUser(pageNum,ordersForm);
    }
}
