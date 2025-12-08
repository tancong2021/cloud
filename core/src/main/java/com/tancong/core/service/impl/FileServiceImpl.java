package com.tancong.core.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tancong.common.exception.CanShowException;
import com.tancong.core.config.CosConfig;
import com.tancong.core.config.FileUploadConfig;
import com.tancong.core.entity.File;
import com.tancong.core.entity.enums.FileStatusEnum;
import com.tancong.core.entity.vo.FileUploadResponse;
import com.tancong.core.entity.vo.FileVO;
import com.tancong.core.mapper.FileMapper;
import com.tancong.core.service.CosService;
import com.tancong.core.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ===================================
 * 文件业务服务实现类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/08
 */
@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private CosService cosService;

    @Autowired
    private FileUploadConfig uploadConfig;

    @Autowired
    private CosConfig cosConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUploadResponse uploadFile(MultipartFile file, Long userId, String md5, Long folderId) {
        // 1. 参数校验
        validateUploadFile(file);

        // 2. 检查是否可以秒传
        if (StrUtil.isNotBlank(md5)) {
            File existingFile = checkQuickUpload(md5, userId);
            if (existingFile != null) {
                return createQuickUploadResponse(existingFile);
            }
        }

        // 3. 生成文件存储路径
        String uuid = IdUtil.simpleUUID();
        String objectKey = generateObjectKey(file.getOriginalFilename(), uuid);

        // 4. 上传到COS
        String fileUrl = cosService.uploadFile(file, objectKey);

        // 5. 保存文件记录到数据库
        File fileEntity = new File()
            .setUuid(uuid)
            .setFileName(file.getOriginalFilename())
            .setFileSize(file.getSize())
            .setFileType(file.getContentType())
            .setFileExtension(getFileExtension(file.getOriginalFilename()))
            .setStoragePath(objectKey)
            .setStorageBucket(cosConfig.getBucketName())
            .setUserId(userId)
            .setFolderId(folderId == null ? 0L : folderId)
            .setMd5(md5)
            .setDownloadCount(0)
            .setStatus(FileStatusEnum.NORMAL);

        fileMapper.insert(fileEntity);

        log.info("文件上传成功: fileId={}, userId={}, fileName={}",
            fileEntity.getId(), userId, file.getOriginalFilename());

        // 6. 返回响应
        return new FileUploadResponse(
            fileEntity.getId(),
            uuid,
            fileEntity.getFileName(),
            fileEntity.getFileSize(),
            fileUrl,
            false,
            "文件上传成功"
        );
    }

    @Override
    public File checkQuickUpload(String md5, Long userId) {
        if (StrUtil.isBlank(md5)) {
            return null;
        }

        // 先查询当前用户是否已上传过相同文件
        File userFile = fileMapper.selectByMd5AndUserId(md5, userId);
        if (userFile != null) {
            return userFile;
        }

        // 再查询其他用户是否上传过（跨用户秒传）
        File existingFile = fileMapper.selectByMd5(md5);
        if (existingFile != null) {
            // 复制文件记录到当前用户
            File newFile = new File();
            BeanUtils.copyProperties(existingFile, newFile);
            newFile.setId(null);
            newFile.setUuid(IdUtil.simpleUUID());
            newFile.setUserId(userId);
            newFile.setDownloadCount(0);
            fileMapper.insert(newFile);

            log.info("文件秒传成功: userId={}, md5={}", userId, md5);
            return newFile;
        }

        return null;
    }

    @Override
    public String getDownloadUrl(Long fileId, Long userId) {
        // 1. 查询文件
        File file = fileMapper.selectById(fileId);
        if (file == null) {
            throw new CanShowException("文件不存在");
        }

        // 2. 权限校验
        if (!file.getUserId().equals(userId)) {
            throw new CanShowException("无权访问该文件");
        }

        // 3. 状态校验
        if (file.getStatus() != FileStatusEnum.NORMAL) {
            throw new CanShowException("文件已被删除或不可用");
        }

        // 4. 增加下载次数
        fileMapper.incrementDownloadCount(fileId);

        // 5. 生成签名URL
        return cosService.generatePresignedUrl(
            file.getStoragePath(),
            cosConfig.getUrlExpirationSeconds()
        ).toString();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFile(Long fileId, Long userId) {
        // 软删除（移到回收站）
        int rows = fileMapper.softDeleteFile(fileId, userId);
        if (rows > 0) {
            log.info("文件软删除成功: fileId={}, userId={}", fileId, userId);
            return true;
        }
        throw new CanShowException("删除失败，文件不存在或无权限");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean permanentDeleteFile(Long fileId, Long userId) {
        // 1. 查询文件
        File file = fileMapper.selectById(fileId);
        if (file == null || !file.getUserId().equals(userId)) {
            throw new CanShowException("文件不存在或无权限");
        }

        // 2. 删除COS文件
        try {
            cosService.deleteFile(file.getStoragePath());
        } catch (Exception e) {
            log.error("删除COS文件失败: {}", e.getMessage(), e);
            // 继续删除数据库记录
        }

        // 3. 删除数据库记录
        int rows = fileMapper.deleteById(fileId);
        if (rows > 0) {
            log.info("文件彻底删除成功: fileId={}, userId={}", fileId, userId);
            return true;
        }

        return false;
    }

    @Override
    public List<FileVO> getUserFiles(Long userId, Long folderId) {
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("folder_id", folderId == null ? 0 : folderId)
               .eq("status", FileStatusEnum.NORMAL.getValue())
               .orderByDesc("create_time");

        List<File> files = fileMapper.selectList(wrapper);

        return files.stream().map(this::convertToFileVO).collect(Collectors.toList());
    }

    @Override
    public FileVO getFileDetail(Long fileId, Long userId) {
        File file = fileMapper.selectById(fileId);
        if (file == null) {
            throw new CanShowException("文件不存在");
        }

        if (!file.getUserId().equals(userId)) {
            throw new CanShowException("无权访问该文件");
        }

        return convertToFileVO(file);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 校验上传文件
     */
    private void validateUploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CanShowException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > uploadConfig.getMaxFileSize()) {
            throw new CanShowException("文件大小超过限制（最大" +
                formatFileSize(uploadConfig.getMaxFileSize()) + "）");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !uploadConfig.getAllowedMimeTypes().contains(contentType)) {
            throw new CanShowException("不支持的文件类型: " + contentType);
        }

        // 检查文件扩展名
        String extension = getFileExtension(file.getOriginalFilename());
        if (extension == null || !uploadConfig.getAllowedExtensions().contains(extension.toLowerCase())) {
            throw new CanShowException("不支持的文件扩展名: " + extension);
        }
    }

    /**
     * 生成COS对象key（存储路径）
     * 格式：files/2025/12/08/uuid_filename.ext
     */
    private String generateObjectKey(String originalFilename, String uuid) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String safeFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        return uploadConfig.getCosPathPrefix() + "/" + date + "/" + uuid + "_" + safeFilename;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return null;
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * 创建秒传响应
     */
    private FileUploadResponse createQuickUploadResponse(File file) {
        String fileUrl = cosService.getFileUrl(file.getStoragePath());
        return new FileUploadResponse(
            file.getId(),
            file.getUuid(),
            file.getFileName(),
            file.getFileSize(),
            fileUrl,
            true,
            "文件秒传成功"
        );
    }

    /**
     * 转换为FileVO
     */
    private FileVO convertToFileVO(File file) {
        FileVO vo = new FileVO();
        BeanUtils.copyProperties(file, vo);

        // 生成下载URL（临时签名URL）
        vo.setDownloadUrl(cosService.generatePresignedUrl(
            file.getStoragePath(),
            cosConfig.getUrlExpirationSeconds()
        ).toString());

        // 生成预览URL
        vo.setPreviewUrl(vo.getDownloadUrl());

        // 格式化文件大小
        vo.setFileSizeFormatted(formatFileSize(file.getFileSize()));

        return vo;
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(Long size) {
        if (size == null) return "0 B";

        double kb = size / 1024.0;
        if (kb < 1024) return String.format("%.2f KB", kb);

        double mb = kb / 1024.0;
        if (mb < 1024) return String.format("%.2f MB", mb);

        double gb = mb / 1024.0;
        return String.format("%.2f GB", gb);
    }
}
