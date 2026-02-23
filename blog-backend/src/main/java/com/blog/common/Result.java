package com.blog.common;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public Result() {
        this.timestamp = LocalDateTime.now();
    }

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功");
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    public static <T> Result<T> success(String message) {
        return new Result<>(200, message);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败");
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }

    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未授权");
    }

    public static <T> Result<T> forbidden() {
        return new Result<>(403, "禁止访问");
    }

    public static <T> Result<T> notFound() {
        return new Result<>(404, "资源不存在");
    }

    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message);
    }
}
