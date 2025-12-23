package com.tancong.core.service.impl;

import cn.hutool.core.util.StrUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.tancong.common.exception.CanShowException;
import com.tancong.core.config.CosConfig;
import com.tancong.core.service.CosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * ===================================
 * 腾讯云COS服务实现类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/08
 */
@Slf4j
@Service
public class CosServiceImpl implements CosService {

    @Autowired
    private COSClient cosClient;

    @Autowired
    private CosConfig cosConfig;

    @Override
    public String uploadFile(MultipartFile file, String objectKey) {
        try (InputStream inputStream = file.getInputStream()) {
            return uploadFile(inputStream, objectKey, file.getSize(), file.getContentType());
        } catch (Exception e) {
            log.error("COS文件上传失败: {}", e.getMessage(), e);
            throw new CanShowException("文件上传失败，请稍后重试");
        }
    }

    @Override
    public String uploadFile(InputStream inputStream, String objectKey, Long contentLength, String contentType) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);
            if (StrUtil.isNotBlank(contentType)) {
                metadata.setContentType(contentType);
            }

            PutObjectRequest putRequest = new PutObjectRequest(
                cosConfig.getBucketName(),
                objectKey,
                inputStream,
                metadata
            );

            cosClient.putObject(putRequest);

            log.info("文件上传成功: {}", objectKey);
            return getFileUrl(objectKey);
        } catch (CosClientException e) {
            log.error("COS文件上传失败: {}", e.getMessage(), e);
            throw new CanShowException("文件上传失败，请稍后重试");
        }
    }

    @Override
    public void deleteFile(String objectKey) {
        try {
            cosClient.deleteObject(cosConfig.getBucketName(), objectKey);
            log.info("文件删除成功: {}", objectKey);
        } catch (Exception e) {
            log.error("COS文件删除失败: {}", e.getMessage(), e);
            throw new CanShowException("文件删除失败");
        }
    }

    @Override
    public URL generatePresignedUrl(String objectKey, Long expirationSeconds) {
        try {
            Date expiration = new Date(System.currentTimeMillis() + expirationSeconds * 1000);
            return cosClient.generatePresignedUrl(cosConfig.getBucketName(), objectKey, expiration);
        } catch (Exception e) {
            log.error("生成签名URL失败: {}", e.getMessage(), e);
            throw new CanShowException("生成下载链接失败");
        }
    }

    @Override
    public boolean doesFileExist(String objectKey) {
        try {
            return cosClient.doesObjectExist(cosConfig.getBucketName(), objectKey);
        } catch (Exception e) {
            log.error("检查文件存在性失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getFileUrl(String objectKey) {
        // 如果配置了自定义域名，使用自定义域名
        if (StrUtil.isNotBlank(cosConfig.getCustomDomain())) {
            return "https://" + cosConfig.getCustomDomain() + "/" + objectKey;
        }

        // 否则使用默认COS域名
        return "https://" + cosConfig.getBucketName() + ".cos." + cosConfig.getRegion() + ".myqcloud.com/" + objectKey;
    }
}
