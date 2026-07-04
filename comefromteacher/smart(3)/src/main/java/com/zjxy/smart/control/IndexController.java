package com.zjxy.smart.control;


import com.github.pagehelper.PageInfo;
import com.zjxy.smart.form.GoodsStoreForm;
import com.zjxy.smart.mapper.GoodsStoreMapper;
import com.zjxy.smart.model.GoodsStore;
import com.zjxy.smart.service.GoodsStoreService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
public class IndexController {

    @Resource
    private GoodsStoreService goodsStoreService;

    @RequestMapping("list/{pageNum}")
    public PageInfo<GoodsStore> list(@PathVariable int pageNum, @RequestBody GoodsStoreForm goodsStoreForm){
        return goodsStoreService.selAll(pageNum,goodsStoreForm);
    }
}
