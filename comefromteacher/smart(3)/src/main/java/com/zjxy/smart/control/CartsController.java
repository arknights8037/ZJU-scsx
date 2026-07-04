package com.zjxy.smart.control;

import com.zjxy.smart.common.Result;
import com.zjxy.smart.model.Carts;
import com.zjxy.smart.service.CartsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartsController {

    @Resource
    private CartsService cartsService;

    @RequestMapping("list/{userId}")
    public List<Carts> list(@PathVariable Integer userId) {
        return cartsService.selByUser(userId);
    }

    @RequestMapping("add")
    public Result add(@RequestBody Carts carts){
        cartsService.add(carts);
        Result result=new Result();
        result.setCode(200);
        return result;
    }

    @RequestMapping("update")
    public Result update(@RequestBody Carts carts){
        cartsService.update(carts);
        Result result=new Result();
        result.setCode(200);
        return result;
    }

    @RequestMapping("delByUser/{userId}")
    public Result delByUser(@PathVariable Integer userId){
        cartsService.delByUser(userId);
        Result result=new Result();
        result.setCode(200);
        return result;
    }
}
