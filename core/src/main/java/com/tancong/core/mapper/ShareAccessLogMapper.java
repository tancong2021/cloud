package com.tancong.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tancong.core.entity.ShareAccessLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * ===================================
 * ShareAccessLog数据访问层
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/25
 */
@Mapper
public interface ShareAccessLogMapper extends BaseMapper<ShareAccessLog> {
    // 基本CRUD使用MyBatis-Plus自动生成
}
