package com.zjxy.smart.control;

import com.github.pagehelper.PageInfo;
import com.zjxy.smart.common.Result;
import com.zjxy.smart.model.Store;
import com.zjxy.smart.service.StoreService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/store")
public class StoreController {

    @Resource
    private StoreService storeService;

    /**
     * 根据主键取数据
     * @param id
     * @return
     */
    @RequestMapping("selById/{id}")
    public Store selById(@PathVariable Integer id){
        return this.storeService.selById(id);
    }

    /**
     * 取列表数据
     * @return
     */
    @RequestMapping("list")
    public List<Store> list(@RequestBody Store store){
        return storeService.selAll(store);
    }

    /**
     * 分页查询
     */
    @RequestMapping("pageList/{pageNum}")
    public PageInfo<Store> pageList(@RequestBody Store store,@PathVariable int pageNum){
        return storeService.selPageAll(store,pageNum);
    }

    /**
     * 新增
     * @param store
     * @return
     */
    @RequestMapping("add")
    public Result add(@RequestBody Store store){
        storeService.add(store);
        Result result = new Result();
        result.setCode(200);
        return result;
    }

    /**
     * 修改
     * @param store
     * @return
     */
    @RequestMapping("update")
    public Result update(@RequestBody Store store){
        storeService.update(store);
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
        storeService.del(id);
        Result result = new Result();
        result.setCode(200);
        return result;
    }

    @RequestMapping("selByNo/{storeNo}")
    public Store selByNo(@PathVariable String storeNo){
        return storeService.selByNo(storeNo);
    }
}
