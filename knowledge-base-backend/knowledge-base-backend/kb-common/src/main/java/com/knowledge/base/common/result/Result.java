package com.knowledge.base.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果封装类
 *
 * <p>按照阿里巴巴Java开发规范设计，所有接口返回统一格式的响应结果</p>
 *
 * @param <T> 数据类型
 * @author fangAndlu
 * @since 1.0.0
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 私有构造方法
     */
    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 私有构造方法
     *
     * @param code    响应码
     * @param message 响应消息
     * @param data    响应数据
     */
    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }


    /**
     * 返回成功响应结果（无业务数据）
     * <p>适用于执行了增、删、改等操作，但不需要向前端返回具体业务数据的场景。</p>
     *
     * @param <T> 响应数据的泛型类型
     * @return 包含成功状态码和成功提示信息的 Result 对象，data 为 null
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功返回（有数据）
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 统一响应结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回（自定义消息）
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return 统一响应结果
     */
    public static <T> Result<T> success(String message) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, null);
    }

    /**
     * 成功返回（自定义消息和数据）
     *
     * @param message 消息
     * @param data    数据
     * @param <T>     数据类型
     * @return 统一响应结果
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回（默认错误）
     *
     * @param <T> 数据类型
     * @return 统一响应结果
     */
    public static <T> Result<T> error() {
        return new Result<>(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMessage(), null);
    }

    /**
     * 失败响应（错误码）
     */
    public static <T> Result<T> error(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 失败返回（自定义消息）
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 统一响应结果
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCode.ERROR.getCode(), message, null);
    }

    /**
     * 失败返回（自定义错误码和消息）
     *
     * @param code    错误码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 统一响应结果
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败返回（使用结果码枚举）
     *
     * @param resultCode 结果码枚举
     * @param <T>        数据类型
     * @return 统一响应结果
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 根据条件返回成功或失败
     *
     * @param flag 条件标识
     * @param <T>  数据类型
     * @return 统一响应结果
     */
    public static <T> Result<T> error(boolean flag) {
        return flag ? success() : error();
    }

}
