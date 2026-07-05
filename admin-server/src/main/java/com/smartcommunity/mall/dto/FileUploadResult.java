package com.smartcommunity.mall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** 文件上传成功后返回给前端的信息。 */
@Data
@AllArgsConstructor
public class FileUploadResult {

    private String originalName; // 文件原始名称
    private String url; // 文件访问URL
    private long size; // 文件大小（字节）
}
