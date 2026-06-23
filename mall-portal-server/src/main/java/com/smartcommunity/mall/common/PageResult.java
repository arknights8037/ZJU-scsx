package com.smartcommunity.mall.common;

import lombok.Data;

import java.util.List;

/**
 * 分页响应
 */
@Data
public class PageResult<T> {

    private Long total;
    private Long page;
    private Long size;
    private List<T> records;

    public static <T> PageResult<T> of(Long total, Long page, Long size, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);
        result.setRecords(records);
        return result;
    }
}
