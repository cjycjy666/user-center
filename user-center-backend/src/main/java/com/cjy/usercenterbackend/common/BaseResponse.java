package com.cjy.usercenterbackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * Function: 通用返回类
 * Author: cjy
 * Date: 2024/4/11 16:34
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    private String description;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data, String message) {
        this(code, data, message, null);
    }

    public BaseResponse(int code, T data) {
        this(code, data, null, null);
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }
}
