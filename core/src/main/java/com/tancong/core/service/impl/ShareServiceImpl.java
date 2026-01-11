package com.tancong.core.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tancong.common.exception.CanShowException;
import com.tancong.common.utils.CacheManagers;
import com.tancong.core.entity.File;
import com.tancong.core.entity.Share;
import com.tancong.core.entity.ShareAccessLog;
import com.tancong.core.entity.dto.CreateShareDTO;
import com.tancong.core.entity.dto.UpdateShareDTO;
import com.tancong.core.entity.enums.FileTypeEnum;
import com.tancong.core.entity.enums.ShareAccessTypeEnum;
import com.tancong.core.entity.enums.ShareExpireTypeEnum;
import com.tancong.core.entity.enums.ShareStatusEnum;
import com.tancong.core.entity.vo.*;
import com.tancong.core.mapper.ShareAccessLogMapper;
import com.tancong.core.mapper.ShareMapper;
import com.tancong.core.service.CosService;
import com.tancong.core.service.FileService;
import com.tancong.core.service.ShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ===================================
 * 文件分享业务服务实现类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/25
 */
@Slf4j
@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share> implements ShareService {

    @Autowired
    private ShareMapper shareMapper;

    @Autowired
    private ShareAccessLogMapper shareAccessLogMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private CosService cosService;

    @Autowired
    private com.tancong.core.mapper.FileMapper fileMapper;

    /**
     * 创建文件分享
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShareVO createShare(CreateShareDTO dto, Long userId) {
        // 1. 验证文件所有权
        File file = fileService.getById(dto.getFileId());
        if (file == null) {
            throw new CanShowException("文件不存在");
        }
        if (!file.getUserId().equals(userId)) {
            throw new CanShowException("无权分享该文件");
        }

        // 2. 生成唯一分享码
        String shareCode = generateUniqueShareCode();

        // 3. 生成或验证提取码
        String extractCode = StrUtil.isNotBlank(dto.getExtractCode())
                ? dto.getExtractCode()
                : generateExtractCode();

        // 4. 计算过期时间
        LocalDateTime expireTime = calculateExpireTime(dto.getExpireType());

        // 5. 创建分享记录
        Share share = new Share()
                .setShareCode(shareCode)
                .setExtractCode(extractCode)
                .setFileId(dto.getFileId())
                .setUserId(userId)
                .setExpireType(dto.getExpireType())
                .setExpireTime(expireTime)
                .setStatus(ShareStatusEnum.NORMAL)
                .setViewCount(0)
                .setDownloadCount(0)
                .setFailedAttempts(0)
                .setRemark(dto.getRemark());

        shareMapper.insert(share);

        // 6. 转换为VO返回
        return convertToShareVO(share, file);
    }

    /**
     * 获取我的分享列表
     */
    @Override
    public Pager<ShareVO> getMyShares(Long userId, Pager<ShareVO> pager) {
        QueryWrapper<Share> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .ne("status", ShareStatusEnum.CANCELLED.getValue())
                .orderByDesc("create_time");

        Page<Share> page = new Page<>(pager.getCurrent(), pager.getSize());
        Page<Share> sharePage = shareMapper.selectPage(page, wrapper);

        // 转换为VO
        List<ShareVO> voList = sharePage.getRecords().stream()
                .map(share -> {
                    File file = fileService.getById(share.getFileId());
                    return convertToShareVO(share, file);
                })
                .collect(Collectors.toList());

        pager.setRecords(voList);
        pager.setTotal(sharePage.getTotal());
        return pager;
    }

    /**
     * 获取分享详情
     */
    @Override
    public ShareVO getShareDetail(Long shareId, Long userId) {
        Share share = shareMapper.selectById(shareId);
        if (share == null) {
            throw new CanShowException("分享不存在");
        }

        // 权限校验
        if (!share.getUserId().equals(userId)) {
            throw new CanShowException("无权访问该分享");
        }

        File file = fileService.getById(share.getFileId());
        return convertToShareVO(share, file);
    }

    /**
     * 更新分享设置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShareVO updateShare(UpdateShareDTO dto, Long userId) {
        Share share = shareMapper.selectById(dto.getShareId());
        if (share == null) {
            throw new CanShowException("分享不存在");
        }

        // 权限校验
        if (!share.getUserId().equals(userId)) {
            throw new CanShowException("无权修改该分享");
        }

        // 更新字段
        if (StrUtil.isNotBlank(dto.getExtractCode())) {
            share.setExtractCode(dto.getExtractCode());
        }
        if (dto.getExpireType() != null) {
            share.setExpireType(dto.getExpireType());
            share.setExpireTime(calculateExpireTime(dto.getExpireType()));
        }
        if (dto.getRemark() != null) {
            share.setRemark(dto.getRemark());
        }

        shareMapper.updateById(share);

        File file = fileService.getById(share.getFileId());
        return convertToShareVO(share, file);
    }

    /**
     * 取消分享
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelShare(Long shareId, Long userId) {
        Share share = shareMapper.selectById(shareId);
        if (share == null) {
            throw new CanShowException("分享不存在");
        }

        // 权限校验
        if (!share.getUserId().equals(userId)) {
            throw new CanShowException("无权取消该分享");
        }

        share.setStatus(ShareStatusEnum.CANCELLED);
        return shareMapper.updateById(share) > 0;
    }

    /**
     * 批量取消分享
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCancelShares(List<Long> shareIds, Long userId) {
        for (Long shareId : shareIds) {
            cancelShare(shareId, userId);
        }
        return true;
    }

    /**
     * 验证提取码并获取分享详情（公共访问）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShareDetailVO verifyAndGetShareDetail(String shareCode, String extractCode, String ipAddress, String userAgent) {
        // 1. 防暴力破解检查
        checkBruteForce(shareCode, ipAddress);

        // 2. 查询分享记录
        Share share = shareMapper.selectByShareCode(shareCode);
        if (share == null) {
            throw new CanShowException("分享不存在或已失效");
        }

        // 3. 验证状态和有效期
        validateShareStatus(share);

        // 4. 验证提取码
        if (!share.getExtractCode().equalsIgnoreCase(extractCode)) {
            // 错误次数+1
            incrementFailedAttempts(shareCode, ipAddress);
            // 记录失败日志
            recordAccessLog(share.getId(), ShareAccessTypeEnum.VERIFY_FAILED, ipAddress, userAgent, false, "提取码错误");
            throw new CanShowException("提取码错误");
        }

        // 5. 验证成功，清除错误记录
        clearFailedAttempts(shareCode, ipAddress);

        // 6. 增加访问次数
        shareMapper.incrementViewCount(share.getId());

        // 7. 记录访问日志
        recordAccessLog(share.getId(), ShareAccessTypeEnum.VIEW, ipAddress, userAgent, true, null);

        // 8. 获取文件信息
        File file = fileService.getById(share.getFileId());
        ShareDetailVO detailVO = new ShareDetailVO();
        detailVO.setShareCode(share.getShareCode());
        detailVO.setFileInfo(convertToFileVO(file));
        detailVO.setSharerName("用户***"); // 脱敏处理
        detailVO.setExpireTime(share.getExpireTime() != null ? share.getExpireTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "永久");
        detailVO.setViewCount(share.getViewCount());

        // 9. 如果是文件夹，递归获取子文件
        if (file.getType().equals(FileTypeEnum.FOLDER.getValue())) {
            List<FileVO> children = fileService.getFileTree(file.getId(), file.getUserId());
            detailVO.setChildren(children);
        }

        return detailVO;
    }

    /**
     * 获取分享基本信息（无需验证）
     */
    @Override
    public ShareDetailVO getShareBasicInfo(String shareCode) {
        Share share = shareMapper.selectByShareCode(shareCode);
        if (share == null) {
            throw new CanShowException("分享不存在或已失效");
        }

        validateShareStatus(share);

        File file = fileService.getById(share.getFileId());
        ShareDetailVO detailVO = new ShareDetailVO();
        detailVO.setShareCode(share.getShareCode());

        // 只返回基本信息，不返回详细内容
        FileVO fileVO = new FileVO();
        fileVO.setName(file.getName());
        fileVO.setType(file.getType());
        fileVO.setFileSize(file.getFileSize());
        detailVO.setFileInfo(fileVO);

        detailVO.setSharerName("用户***");
        detailVO.setExpireTime(share.getExpireTime() != null ? share.getExpireTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "永久");

        return detailVO;
    }

    /**
     * 获取分享文件下载URL
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getSharedFileDownloadUrl(String shareCode, Long fileId, String ipAddress, String userAgent) {
        Share share = shareMapper.selectByShareCode(shareCode);
        if (share == null) {
            throw new CanShowException("分享不存在或已失效");
        }

        validateShareStatus(share);

        // 确定要下载的文件
        Long targetFileId = fileId != null ? fileId : share.getFileId();
        File file = fileService.getById(targetFileId);
        if (file == null) {
            throw new CanShowException("文件不存在");
        }

        // 如果指定了子文件，需要验证该文件是否属于分享的文件夹
        if (fileId != null && !fileId.equals(share.getFileId())) {
            File sharedFile = fileService.getById(share.getFileId());
            if (!sharedFile.getType().equals(FileTypeEnum.FOLDER.getValue())) {
                throw new CanShowException("该分享不支持指定文件下载");
            }
            // 验证fileId是否在文件夹树中
            Boolean isInTree = fileMapper.isFileInFolderTree(share.getFileId(), fileId, sharedFile.getUserId());
            if (isInTree == null || !isInTree) {
                throw new CanShowException("文件不在分享范围内");
            }
        }

        // 增加下载次数
        shareMapper.incrementDownloadCount(share.getId());

        // 记录下载日志
        recordAccessLog(share.getId(), ShareAccessTypeEnum.DOWNLOAD, ipAddress, userAgent, true, "下载文件: " + file.getFileName());

        // 生成临时下载URL（1小时有效）
        return cosService.generatePresignedUrl(file.getStoragePath(), 3600L).toString();
    }

    /**
     * 获取分享访问日志
     */
    @Override
    public Pager<ShareAccessLogVO> getShareAccessLogs(Long shareId, Long userId, Pager<ShareAccessLogVO> pager) {
        // 验证分享所有权
        Share share = shareMapper.selectById(shareId);
        if (share == null || !share.getUserId().equals(userId)) {
            throw new CanShowException("无权查看该分享日志");
        }

        QueryWrapper<ShareAccessLog> wrapper = new QueryWrapper<>();
        wrapper.eq("share_id", shareId)
                .orderByDesc("access_time");

        Page<ShareAccessLog> page = new Page<>(pager.getCurrent(), pager.getSize());
        Page<ShareAccessLog> logPage = shareAccessLogMapper.selectPage(page, wrapper);

        List<ShareAccessLogVO> voList = logPage.getRecords().stream()
                .map(log -> {
                    ShareAccessLogVO vo = new ShareAccessLogVO();
                    BeanUtils.copyProperties(log, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        pager.setRecords(voList);
        pager.setTotal(logPage.getTotal());
        return pager;
    }

    /**
     * 定时任务：自动过期分享
     */
    @Override
    @Scheduled(cron = "0 0 * * * ?") // 每小时执行一次
    public void expireShares() {
        int count = shareMapper.expireShares();
        if (count > 0) {
            log.info("自动过期分享任务完成，过期数量：{}", count);
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 生成唯一分享码
     */
    private String generateUniqueShareCode() {
        int maxAttempts = 10;
        for (int i = 0; i < maxAttempts; i++) {
            String code = RandomUtil.randomString(8).toLowerCase();
            if (shareMapper.selectByShareCode(code) == null) {
                return code;
            }
        }
        throw new CanShowException("生成分享码失败，请重试");
    }

    /**
     * 生成提取码
     */
    private String generateExtractCode() {
        return RandomUtil.randomString("0123456789abcdefghijklmnopqrstuvwxyz", 4);
    }

    /**
     * 计算过期时间
     */
    private LocalDateTime calculateExpireTime(ShareExpireTypeEnum expireType) {
        if (expireType.getDays() < 0) {
            return null; // 永久有效
        }
        return LocalDateTime.now().plusDays(expireType.getDays());
    }

    /**
     * 验证分享状态
     */
    private void validateShareStatus(Share share) {
        if (share.getStatus().equals(ShareStatusEnum.CANCELLED)) {
            throw new CanShowException("分享已被取消");
        }
        if (share.getStatus().equals(ShareStatusEnum.EXPIRED)) {
            throw new CanShowException("分享已过期");
        }
        if (share.getExpireTime() != null && share.getExpireTime().isBefore(LocalDateTime.now())) {
            // 自动标记为过期
            share.setStatus(ShareStatusEnum.EXPIRED);
            shareMapper.updateById(share);
            throw new CanShowException("分享已过期");
        }
    }

    /**
     * 防暴力破解检查
     */
    private void checkBruteForce(String shareCode, String ipAddress) {
        String redisKey = "share:verify:fail:" + shareCode + ":" + ipAddress;
        Object failCountObj = CacheManagers.get(redisKey);
        Integer failCount = failCountObj != null ? (Integer) failCountObj : 0;

        if (failCount >= 5) {
            throw new CanShowException("尝试次数过多，请1小时后再试");
        }
    }

    /**
     * 增加失败次数
     */
    private void incrementFailedAttempts(String shareCode, String ipAddress) {
        String redisKey = "share:verify:fail:" + shareCode + ":" + ipAddress;
        Object failCountObj = CacheManagers.get(redisKey);
        Integer failCount = failCountObj != null ? (Integer) failCountObj : 0;
        CacheManagers.set(redisKey, failCount + 1, 3600); // 1小时过期
    }

    /**
     * 清除失败次数
     */
    private void clearFailedAttempts(String shareCode, String ipAddress) {
        String redisKey = "share:verify:fail:" + shareCode + ":" + ipAddress;
        CacheManagers.del(redisKey);
    }

    /**
     * 记录访问日志
     */
    private void recordAccessLog(Long shareId, ShareAccessTypeEnum accessType, String ipAddress, String userAgent, Boolean success, String remark) {
        ShareAccessLog log = new ShareAccessLog()
                .setShareId(shareId)
                .setAccessType(accessType)
                .setIpAddress(ipAddress)
                .setUserAgent(userAgent)
                .setAccessTime(LocalDateTime.now())
                .setSuccess(success)
                .setRemark(remark);

        shareAccessLogMapper.insert(log);
    }

    /**
     * 转换为ShareVO
     */
    private ShareVO convertToShareVO(Share share, File file) {
        ShareVO vo = new ShareVO();
        BeanUtils.copyProperties(share, vo);

        // 设置文件信息
        vo.setFileInfo(convertToFileVO(file));

        // 设置分享链接（假设前端域名为当前域名）
        vo.setShareUrl("/public/shares/" + share.getShareCode());

        // 计算是否过期
        if (share.getExpireTime() != null) {
            vo.setExpired(share.getExpireTime().isBefore(LocalDateTime.now()));
            long days = ChronoUnit.DAYS.between(LocalDateTime.now(), share.getExpireTime());
            vo.setRemainingDays((int) Math.max(0, days));
        } else {
            vo.setExpired(false);
            vo.setRemainingDays(-1); // 永久
        }

        return vo;
    }

    /**
     * 转换为FileVO
     */
    private FileVO convertToFileVO(File file) {
        FileVO vo = new FileVO();
        BeanUtils.copyProperties(file, vo);
        return vo;
    }
}
