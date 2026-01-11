package com.tancong.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tancong.core.entity.Share;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * ===================================
 * Share数据访问层
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/25
 */
@Mapper
public interface ShareMapper extends BaseMapper<Share> {

    /**
     * 根据分享码查询分享记录
     * @param shareCode 分享码
     * @return 分享记录
     */
    @Select("SELECT * FROM share WHERE share_code = #{shareCode} LIMIT 1")
    Share selectByShareCode(@Param("shareCode") String shareCode);

    /**
     * 增加访问次数
     * @param shareId 分享ID
     * @return 受影响行数
     */
    @Update("UPDATE share SET view_count = view_count + 1 WHERE id = #{shareId}")
    int incrementViewCount(@Param("shareId") Long shareId);

    /**
     * 增加下载次数
     * @param shareId 分享ID
     * @return 受影响行数
     */
    @Update("UPDATE share SET download_count = download_count + 1 WHERE id = #{shareId}")
    int incrementDownloadCount(@Param("shareId") Long shareId);

    /**
     * 批量过期分享（定时任务使用）
     * @return 受影响行数
     */
    @Update("UPDATE share SET status = 2 " +
            "WHERE status = 1 AND expire_time IS NOT NULL AND expire_time < NOW()")
    int expireShares();

    /**
     * 查询用户的所有分享（关联文件信息）
     * @param userId 用户ID
     * @return 分享列表
     */
    List<Share> selectSharesWithFileByUserId(@Param("userId") Long userId);
}
