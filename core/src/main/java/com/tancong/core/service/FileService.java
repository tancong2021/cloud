package com.tancong.core.service;

import com.tancong.core.entity.File;
import com.tancong.core.entity.vo.FileUploadResponse;
import com.tancong.core.entity.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * ===================================
 * 文件业务服务接口
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/08
 */
public interface FileService extends BaseService<File> {

    /**
     * 上传文件
     *
     * @param file 文件对象
     * @param userId 用户ID
     * @param md5 文件MD5值（前端计算）
     * @param folderId 文件夹ID
     * @return 上传响应
     */
    FileUploadResponse uploadFile(MultipartFile file, Long userId, String md5, Long folderId);

    /**
     * 检查文件是否可以秒传
     *
     * @param md5 文件MD5
     * @param userId 用户ID
     * @return 文件信息（如果可以秒传）
     */
    File checkQuickUpload(String md5, Long userId);

    /**
     * 获取文件下载URL
     *
     * @param fileId 文件ID
     * @param userId 用户ID（权限校验）
     * @return 下载URL
     */
    String getDownloadUrl(Long fileId, Long userId);

    /**
     * 删除文件（软删除）
     *
     * @param fileId 文件ID
     * @param userId 用户ID（权限校验）
     * @return 是否成功
     */
    boolean deleteFile(Long fileId, Long userId);

    /**
     * 彻底删除文件（包括COS文件）
     *
     * @param fileId 文件ID
     * @param userId 用户ID（权限校验）
     * @return 是否成功
     */
    boolean permanentDeleteFile(Long fileId, Long userId);

    /**
     * 获取用户文件列表（支持分页）
     *
     * @param userId 用户ID
     * @param folderId 文件夹ID
     * @return 文件列表
     */
    List<FileVO> getUserFiles(Long userId, Long folderId);

    /**
     * 获取文件详情
     *
     * @param fileId 文件ID
     * @param userId 用户ID（权限校验）
     * @return 文件VO
     */
    FileVO getFileDetail(Long fileId, Long userId);
}
