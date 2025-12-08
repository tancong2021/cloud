package com.tancong.core.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ===================================
 * 腾讯云COS配置类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/08
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "tencent.cos")
public class CosConfig {

    /**
     * 腾讯云 SecretId
     */
    private String secretId;

    /**
     * 腾讯云 SecretKey
     */
    private String secretKey;

    /**
     * 地域（如：ap-guangzhou, ap-shanghai, ap-beijing）
     */
    private String region;

    /**
     * 存储桶名称（格式：bucketname-appid）
     */
    private String bucketName;

    /**
     * 自定义域名（可选，如已绑定CDN域名）
     */
    private String customDomain;

    /**
     * 文件URL有效期（秒），默认3600秒（1小时）
     */
    private Long urlExpirationSeconds = 3600L;

    /**
     * 创建COS客户端Bean
     */
    @Bean
    public COSClient cosClient() {
        // 初始化用户身份信息（secretId, secretKey）
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);

        // 设置bucket的地域
        Region regionObj = new Region(region);
        ClientConfig clientConfig = new ClientConfig(regionObj);

        // 设置使用 HTTPS 协议
        clientConfig.setHttpProtocol(HttpProtocol.https);

        // 生成cos客户端
        return new COSClient(cred, clientConfig);
    }
}
