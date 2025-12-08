package com.tancong.common.entity.vo;

import com.tancong.common.entity.enums.RespStatus;

import java.io.Serializable;

/**
 * ==============================
 * 〈统一响应返回类〉
 * ==============================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/25
 */
public class RespBody<T> implements Serializable {
    // 状态响应码
    private int code;
    // 响应信息
    private String msg;
    // 响应数据
    private T data;
    // 无参构造
    public RespBody(){}
    // 有参构造
    public RespBody(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    // ====================
    // ✅ 静态工厂方法（推荐）
    // ====================
    public static <T> RespBody<T> success() { // 成功：无数据
        return new RespBody<>(RespStatus.SUCCESS.getCode(), RespStatus.SUCCESS.getMsg(), null);
    }
    public static <T> RespBody<T> success(T data) { // 成功：有数据
        return new RespBody<>(RespStatus.SUCCESS.getCode(), RespStatus.SUCCESS.getMsg(), data);
    }
    public static <T> RespBody<T> success(String msg, T data) {// 自定义成功返回消息
        return new RespBody<>(RespStatus.SUCCESS.getCode(), msg, data);
    }


    public static <T> RespBody<T> fail() { // 失败
        return new RespBody<>(RespStatus.FAIL.getCode(), RespStatus.FAIL.getMsg(), null);
    }
    public static <T> RespBody<T> fail(String msg) { // 自定义失败返回消息
        return new RespBody<>(RespStatus.FAIL.getCode(), msg, null);
    }
    public static <T> RespBody<T> fail(int code, String msg) { // 自定义失败状态码与消息
        return new RespBody<>(code, msg, null);
    }

    // ==============================
    // ✅ Getter / Setter
    // ==============================
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
