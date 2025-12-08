package com.tancong.core.entity.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tancong.core.entity.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * ===================================
 * 展示给前端查询数据结果的分页页面查询实体类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/27
 */
public class Pager<T> extends Page<T> {
    /**
     * 模糊查询关键字
     */
    private String word;
    /**
     * 查询条件状态关键字
     */
    private StatusEnum status;

    /**
     * 查询起始日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    /**
     * 查询结束日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    /**
     * 设置为一天最后结束的时间
     * 例子：endTime = 2025-11-27 查询的结束时间是 2025-11-27 00:00:00
     * 修改：endTime = 2025-11-27 23:59:59
     * @param endTime
     * @return
     */
    public Pager<T> setEndTime(Date endTime) {
        this.endTime = endTime;
        endTime.setTime(endTime.getTime() + 86400000 - 1);
        return this;
    }

    /**
     * 告诉分页对象：我要查什么状态的数据
     * @param status
     * @return
     */
    public Pager<T> setStatus(StatusEnum status) {
        this.status = status;
        return this;
    }
}
