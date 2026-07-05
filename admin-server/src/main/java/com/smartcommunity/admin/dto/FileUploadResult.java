package com.smartcommunity.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** 文件上传结果，包含文件原始名称、访问URL和大小。 */
@Data
@AllArgsConstructor
public class FileUploadResult {

    private String originalName; // 文件原始名称
    private String url; // 文件访问URL
    private long size; // 文件大小（字节）
}
