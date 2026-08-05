package com.knowledge.base.common.exception;

import com.knowledge.base.common.result.Result;
import com.knowledge.base.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>统一处理所有业务服务的异常</li>
 *   <li>区分内部服务调用和外部API调用</li>
 *   <li>内部调用返回ResponseEntity（保留HTTP状态码）</li>
 *   <li>外部调用返回统一的Result格式（HTTP 200 + 业务错误码）</li>
 * </ul>
 *
 * @author fangAndlu
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(value = BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        /*
         * 一个请求可能同时有多个参数校验失败。
         * e.getBindingResult() 包含了所有的错误信息，
         * 而 getFieldError() 会提取出第一个导致失败的字段错误。
         */
        FieldError fieldError = e.getBindingResult().getFieldError();
        /*
         * fieldError.getDefaultMessage() 获取的是我们在实体类上配置的校验提示语
         * （例如 @NotBlank(message = "用户名不能为空") 中的“用户名不能为空”）。
         */
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.error("参数校验异常：{}", message);
        return Result.error(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 系统异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常：{}", e.getMessage(), e);
        return Result.error(ResultCode.ERROR.getCode(), "系统错误，请联系管理员");
    }
}
