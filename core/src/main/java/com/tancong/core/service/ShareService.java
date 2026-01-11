package com.tancong.core.service;

import com.tancong.core.entity.Share;
import com.tancong.core.entity.dto.CreateShareDTO;
import com.tancong.core.entity.dto.UpdateShareDTO;
import com.tancong.core.entity.vo.Pager;
import com.tancong.core.entity.vo.ShareAccessLogVO;
import com.tancong.core.entity.vo.ShareDetailVO;
import com.tancong.core.entity.vo.ShareVO;

import java.util.List;

/**
 * ===================================
 * 文件分享业务服务接口
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/25
 */
public interface ShareService extends BaseService<Share> {

    /**
     * 创建文件分享
     *
     * @param dto 创建分享请求
     * @param userId 用户ID
     * @return 分享详情
     */
    ShareVO createShare(CreateShareDTO dto, Long userId);

    /**
     * 获取我的分享列表（分页）
     *
     * @param userId 用户ID
     * @param pager 分页参数
     * @return 分享列表
     */
    Pager<ShareVO> getMyShares(Long userId, Pager<ShareVO> pager);

    /**
     * 获取分享详情（仅创建者）
     *
     * @param shareId 分享ID
     * @param userId 用户ID
     * @return 分享详情
     */
    ShareVO getShareDetail(Long shareId, Long userId);

    /**
     * 更新分享设置
     *
     * @param dto 更新请求
     * @param userId 用户ID
     * @return 更新后的分享详情
     */
    ShareVO updateShare(UpdateShareDTO dto, Long userId);

    /**
     * 取消分享
     *
     * @param shareId 分享ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean cancelShare(Long shareId, Long userId);

    /**
     * 批量取消分享
     *
     * @param shareIds 分享ID列表
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean batchCancelShares(List<Long> shareIds, Long userId);

    /**
     * 验证提取码并获取分享详情（公共访问）
     *
     * @param shareCode 分享码
     * @param extractCode 提取码
     * @param ipAddress 访问者IP
     * @param userAgent 浏览器信息
     * @return 分享详情
     */
    ShareDetailVO verifyAndGetShareDetail(String shareCode, String extractCode, String ipAddress, String userAgent);

    /**
     * 获取分享基本信息（无需验证）
     *
     * @param shareCode 分享码
     * @return 基本信息
     */
    ShareDetailVO getShareBasicInfo(String shareCode);

    /**
     * 获取分享文件下载URL（公共访问）
     *
     * @param shareCode 分享码
     * @param fileId 文件ID（文件夹分享时指定子文件）
     * @param ipAddress 访问者IP
     * @param userAgent 浏览器信息
     * @return 下载URL
     */
    String getSharedFileDownloadUrl(String shareCode, Long fileId, String ipAddress, String userAgent);

    /**
     * 获取分享访问日志
     *
     * @param shareId 分享ID
     * @param userId 用户ID
     * @param pager 分页参数
     * @return 访问日志列表
     */
    Pager<ShareAccessLogVO> getShareAccessLogs(Long shareId, Long userId, Pager<ShareAccessLogVO> pager);

    /**
     * 定时任务：自动过期分享
     */
    void expireShares();
}
