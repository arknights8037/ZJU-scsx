package com.smartcommunity.mall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 文件上传成功后返回给前端的信息。
 */
@Data
@AllArgsConstructor
public class FileUploadResult {

    private String originalName;
    private String url;
    private long size;
}
