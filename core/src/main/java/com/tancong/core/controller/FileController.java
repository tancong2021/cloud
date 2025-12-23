package com.tancong.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tancong.common.annotation.API;
import com.tancong.common.annotation.LogRecord;
import com.tancong.common.entity.vo.RespBody;
import com.tancong.core.entity.File;
import com.tancong.core.entity.LoginUser;
import com.tancong.core.entity.vo.FileUploadResponse;
import com.tancong.core.entity.vo.FileVO;
import com.tancong.core.entity.vo.Pager;
import com.tancong.core.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * ===================================
 * 文件管理控制器
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/08
 */
@Slf4j
@API(path = "/files", name = "文件管理", description = "文件上传、下载、删除等接口")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    @LogRecord(value = "上传文件", module = "文件管理")
    public RespBody<FileUploadResponse> uploadFile(
        @Parameter(description = "文件") @RequestParam("file") MultipartFile file,
        @Parameter(description = "文件MD5值（可选，用于秒传）") @RequestParam(value = "md5", required = false) String md5,
        @Parameter(description = "文件夹ID（可选）") @RequestParam(value = "folderId", required = false) Long folderId,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        FileUploadResponse response = fileService.uploadFile(file, loginUser.getId(), md5, folderId);
        return RespBody.success(response);
    }

    /**
     * 检查文件是否可以秒传
     */
    @GetMapping("/check-quick-upload")
    @Operation(summary = "检查文件是否可以秒传")
    public RespBody<Boolean> checkQuickUpload(
        @Parameter(description = "文件MD5值") @RequestParam("md5") String md5,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        File file = fileService.checkQuickUpload(md5, loginUser.getId());
        return RespBody.success(file != null);
    }

    /**
     * 创建文件夹
     */
    @PostMapping("/folders")
    @Operation(summary = "创建文件夹")
    @LogRecord(value = "创建文件夹", module = "文件管理")
    public RespBody<FileVO> createFolder(
            @Parameter(description = "文件夹名称")
            @RequestParam("folderName") String folderName,
            @Parameter(description = "父文件夹ID（可选，默认根目录）")
            @RequestParam(value = "parentFolderId", required = false) Long
                    parentFolderId,
            @AuthenticationPrincipal LoginUser loginUser
    ) {
        FileVO vo = fileService.createFolder(folderName, loginUser.getId(), parentFolderId);
        return RespBody.success(vo);
    }

    /**
     * 获取文件夹树形结构
     */
    @GetMapping("/folders/{id}/tree")
    @Operation(summary = "获取文件夹树形结构")
    public RespBody<List<FileVO>> getFolderTree(
            @Parameter(description = "文件夹ID") @PathVariable("id") Long
                    folderId,
            @AuthenticationPrincipal LoginUser loginUser
    ) {
        List<FileVO> tree = fileService.getFileTree(folderId,
                loginUser.getId());
        return RespBody.success(tree);
    }

    /**
     * 重命名文件或文件夹
     */
    @PutMapping("/{id}/rename")
    @Operation(summary = "重命名文件或文件夹")
    @LogRecord(value = "重命名", module = "文件管理")
    public RespBody<Boolean> renameFile(
            @Parameter(description = "文件/文件夹ID") @PathVariable("id")
            Long fileId,
            @Parameter(description = "新名称") @RequestParam("newName")
            @Pattern(regexp = "^[^/\\\\:*?\"<>|]+$", message = "文件名包含非法字符")
            String newName,
            @AuthenticationPrincipal LoginUser loginUser
    ) {
        boolean result = fileService.renameFile(fileId, loginUser.getId(),newName);
        if (result) {
            return RespBody.success(true);
        }
        return RespBody.success(false);
    }

    /**
     * 移动文件或文件夹
     */
    @PutMapping("/{id}/move")
    @Operation(summary = "移动文件或文件夹")
    @LogRecord(value = "移动", module = "文件管理")
    public RespBody<Boolean> moveFile(
            @Parameter(description = "文件/文件夹ID") @PathVariable("id")
            Long fileId,
            @Parameter(description = "目标文件夹ID")
            @RequestParam("targetFolderId") Long targetFolderId,
            @AuthenticationPrincipal LoginUser loginUser
    ) {
        boolean success = fileService.moveFile(fileId, targetFolderId,
                loginUser.getId());
        return RespBody.success(success);
    }


    /**
     * 获取文件列表（分页）
     */
    @GetMapping("/list")
    @Operation(summary = "获取文件列表")
    public RespBody<Pager<FileVO>> getFileList(
            @Parameter(description = "文件夹ID（可选）")
            @RequestParam(value = "folderId", required = false) Long folderId,
            Pager<FileVO> pager,
            @AuthenticationPrincipal LoginUser loginUser
    ) {
        // ✅ Controller 只负责调用 Service
        Pager<FileVO> result = fileService.getUserFilesWithPage(
                loginUser.getId(), folderId, pager
        );
        return RespBody.success(result);
    }

    /**
     * 获取文件详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取文件详情")
    public RespBody<FileVO> getFileDetail(
        @Parameter(description = "文件ID") @PathVariable("id") Long id,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        FileVO fileVO = fileService.getFileDetail(id, loginUser.getId());
        return RespBody.success(fileVO);
    }

    /**
     * 获取文件下载URL
     */
    @GetMapping("/{id}/download")
    @Operation(summary = "获取文件下载URL")
    @LogRecord(value = "下载文件", module = "文件管理")
    public RespBody<String> getDownloadUrl(
        @Parameter(description = "文件ID") @PathVariable("id") Long id,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        String downloadUrl = fileService.getDownloadUrl(id, loginUser.getId());
        return RespBody.success(downloadUrl);
    }

    /**
     * 删除文件（软删除）
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除文件")
    @LogRecord(value = "删除文件", module = "文件管理")
    public RespBody<Boolean> deleteFile(
        @Parameter(description = "文件ID") @PathVariable("id") Long id,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        boolean success = fileService.deleteFile(id, loginUser.getId());
        return RespBody.success(success);
    }

    /**
     * 彻底删除文件
     */
    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "彻底删除文件")
    @LogRecord(value = "彻底删除文件", module = "文件管理")
    public RespBody<Boolean> permanentDeleteFile(
        @Parameter(description = "文件ID") @PathVariable("id") Long id,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        boolean success = fileService.permanentDeleteFile(id, loginUser.getId());
        return RespBody.success(success);
    }

    /**
     * 批量删除文件
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除文件")
    @LogRecord(value = "批量删除文件", module = "文件管理")
    public RespBody<Boolean> batchDeleteFiles(
        @Parameter(description = "文件ID列表") @RequestBody List<Long> fileIds,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        boolean success = fileService.batchDeleteFiles(fileIds, loginUser.getId());
        return RespBody.success(success);
    }
}
