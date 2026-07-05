package com.smartcommunity.mall.common;

import lombok.Data;

import java.util.List;

/**
 * 分页响应结果封装类。
 * <p>
 * 用于统一封装分页查询的返回结果，包含总记录数、当前页码、
 * 每页大小和当前页数据列表。
 *
 * @param <T> 记录类型
 */
@Data
public class PageResult<T> {

    /** 总记录数 */
    private Long total;
    /** 当前页码 */
    private Long page;
    /** 每页大小 */
    private Long size;
    /** 当前页数据列表 */
    private List<T> records;

    /**
     * 静态工厂方法，构造分页结果。
     *
     * @param total   总记录数
     * @param page    当前页码
     * @param size    每页大小
     * @param records 当前页数据
     * @param <T>     记录类型
     * @return PageResult 实例
     */
    public static <T> PageResult<T> of(Long total, Long page, Long size, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);
        result.setRecords(records);
        return result;
    }
}
