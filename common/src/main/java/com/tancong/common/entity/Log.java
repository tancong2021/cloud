package com.tancong.common.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用来封装一条操作日志的信息，比如谁做了什么操作，花了多长时间，有没有出错
 * 可以配合这个@LogRecord注解使用
 * @author tancong
 * @create 2025/11/1
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class Log implements Serializable {
    /**
     * 日志标题，比如 “用户登录”
     */
    private String title;
    /**
     * 日志内容或描述
     */
    private String content;
    /**
     * 日志错误信息
     */
    private String error;
    /**
     * 日志类型
     */
    private String type;
    /**
     * 调用的方法名
     */
    private String method;
    /**
     * 操作者的 IP 地址
     */
    private String ip;
    /**
     * 耗时（毫秒）
     */
    private Integer time;
    /**
     * 操作人 ID
     */
    private Long userId;

    @JsonSerialize(using = LocalDateTimeSerializer.class) // 在把 createTime 转成 JSON 时，用 LocalDateTimeSerializer 处理
    /**
     * 模式	        请求时（前端→后端）	响应时（后端→前端）    典型用例
     * READ_ONLY	❌ 不能传入	        ✅ 可以看到	        id, createTime
     * WRITE_ONLY	✅ 需要传入	        ❌ 不会返回	        password, 验证码
     * READ_WRITE	✅ 可以传入	        ✅ 可以看到	        name, email
     */
    @JsonProperty(access = JsonProperty.Access.READ_WRITE) //控制这个字段在 JSON 转换时的访问权限--可读可写
    private LocalDateTime createTime;
}