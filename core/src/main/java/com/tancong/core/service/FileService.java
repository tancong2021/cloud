package com.tancong.core.service;

import com.tancong.core.entity.File;
import com.tancong.core.entity.vo.FileUploadResponse;
import com.tancong.core.entity.vo.FileVO;
import com.tancong.core.entity.vo.Pager;
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
     * 获取用户文件列表（不分页，用于树形结构）
     */
    List<FileVO> getUserFiles(Long userId, Long folderId);

    /**
     * 【新增】获取用户文件列表（分页）
     */
    Pager<FileVO> getUserFilesWithPage(Long userId, Long folderId, Pager<FileVO> pager);

    /**
     * 【新增】递归获取指定根文件夹下的完整文件树结构
     */
    List<FileVO> getFileTree(Long rootFolderId, Long userId);
    /**
     * 获取文件详情
     *
     * @param fileId 文件ID
     * @param userId 用户ID（权限校验）
     * @return 文件VO
     */
    FileVO getFileDetail(Long fileId, Long userId);

    // Service 接口改为返回 FileVO
    FileVO createFolder(String folderName, Long userId, Long parentFolderId);

    /**
     * 新增重命名文件
     * @param fileId
     * @param userId
     * @param newName
     * @return
     */
    boolean renameFile(Long fileId, Long userId, String newName);

    /**
     * 新增移动文件的方法
     * @param fileId
     * @param userId
     * @param targetFolderId
     * @return
     */
    boolean moveFile(Long fileId, Long userId, Long targetFolderId);


    boolean batchDeleteFiles(List<Long> fileIds, Long id);
}
