package com.zjxy.smart.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjxy.smart.common.Define;
import com.zjxy.smart.mapper.GoodsMapper;
import com.zjxy.smart.model.Goods;
import com.zjxy.smart.model.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 分页查询
     * @param goods  查询条件
     * @param pageNum 需要查询的页码
     * @return
     */
    public PageInfo<Goods> selPageAll(Goods goods, int pageNum){
        //设置分页数据：第几页，每页多少条
        PageHelper.startPage(pageNum, Define.ADMIN_PAGE_SIZE);
        PageInfo<Goods> pageInfo = new PageInfo<>(goodsMapper.selAll(goods));
        return pageInfo;
    }

    public void add(Goods goods){
        //商品创建时间
        goods.setCreateTime(new Date());
        //商品编号
        goods.setGoodsNo(UUID.randomUUID().toString().substring(0,32));
        goodsMapper.insert(goods);
    }

    public void update(Goods goods){
        goodsMapper.updateByPrimaryKeySelective(goods);
    }

    public void delete(int id){
        goodsMapper.deleteByPrimaryKey(id);
    }

    public Goods selectById(int id){
        return goodsMapper.selectByPrimaryKey(id);
    }

}
