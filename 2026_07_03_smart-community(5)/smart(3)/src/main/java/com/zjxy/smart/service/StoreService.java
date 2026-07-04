package com.zjxy.smart.service;


import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjxy.smart.common.Define;
import com.zjxy.smart.mapper.StoreMapper;
import com.zjxy.smart.model.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class StoreService {

    @Autowired
    private StoreMapper storeMapper;

/**
 * 根据ID查询商店信息
 * @param id 商店ID，用于查询指定商店
 * @return 返回对应的Store对象，如果未找到则返回null
 */
    public Store selById(Integer id){
    // 调用storeMapper的selectByPrimaryKey方法，根据id查询商店信息
        return storeMapper.selectByPrimaryKey(id);
    }

    public List<Store> selAll(Store store){
        return storeMapper.selAll(store);
    }

    /**
     * 分页查询
     * @param store  查询条件
     * @param pageNum 需要查询的页码
     * @return
     */
    public PageInfo<Store> selPageAll(Store store,int pageNum){
        //设置分页数据：第几页，每页多少条
        PageHelper.startPage(pageNum, Define.ADMIN_PAGE_SIZE);
        PageInfo<Store> pageInfo = new PageInfo<>(storeMapper.selAll(store));
        return pageInfo;
    }

    /**
     * 新增
     * @param store
     */
    public void add(Store store){
        //店铺创建时间
        store.setCreateTime(new Date());
        //店铺编号
        store.setStoreNo(UUID.randomUUID().toString().substring(0,32));
        storeMapper.insert(store);
    }

    public void del(Integer id){
        storeMapper.deleteByPrimaryKey(id);
    }

    public void update(Store store){
        storeMapper.updateByPrimaryKeySelective(store);
    }

    public Store selByNo(String storeNo){
        return storeMapper.selByNo(storeNo);
    }
}
