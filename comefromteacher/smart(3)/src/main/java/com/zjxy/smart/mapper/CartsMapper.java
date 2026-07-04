package com.zjxy.smart.mapper;

import com.zjxy.smart.model.Carts;

import java.util.List;

public interface CartsMapper {
/**
 * 根据主键删除记录
 * @param id 主键ID
 * @return 删除的记录数，通常为1表示成功删除，0表示未找到记录
 */
// 这是一个接口方法声明，用于根据主键删除数据库中的记录
// 方法名为deleteByPrimaryKey，表示通过主键进行删除操作
// 参数id为Integer类型，表示要删除记录的主键值
// 返回类型为int，表示删除的记录数量
    int deleteByPrimaryKey(Integer id);

    int insert(Carts record);

    int insertSelective(Carts record);

    Carts selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Carts record);

    int updateByPrimaryKey(Carts record);

    Carts selByGoods(Carts carts);

    List<Carts> selByUser(Integer userId);

    void delByUser(Integer userId);
}