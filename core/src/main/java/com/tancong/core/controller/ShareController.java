package com.tancong.core.controller;

import com.tancong.common.annotation.API;
import com.tancong.common.annotation.LogRecord;
import com.tancong.common.entity.vo.RespBody;
import com.tancong.core.entity.LoginUser;
import com.tancong.core.entity.dto.CreateShareDTO;
import com.tancong.core.entity.dto.UpdateShareDTO;
import com.tancong.core.entity.vo.Pager;
import com.tancong.core.entity.vo.ShareAccessLogVO;
import com.tancong.core.entity.vo.ShareVO;
import com.tancong.core.service.ShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ===================================
 * 文件分享管理控制器（需要JWT认证）
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/25
 */
@Slf4j
@API(path = "/shares", name = "文件分享管理", description = "创建、查看、修改、取消分享")
public class ShareController {

    @Autowired
    private ShareService shareService;

    /**
     * 创建文件分享
     */
    @PostMapping
    @Operation(summary = "创建文件分享")
    @LogRecord(value = "创建文件分享", module = "分享管理")
    public RespBody<ShareVO> createShare(
        @RequestBody @Valid CreateShareDTO dto,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        ShareVO shareVO = shareService.createShare(dto, loginUser.getId());
        return RespBody.success(shareVO);
    }

    /**
     * 获取我的分享列表（分页）
     */
    @GetMapping("/my-shares")
    @Operation(summary = "获取我的分享列表")
    public RespBody<Pager<ShareVO>> getMyShares(
        Pager<ShareVO> pager,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        Pager<ShareVO> result = shareService.getMyShares(loginUser.getId(), pager);
        return RespBody.success(result);
    }

    /**
     * 获取分享详情（仅限分享创建者）
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取分享详情")
    public RespBody<ShareVO> getShareDetail(
        @Parameter(description = "分享ID") @PathVariable("id") Long shareId,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        ShareVO shareVO = shareService.getShareDetail(shareId, loginUser.getId());
        return RespBody.success(shareVO);
    }

    /**
     * 修改分享设置（提取码、有效期）
     */
    @PutMapping
    @Operation(summary = "修改分享设置")
    @LogRecord(value = "修改分享设置", module = "分享管理")
    public RespBody<ShareVO> updateShare(
        @RequestBody @Valid UpdateShareDTO dto,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        ShareVO shareVO = shareService.updateShare(dto, loginUser.getId());
        return RespBody.success(shareVO);
    }

    /**
     * 取消分享
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "取消分享")
    @LogRecord(value = "取消分享", module = "分享管理")
    public RespBody<Boolean> cancelShare(
        @Parameter(description = "分享ID") @PathVariable("id") Long shareId,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        boolean success = shareService.cancelShare(shareId, loginUser.getId());
        return RespBody.success(success);
    }

    /**
     * 批量取消分享
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量取消分享")
    @LogRecord(value = "批量取消分享", module = "分享管理")
    public RespBody<Boolean> batchCancelShares(
        @RequestBody List<Long> shareIds,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        boolean success = shareService.batchCancelShares(shareIds, loginUser.getId());
        return RespBody.success(success);
    }

    /**
     * 获取分享访问日志
     */
    @GetMapping("/{id}/logs")
    @Operation(summary = "获取分享访问日志")
    public RespBody<Pager<ShareAccessLogVO>> getShareLogs(
        @Parameter(description = "分享ID") @PathVariable("id") Long shareId,
        Pager<ShareAccessLogVO> pager,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        Pager<ShareAccessLogVO> result = shareService.getShareAccessLogs(shareId, loginUser.getId(), pager);
        return RespBody.success(result);
    }
}
