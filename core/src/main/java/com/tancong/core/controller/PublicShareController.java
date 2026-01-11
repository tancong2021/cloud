package com.tancong.core.controller;

import com.tancong.common.annotation.API;
import com.tancong.common.entity.vo.RespBody;
import com.tancong.core.entity.dto.VerifyShareDTO;
import com.tancong.core.entity.vo.ShareDetailVO;
import com.tancong.core.service.ShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ===================================
 * 公共分享访问控制器（无需JWT认证）
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/25
 */
@Slf4j
@API(path = "/public/shares", name = "公共分享访问", description = "验证提取码、查看/下载分享文件")
public class PublicShareController {

    @Autowired
    private ShareService shareService;

    /**
     * 验证分享提取码
     */
    @PostMapping("/verify")
    @Operation(summary = "验证分享提取码")
    public RespBody<ShareDetailVO> verifyShare(
        @RequestBody @Valid VerifyShareDTO dto,
        HttpServletRequest request
    ) {
        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        ShareDetailVO detailVO = shareService.verifyAndGetShareDetail(
            dto.getShareCode(),
            dto.getExtractCode(),
            ipAddress,
            userAgent
        );
        return RespBody.success(detailVO);
    }

    /**
     * 获取分享基本信息（无需提取码，仅显示文件名等基本信息）
     */
    @GetMapping("/{shareCode}/info")
    @Operation(summary = "获取分享基本信息")
    public RespBody<ShareDetailVO> getShareInfo(
        @Parameter(description = "分享码") @PathVariable("shareCode") String shareCode
    ) {
        ShareDetailVO info = shareService.getShareBasicInfo(shareCode);
        return RespBody.success(info);
    }

    /**
     * 下载分享文件（需先通过验证）
     */
    @GetMapping("/{shareCode}/download")
    @Operation(summary = "下载分享文件")
    public RespBody<String> downloadSharedFile(
        @Parameter(description = "分享码") @PathVariable("shareCode") String shareCode,
        @Parameter(description = "文件ID（文件夹分享时指定子文件）")
        @RequestParam(required = false) Long fileId,
        HttpServletRequest request
    ) {
        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        String downloadUrl = shareService.getSharedFileDownloadUrl(
            shareCode,
            fileId,
            ipAddress,
            userAgent
        );
        return RespBody.success(downloadUrl);
    }

    /**
     * 获取客户端真实IP（处理代理情况）
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果有多个代理，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
