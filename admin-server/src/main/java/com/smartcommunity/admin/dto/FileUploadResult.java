package com.smartcommunity.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUploadResult {

    private String originalName;
    private String url;
    private long size;
}
