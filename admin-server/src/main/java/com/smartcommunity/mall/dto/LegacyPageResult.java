package com.smartcommunity.mall.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/** 兼容 PageHelper PageInfo 的分页结果封装。 */
@Data
public class LegacyPageResult<T> {

    private Long total; // 数据总数
    private Long pageNum; // 当前页码
    private Long pageSize; // 每页条数
    private Integer size; // 当前页实际条数
    private Long pages; // 总页数
    private List<T> list; // 数据列表

    /**
     * 从 MyBatis-Plus Page 对象转换为 LegacyPageResult。
     *
     * @param page MyBatis-Plus 分页对象
     * @param <T>  数据类型
     * @return 转换后的 LegacyPageResult
     */
    public static <T> LegacyPageResult<T> of(Page<T> page) {
        LegacyPageResult<T> result = new LegacyPageResult<>();
        result.setTotal(page.getTotal());
        result.setPageNum(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setSize(page.getRecords().size());
        result.setPages(page.getPages());
        result.setList(page.getRecords());
        return result;
    }
}
