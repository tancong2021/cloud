package com.tancong.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tancong.core.entity.File;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ===================================
 * File数据访问层
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/08
 */
@Mapper
public interface FileMapper extends BaseMapper<File> {



    /**
     * 递归查询指定文件夹及其所有子文件夹下的所有文件和文件夹。
     * 适用于 MySQL 8.0+ 的 WITH RECURSIVE 语法。
     * @param parentFolderId 父文件夹ID
     * @param userId 用户ID
     * @return 所有子项的列表
     */
    List<File> selectAllFilesRecursively(@Param("parentFolderId") Long parentFolderId, @Param("userId") Long userId);

    /**
     * 递归软删除文件/文件夹及其所有子项
     * @param id 文件/文件夹ID
     * @param userId 用户ID
     * @param status 新状态 (如 FileStatusEnum.DELETED.getValue())
     * @return 受影响的行数
     */
    int softDeleteFileAndChildren(
            @Param("id") Long id,
            @Param("userId") Long userId,
            @Param("status") Integer status,
            @Param("updateTime") LocalDateTime updateTime
    );
    /**
     * 物理彻底删除文件/文件夹，递归的
     * @param id
     * @param userId
     * @return
     */
    int permanentDeleteFileAndChildren(@Param("id") Long id,
                                       @Param("userId") Long userId);

    /**
     * 递归查询文件夹下所有文件的路径（仅文件，不含文件夹）
     */
    List<String> selectFilePathsInFolder(
            @Param("folderId") Long folderId,
            @Param("userId") Long userId
    );

    /**
     * 根据MD5和用户ID查询文件（用于秒传）
     */
    @Select("SELECT * FROM file WHERE md5 = #{md5} AND user_id = #{userId} AND status = 1 LIMIT 1")
    File selectByMd5AndUserId(@Param("md5") String md5, @Param("userId") Long userId);

    /**
     * 根据MD5查询任意用户的文件（用于跨用户秒传）
     */
    @Select("SELECT * FROM file WHERE md5 = #{md5} AND status = 1 LIMIT 1")
    File selectByMd5(@Param("md5") String md5);

    /**
     * 增加下载次数
     */
    @Update("UPDATE file SET download_count = download_count + 1 WHERE id = #{fileId}")
    int incrementDownloadCount(@Param("fileId") Long fileId);

    /**
     * 软删除文件（移到回收站）
     */
    @Update("UPDATE file SET status = 0 WHERE id = #{fileId} AND user_id = #{userId}")
    int softDeleteFile(@Param("fileId") Long fileId, @Param("userId") Long userId);

    /**
     * 恢复文件
     */
    @Update("UPDATE file SET status = 1 WHERE id = #{fileId} AND user_id = #{userId}")
    int restoreFile(@Param("fileId") Long fileId, @Param("userId") Long userId);

    /**
     * 更新文件名和文件夹的名字
     * @param fileId
     * @param userId
     * @param newName
     * @return
     */
    @Update("UPDATE file SET file_name = #{newName}, update_time = #{updateTime} " +
            "WHERE id = #{fileId} AND user_id = #{userId}")
    Integer updateFileName(@Param("fileId") Long fileId, @Param("userId") Long userId, @Param("newName") String newName, @Param("updateTime") LocalDateTime updateTime);
    @Update("UPDATE file SET folder_id = #{targetFolderId}, update_time = #{updateTime} " +
            "WHERE id = #{fileId} AND user_id = #{userId}")
    Integer updateFolderId(@Param("fileId") Long fileId, @Param("userId") Long userId, @Param("targetFolderId")  Long targetFolderId, @Param("updateTime") LocalDateTime updateTime);

    /**
     * 验证指定文件是否在某个文件夹的子树中
     * @param rootFolderId 根文件夹ID
     * @param targetFileId 目标文件ID
     * @param userId 用户ID
     * @return true表示文件在文件夹树中，false表示不在
     */
    Boolean isFileInFolderTree(
            @Param("rootFolderId") Long rootFolderId,
            @Param("targetFileId") Long targetFileId,
            @Param("userId") Long userId
    );
}
