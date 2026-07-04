package com.zjxy.smart.control;

import com.zjxy.smart.model.OrderDetail;
import com.zjxy.smart.service.OrderDetailService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {

    @Resource
    private OrderDetailService orderDetailService;

    @RequestMapping("selByOrder/{orderNo}")
    public List<OrderDetail> selByOrder(@PathVariable String orderNo){
        return orderDetailService.selByOrder(orderNo);
    }
}
