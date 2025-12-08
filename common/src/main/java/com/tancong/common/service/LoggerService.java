package com.tancong.common.service;

import com.tancong.common.entity.Log;
import org.springframework.stereotype.Service;

/**
 * ===================================
 * 日志记录服务 LoggerService
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/04
 */

public interface LoggerService {
    /**
     * 插入一条日志记录
     * @param log
     * @return
     */
    boolean insert(Log log);
}
