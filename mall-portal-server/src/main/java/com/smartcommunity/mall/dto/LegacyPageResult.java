package com.smartcommunity.mall.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * 兼容 PageHelper PageInfo 的常用字段。
 */
@Data
public class LegacyPageResult<T> {

    private Long total;
    private Long pageNum;
    private Long pageSize;
    private Integer size;
    private Long pages;
    private List<T> list;

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
