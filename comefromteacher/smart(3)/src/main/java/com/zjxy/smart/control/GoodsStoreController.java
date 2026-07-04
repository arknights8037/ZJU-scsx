package com.zjxy.smart.control;

import com.github.pagehelper.PageInfo;
import com.zjxy.smart.common.Result;
import com.zjxy.smart.form.GoodsStoreForm;
import com.zjxy.smart.model.GoodsStore;
import com.zjxy.smart.model.Store;
import com.zjxy.smart.service.GoodsStoreService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goodsStore")
public class GoodsStoreController {

    @Resource
    private GoodsStoreService goodsStoreService;


    @RequestMapping("add")
    public Result add(@RequestBody GoodsStore goodsStore){
        goodsStoreService.add(goodsStore);
        Result result = new Result();
        result.setCode(200);
        return result;
    }

    @RequestMapping("selByStore/{pageNum}")
    public PageInfo<GoodsStore> selByStore(@PathVariable int pageNum, @RequestBody GoodsStoreForm goodsStoreForm){
        return  goodsStoreService.selByStore(pageNum,goodsStoreForm);
    }


}
