package com.zjxy.smart.control;

import com.github.pagehelper.PageInfo;
import com.zjxy.smart.common.Result;
import com.zjxy.smart.model.Goods;
import com.zjxy.smart.model.Store;
import com.zjxy.smart.service.GoodsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;


    @RequestMapping("selById/{id}")
    public Goods selById(@PathVariable Integer id){
        return goodsService.selectById(id);
    }

    /**
     * 分页查询
     */
    @RequestMapping("pageList/{pageNum}")
    public PageInfo<Goods> pageList(@RequestBody Goods goods, @PathVariable int pageNum){
        return goodsService.selPageAll(goods,pageNum);
    }

    /**
     * 新增
     * @param goods
     * @return
     */
    @RequestMapping("add")
    public Result add(@RequestBody Goods goods){
        goodsService.add(goods);
        Result result = new Result();
        result.setCode(200);
        return result;
    }

    /**
     * 修改
     * @param goods
     * @return
     */
    @RequestMapping("update")
    public Result update(@RequestBody Goods goods){
        goodsService.update(goods);
        Result result = new Result();
        result.setCode(200);
        return result;
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping("del/{id}")
    public Result delete(@PathVariable Integer id){
        goodsService.delete(id);
        Result result = new Result();
        result.setCode(200);
        return result;
    }

}
