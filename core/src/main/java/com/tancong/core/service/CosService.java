package com.tancong.core.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;

/**
 * ===================================
 * 腾讯云COS服务接口
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/08
 */
public interface CosService {

    /**
     * 上传文件到COS
     *
     * @param file 文件对象
     * @param objectKey COS对象key（文件路径）
     * @return COS文件URL
     */
    String uploadFile(MultipartFile file, String objectKey);

    /**
     * 上传文件流到COS
     *
     * @param inputStream 文件输入流
     * @param objectKey COS对象key
     * @param contentLength 文件大小
     * @param contentType 文件类型
     * @return COS文件URL
     */
    String uploadFile(InputStream inputStream, String objectKey, Long contentLength, String contentType);

    /**
     * 删除COS文件
     *
     * @param objectKey COS对象key
     */
    void deleteFile(String objectKey);

    /**
     * 生成文件下载签名URL（临时访问URL）
     *
     * @param objectKey COS对象key
     * @param expirationSeconds 有效期（秒）
     * @return 签名URL
     */
    URL generatePresignedUrl(String objectKey, Long expirationSeconds);

    /**
     * 检查文件是否存在
     *
     * @param objectKey COS对象key
     * @return 是否存在
     */
    boolean doesFileExist(String objectKey);

    /**
     * 获取文件访问URL（永久URL，需要bucket公共读）
     *
     * @param objectKey COS对象key
     * @return 文件URL
     */
    String getFileUrl(String objectKey);
}
