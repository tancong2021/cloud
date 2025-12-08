package com.tancong.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tancong.core.entity.File;
import org.apache.ibatis.annotations.*;

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
}
