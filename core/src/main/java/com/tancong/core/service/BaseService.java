package com.tancong.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tancong.core.entity.vo.Pager;

/**
 * ===================================
 * 这是一个服务实现接口
 * 所有业务 Service 的统一分页查询规范接口
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/27
 */
public interface BaseService<DOMIN> extends IService<DOMIN> {
    default Pager<DOMIN> listOfPage(Pager<DOMIN> pager, QueryWrapper<DOMIN> wrapper) {
        this.page(pager,wrapper);
        return pager;
    };
}
