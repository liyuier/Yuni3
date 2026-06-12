package com.yuier.yuni.webapi.dto;

import lombok.Data;

/**
 * @Title: Result
 * @Author yuier
 * @Package com.yuier.yuni.webapi.dto
 * @Date 2026/06/13
 * @description: 统一 API 响应体
 */

@Data
public class Result<T> {

    /** 业务响应码 */
    private int code;

    /** 响应数据 */
    private T data;

    /** 响应信息 */
    private String message;

    private Result(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(200, data, "ok");
    }

    public static <T> Result<T> ok(T data, String message) {
        return new Result<>(200, data, message);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, null, message);
    }
}
