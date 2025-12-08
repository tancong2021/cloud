package com.tancong.core.service.impl;


import com.tancong.common.entity.Log;
import com.tancong.common.service.LoggerService;
import com.tancong.core.entity.DbLog;
import com.tancong.core.mapper.LogMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ===================================
 * 数据库日志服务实现类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/12/04
 */
@Service
public class DbLoggerServiceImpl implements LoggerService {

    @Autowired
    LogMapper logMapper;

    @Override
    public boolean insert(Log log) {
        DbLog dbLog = new DbLog();
        BeanUtils.copyProperties(log, dbLog);
        return logMapper.insert(dbLog) == 1;
    }

}
