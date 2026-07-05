package com.smartcommunity.admin.service;

import com.smartcommunity.admin.dto.FileUploadResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 管理端商品图片本地存储服务。
 *
 * 图片文件保存到本地磁盘 uploads 目录，数据库只保存访问 URL 路径。
 * 文件名由后端使用 UUID 重新生成，避免中文名、重名和路径穿越问题。
 */
@Service
public class LocalFileService {

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");

    private final Path uploadRoot;

    public LocalFileService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    /**
     * 上传商品图片，校验后保存到按日期分组的目录中。
     *
     * @param file 上传的图片文件
     * @return 文件上传结果（原始文件名、访问 URL、文件大小）
     */
    public FileUploadResult uploadImage(MultipartFile file) {
        validateImage(file);

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getExtension(originalName);
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String storedName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        Path targetDirectory = uploadRoot.resolve(datePath).normalize();
        Path targetFile = targetDirectory.resolve(storedName).normalize();

        if (!targetFile.startsWith(uploadRoot)) {
            throw new RuntimeException("文件保存路径不合法");
        }

        try {
            Files.createDirectories(targetDirectory);
            file.transferTo(targetFile);
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败", e);
        }

        String url = "/admin-uploads/" + datePath + "/" + storedName;
        return new FileUploadResult(originalName, url, file.getSize());
    }

    /**
     * 校验上传图片：非空、大小不超过 5MB、扩展名和 MIME 类型符合图片规范。
     *
     * @param file 待校验的图片文件
     */
    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请选择图片");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new RuntimeException("图片不能超过5MB");
        }
        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getExtension(originalName);
        if (!IMAGE_EXTENSIONS.contains(extension)) {
            throw new RuntimeException("仅支持 jpg、png、webp、gif 图片");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new RuntimeException("上传文件不是图片");
        }
    }

    /**
     * 从文件名中提取小写扩展名。
     *
     * @param filename 原始文件名
     * @return 小写扩展名字符串（不含点号）
     */
    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            return "";
        }
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
