package com.smartcommunity.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcommunity.mall.dto.GoodsClickStat;
import com.smartcommunity.mall.entity.GoodsClickRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GoodsClickRecordMapper extends BaseMapper<GoodsClickRecord> {

    /**
     * MySQL 的 ON DUPLICATE KEY UPDATE 可以在一条 SQL 中完成新增或累加。
     * 即使用户快速连续点击，也不会出现“先查再改”造成的计数覆盖。
     */
    @Insert("""
        INSERT INTO goods_click_record
            (user_id, goods_no, click_count, last_click_time, create_time, update_time)
        VALUES
            (#{userId}, #{goodsNo}, 1, NOW(), NOW(), NOW())
        ON DUPLICATE KEY UPDATE
            click_count = click_count + 1,
            last_click_time = NOW(),
            update_time = NOW()
        """)
    int increaseClick(@Param("userId") Integer userId, @Param("goodsNo") String goodsNo);

    @Select("""
        SELECT goods_no AS goodsNo, SUM(click_count) AS clickCount
        FROM goods_click_record
        GROUP BY goods_no
        """)
    List<GoodsClickStat> sumClicksByGoods();
}
