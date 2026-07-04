package com.zjxy.smart.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjxy.smart.common.Define;
import com.zjxy.smart.form.GoodsStoreForm;
import com.zjxy.smart.mapper.GoodsStoreMapper;
import com.zjxy.smart.model.GoodsStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GoodsStoreService {

    @Autowired
    private GoodsStoreMapper goodsStoreMapper;

    public void add(GoodsStore goodsStore){
        //根据店铺编号和商品编号查询有没有记录，如果没有，就新增，如果用就更新价格和库存
       GoodsStore goodsStore1 = goodsStoreMapper.selByNo(goodsStore);
       if(goodsStore1==null){//没有添加过此商品，新增
           goodsStore.setCreateTime(new Date());
           goodsStoreMapper.insert(goodsStore);
       }else{//店铺添加过此商品,修改价格和库存
           goodsStore1.setGoodsPrice(goodsStore.getGoodsPrice()); //更新价格
           goodsStore1.setGoodsStock(goodsStore1.getGoodsStock()+goodsStore.getGoodsStock()); //更新库存
           goodsStoreMapper.updateByPrimaryKey(goodsStore1);
       }
    }

    public PageInfo<GoodsStore> selByStore(int pageNum, GoodsStoreForm goodsStoreForm){
        PageHelper.startPage(pageNum, Define.STORE_GOODS_PAGE_SIZE);
        PageInfo<GoodsStore> pageInfo = new PageInfo<>(goodsStoreMapper.selByStore(goodsStoreForm));
        return pageInfo;
    }

    public PageInfo<GoodsStore> selAll(int pageNum, GoodsStoreForm goodsStoreForm){
        PageHelper.startPage(pageNum, Define.INDEX_GOODS_PAGE_SIZE);
        PageInfo<GoodsStore> pageInfo = new PageInfo<>(goodsStoreMapper.selAll(goodsStoreForm));
        return pageInfo;
    }

}
