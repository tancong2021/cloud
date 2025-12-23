package com.tancong.core.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tancong.common.exception.BusinessException;
import com.tancong.common.exception.CanShowException;
import com.tancong.common.utils.FileSecurityUtils;
import com.tancong.core.config.CosConfig;
import com.tancong.core.config.FileUploadConfig;
import com.tancong.core.entity.File;
import com.tancong.core.entity.enums.FileStatusEnum;
import com.tancong.core.entity.enums.FileTypeEnum;
import com.tancong.core.entity.vo.FileUploadResponse;
import com.tancong.core.entity.vo.FileVO;
import com.tancong.core.entity.vo.Pager;
import com.tancong.core.mapper.FileMapper;
import com.tancong.core.service.CosService;
import com.tancong.core.service.FileService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    /**
     * 上传文件
     * @param file 文件对象
     * @param userId 用户ID
     * @param md5 文件MD5值（前端计算）
     * @param folderId 文件夹ID
     * @return
     */
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
                .setFileName(FileSecurityUtils.sanitizeFilename(file.getOriginalFilename()))
                .setFileSize(file.getSize())
                .setFileType(file.getContentType())
                .setType(FileTypeEnum.FILE.getValue()) // 【重点：设置类型为文件】
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

    /**
     * 创建文件夹
     * @param folderName
     * @param userId
     * @param parentFolderId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileVO createFolder(String folderName, Long userId, Long parentFolderId) {
        // 1. 校验参数（名称是否为空，父文件夹是否存在和权限）
        if (StrUtil.isBlank(folderName)) {
            throw new CanShowException("文件夹名称不能为空");
        }

        // 2. 检查当前目录下是否有重名文件夹（可选：防止同名）
        // 可以在 FileMapper 中新增 selectByUserIdAndFolderIdAndName(userId, parentFolderId, folderName)

        // 3. 构建文件夹实体
        File folderEntity = new File()
                .setUuid(IdUtil.simpleUUID())
                .setFileName(FileSecurityUtils.sanitizeFilename(folderName))
                .setFileSize(0L) // 文件夹大小为 0
                .setFileType(null)
                .setFileExtension(null)
                .setStoragePath(null) // 文件夹没有 COS 路径
                .setStorageBucket(cosConfig.getBucketName())
                .setUserId(userId)
                .setFolderId(parentFolderId == null ? 0L : parentFolderId)
                .setType(FileTypeEnum.FOLDER.getValue()) // 【重点：设置类型为文件夹】
                .setMd5(null) // 文件夹没有 MD5
                .setDownloadCount(0)
                .setStatus(FileStatusEnum.NORMAL);

        fileMapper.insert(folderEntity);

        log.info("文件夹创建成功: folderId={}, userId={}, folderName={}",
                folderEntity.getId(), userId, folderName);

        return convertToFileVO(folderEntity);
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

    /**
     * 生成文件下载URL
     * @param fileId 文件ID
     * @param userId 用户ID（权限校验）
     * @return
     */
    @Override
    public String getDownloadUrl(Long fileId, Long userId) {
        // 1. 查询文件
        File file = fileMapper.selectById(fileId);
        if (file == null) {
            throw new CanShowException("文件不存在");
        }
        else if (file.getType() != FileTypeEnum.FILE.getValue()) {
            throw new CanShowException("文件夹无法生成下载URL");

        } // 2. 权限校验
        else if (!file.getUserId().equals(userId)) {
            throw new CanShowException("无权访问该文件");
        } // 3. 状态校验
        else if (file.getStatus() != FileStatusEnum.NORMAL) {
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

    /**
     * 批量删除
     * @param fileIds
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean batchDeleteFiles(List<Long> fileIds, Long userId) {
        for (Long fileId : fileIds) {
            if (!deleteFile(fileId, userId)) {
                throw new BusinessException("批量删除失败");
            }
        }
        return true;
    }

    /**
     * 软删除文件和文件夹
     * @param fileId 文件ID
     * @param userId 用户ID（权限校验）
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFile(Long fileId, Long userId) {
        // 校验文件存在性
        File file = fileMapper.selectById(fileId);
        if (file == null || !file.getUserId().equals(userId)) {
            throw new CanShowException("文件不存在或无权限");
        }

        // ✅ 使用递归 CTE 一次性软删除（包括所有子项）
        int rows = fileMapper.softDeleteFileAndChildren(
                fileId,
                userId,
                FileStatusEnum.DELETED.getValue(),
                LocalDateTime.now()
        );

        log.info("递归软删除完成: fileId={}, 影响行数={}", fileId, rows);
        return rows > 0;
    }

    /**
     * 真实删除--永久
     * @param fileId 文件ID
     * @param userId 用户ID（权限校验）
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean permanentDeleteFile(Long fileId, Long userId) {
        File file = fileMapper.selectById(fileId);
        if (file == null || !file.getUserId().equals(userId)) {
            throw new CanShowException("文件不存在或无权限");
        }

        // 1. 收集所有要删除的文件路径
        List<String> filePaths = new ArrayList<>();
        if(file.getType() == FileTypeEnum.FILE.getValue()) {
            if(StringUtils.isNotBlank(file.getStoragePath())) {
                filePaths.add(file.getStoragePath());
            }
        } else {
            filePaths = fileMapper.selectFilePathsInFolder(fileId, userId);
        }
        // 2. 先删除COS文件（在数据库事务之前）
        List<String> failedDeletions = new ArrayList<>();
        if (!filePaths.isEmpty()) {
            log.info("准备删除 {} 个COS文件", filePaths.size());
            for (String filePath : filePaths) {
                try {
                    cosService.deleteFile(filePath);
                } catch (Exception e) {
                    log.error("删除COS文件失败：{}", filePath, e);
                    failedDeletions.add(filePath);
                }
            }
        }
        // 3. 如果任何COS删除失败，抛出异常回滚
        if (!failedDeletions.isEmpty()) {
            log.error("COS文件删除失败，终止数据库删除。失败数：{}， 失败路径：{}",
                    failedDeletions.size(), failedDeletions);
            throw new CanShowException("文件删除失败，请稍后重试");
        }
        // 4. 删除数据库记录（事务内）
        int rows = fileMapper.permanentDeleteFileAndChildren(fileId, userId);
        log.info("彻底删除完成: fileId={}, 影响行数={}", fileId, rows);
        return rows > 0;
    }

    /**
     * 获取用户文件列表（不分页，用于树形结构）
     * @param userId
     * @param folderId
     * @return
     */
    @Override
    public List<FileVO> getUserFiles(Long userId, Long folderId) {
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("folder_id", folderId == null ? 0 : folderId)
                .eq("status", FileStatusEnum.NORMAL.getValue())
                // 【可选优化】将文件夹排在文件前面，并按创建时间倒序
                .orderByDesc("type") // 文件夹 type=2, 文件 type=1 (默认)
                .orderByDesc("create_time");

        List<File> files = fileMapper.selectList(wrapper);

        return files.stream().map(this::convertToFileVO).collect(Collectors.toList());
    }

    /**
     * 【新增】获取用户文件列表（分页）
     * @param userId
     * @param folderId
     * @param pager
     * @return
     */
    @Override
    public Pager<FileVO> getUserFilesWithPage(Long userId, Long folderId, Pager<FileVO> pager) {
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("folder_id", folderId == null ? 0 : folderId)
                .eq("status", FileStatusEnum.NORMAL.getValue())
                .orderByDesc("type")  // 文件夹优先
                .orderByDesc("create_time");

        // MyBatis-Plus 分页
        Page<File> page = new Page<>(pager.getCurrent(), pager.getSize());
        Page<File> filePage = fileMapper.selectPage(page, wrapper);

        // 转换为 VO
        List<FileVO> voList = filePage.getRecords().stream()
                .map(this::convertToFileVO)
                .collect(Collectors.toList());

        pager.setRecords(voList);
        pager.setTotal(filePage.getTotal());
        return pager;
    }

    /**
     * 构建文件树结构
     * @param rootFolderId
     * @param userId
     * @return
     */
    @Override
    public List<FileVO> getFileTree(Long rootFolderId, Long userId) {
        // 1. 调用 Mapper 递归查询所有子项（平铺列表）
        // 注意：这里查询的是以 rootFolderId 的子项为起点，不包含 rootFolderId 自身
        List<File> allItems = fileMapper.selectAllFilesRecursively(rootFolderId, userId);

        // 2. 转换为 FileVO 列表
        List<FileVO> allVOs = allItems.stream()
                .map(this::convertToFileVO) // convertToFileVO 保持不变
                .collect(Collectors.toList());

        // 3. 构建树结构
        return buildFileTree(allVOs, rootFolderId);
    }

    /**
     * 辅助方法：将平铺的 List<FileVO> 转换为树状结构。
     * @param allVOs 所有的 FileVO 对象（包含文件和文件夹）
     * @param rootId 树的根节点ID（即当前查询的 folder_id）
     * @return 根节点下的子项列表
     */
    private List<FileVO> buildFileTree(List<FileVO> allVOs, Long rootId) {
        // 最终的树根列表（即当前目录下的第一级子项）
        List<FileVO> rootChildren = new ArrayList<>();

        // Map<ID, FileVO>，用于通过ID快速查找节点
        Map<Long, FileVO> voMap = allVOs.stream()
                .collect(Collectors.toMap(FileVO::getId, vo -> vo));

        for (FileVO vo : allVOs) {
            Long parentId = vo.getFolderId(); // 获取父文件夹ID

            if (parentId.equals(rootId)) {
                // 如果父ID等于我们指定的根ID，则它是第一级子项
                rootChildren.add(vo);
            } else {
                // 如果父ID在 Map 中存在，则将其作为子节点添加到父节点的 children 列表中
                FileVO parentVO = voMap.get(parentId);
                if (parentVO != null) {
                    if (parentVO.getChildren() == null) {
                        parentVO.setChildren(new ArrayList<>());
                    }
                    parentVO.getChildren().add(vo);
                }
                // 否则，该节点可能是一个错误数据或根ID的子项
            }
        }

        // 【可选优化】对根节点下的子项进行排序（文件夹在前，按名称或创建时间）
        rootChildren.sort((v1, v2) -> {
            // 假设 FileTypeEnum.FOLDER.getValue() > FileTypeEnum.FILE.getValue()
            if (!v1.getType().equals(v2.getType())) {
                return v2.getType().compareTo(v1.getType()); // 文件夹在前
            }
            return v1.getFileName().compareTo(v2.getFileName()); // 按名称排序
        });

        return rootChildren;
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
    public FileVO convertToFileVO(File file) {
        FileVO vo = new FileVO();
        BeanUtils.copyProperties(file, vo);

        // 【修改】仅对文件生成下载URL
        if (file.getType() == FileTypeEnum.FILE.getValue() &&
                StrUtil.isNotBlank(file.getStoragePath())) {
            vo.setDownloadUrl(cosService.generatePresignedUrl(
                    file.getStoragePath(),
                    cosConfig.getUrlExpirationSeconds()
            ).toString());

            // 生成预览URL
            vo.setPreviewUrl(vo.getDownloadUrl());
        } else {
            // 文件夹没有下载链接
            vo.setDownloadUrl(null);
            vo.setPreviewUrl(null);
        }

        // 格式化文件大小
        vo.setFileSizeFormatted(formatFileSize(file.getFileSize()));

        return vo;
    }

    /**
     * 重命名文件或文件夹
     * @param fileId
     * @param userId
     * @param newName
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean renameFile(Long fileId, Long userId, String newName) {
        // 1. 参数校验
        newName = FileSecurityUtils.sanitizeFilename(newName);
        if (StrUtil.isBlank(newName)) {
            throw new CanShowException("新名称不能为空");
        }

        // 2. 查询文件/文件夹
        File file = fileMapper.selectById(fileId);
        if (file == null || !file.getUserId().equals(userId)) {
            throw new CanShowException("文件不存在或无权限");
        }

        // 3. 检查同级目录是否有重名
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("folder_id", file.getFolderId())
                .eq("file_name", newName)
                .ne("id", fileId)
                .eq("status", FileStatusEnum.NORMAL.getValue());

        if (fileMapper.selectCount(wrapper) > 0) {
            throw new CanShowException("当前目录下已存在同名文件或文件夹");
        }

        // 4. 执行重命名
        int rows = fileMapper.updateFileName(fileId, userId, newName, LocalDateTime.now());
        if (rows > 0) {
            log.info("重命名成功: fileId={}, oldName={}, newName={}",
                    fileId, file.getFileName(), newName);
            return true;
        }

        return false;
    }

    /**
     * 移动文件或文件夹
     * @param fileId
     * @param targetFolderId
     * @param userId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean moveFile(Long fileId, Long userId, Long targetFolderId) {
        // 1. 查询源文件/文件夹
        File file = fileMapper.selectById(fileId);
        if (file == null || !file.getUserId().equals(userId)) {
            throw new CanShowException("文件不存在或无权限");
        }

        // 2. 校验目标文件夹
        if (targetFolderId != null && targetFolderId != 0) {
            File targetFolder = fileMapper.selectById(targetFolderId);
            if (targetFolder == null || !targetFolder.getUserId().equals(userId)) {
                throw new CanShowException("目标文件夹不存在或无权限");
            }

            if (targetFolder.getType() != FileTypeEnum.FOLDER.getValue()) {
                throw new CanShowException("目标必须是文件夹");
            }

            // 防止将文件夹移动到自己的子文件夹中（造成循环引用）
            if (file.getType() == FileTypeEnum.FOLDER.getValue()) {
                if (fileId.equals(targetFolderId)) {
                    throw new CanShowException("不能将文件夹移动到自身");
                }
                // 检查 targetFolderId 是否是 fileId 的子孙节点
                if (isDescendant(targetFolderId, fileId)) {
                    throw new BusinessException("不能移动到子文件夹");
                }
            }
        }

        // 3. 检查目标目录是否有重名
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("folder_id", targetFolderId == null ? 0 : targetFolderId)
                .eq("file_name", file.getFileName())
                .ne("id", fileId)
                .eq("status", FileStatusEnum.NORMAL.getValue());

        if (fileMapper.selectCount(wrapper) > 0) {
            throw new CanShowException("目标目录下已存在同名文件或文件夹");
        }

        // 4. 执行移动
        int rows = fileMapper.updateFolderId(userId, fileId, targetFolderId == null ? 0 : targetFolderId, LocalDateTime.now());
        if (rows > 0) {
            log.info("移动成功: fileId={}, oldFolderId={}, newFolderId={}",
                    fileId, file.getFolderId(), targetFolderId);
            return true;
        }

        return false;
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

    /**
     * 检查 targetFolderId 是否是 ancestorFolderId 的子孙节点
     *
     * @param targetFolderId 目标文件夹ID
     * @param ancestorFolderId 祖先文件夹ID
     * @return true 表示 targetFolderId 在 ancestorFolderId 的子树中
     */
    private boolean isDescendant(Long targetFolderId, Long ancestorFolderId) {
        if (targetFolderId == null || targetFolderId == 0) {
            return false;  // 根目录不是任何人的子孙
        }

        Long currentId = targetFolderId;
        int maxDepth = 100;  // 防止数据异常导致死循环
        int depth = 0;

        while (currentId != null && currentId != 0 && depth < maxDepth) {
            if (currentId.equals(ancestorFolderId)) {
                return true;  // 找到了循环引用
            }

            // 查询当前文件夹的父文件夹
            File folder = fileMapper.selectById(currentId);
            if (folder == null) {
                log.warn("文件夹不存在: folderId={}", currentId);
                break;  // 数据异常，中断查询
            }

            currentId = folder.getFolderId();
            depth++;
        }

        if (depth >= maxDepth) {
            log.error("文件夹层级过深，可能存在循环引用: targetFolderId={}", targetFolderId);
            throw new CanShowException("文件夹结构异常");
        }

        return false;
    }
}
