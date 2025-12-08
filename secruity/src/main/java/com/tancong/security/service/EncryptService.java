package com.tancong.security.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * ===================================
 * 加密解密服务
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/30
 */
@Slf4j
@Component
public class EncryptService {

    // ✅ 正确：非 static 字段
    @Value("${encrypt.aes.key}")
    private String aesKeyString;

    // ✅ static 字段在 @PostConstruct 中赋值
    private static byte[] KEY;

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 初始化密钥
     */
    @PostConstruct
    public void init() {
        if (aesKeyString == null || aesKeyString.length() != 16) {
            throw new IllegalArgumentException("AES 密钥必须是 16 字节（128位）");
        }

        // ✅ 将配置的字符串转为字节数组
        KEY = aesKeyString.getBytes(StandardCharsets.UTF_8);

        log.info("EncryptService 初始化完成，AES 密钥长度：{} 字节", KEY.length);
    }

    /**
     * 加密：返回 Base64( IV + Cipher )
     */
    public static String encrypt(String raw) {
        if (raw == null || raw.isEmpty()) {
            throw new IllegalArgumentException("待加密内容不能为空");
        }

        // 生成随机 IV
        byte[] IV = new byte[16];
        RANDOM.nextBytes(IV);

        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, KEY, IV);

        // 加密得到字节数组
        byte[] cipher = aes.encrypt(raw);

        // 输出：IV + 密文，最后整体 Base64
        byte[] output = new byte[IV.length + cipher.length];
        System.arraycopy(IV, 0, output, 0, IV.length);
        System.arraycopy(cipher, 0, output, IV.length, cipher.length);

        return Base64.encode(output);
    }

    /**
     * 解密：输入 Base64(IV + Cipher)
     */
    public static String decrypt(String base64) {
        if (base64 == null || base64.isEmpty()) {
            throw new IllegalArgumentException("待解密内容不能为空");
        }

        byte[] input = Base64.decode(base64);

        if (input.length < 16) {
            throw new IllegalArgumentException("加密数据格式错误");
        }

        // 前 16个字节是 IV
        byte[] IV = Arrays.copyOfRange(input, 0, 16);

        // 余下内容是密文
        byte[] cipher = Arrays.copyOfRange(input, 16, input.length);

        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, KEY, IV);

        return aes.decryptStr(cipher, CharsetUtil.CHARSET_UTF_8);
    }
}
