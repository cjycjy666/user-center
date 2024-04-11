package com.cjy.usercenterbackend.utils;

import com.cjy.usercenterbackend.common.BaseResponse;
import com.cjy.usercenterbackend.common.ErrorCode;

/**
 * Function: 返回工具类
 * Author: cjy
 * Date: 2024/4/11 16:44
 */
public class ResultUtils {
    /**
     * 成功
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    public static BaseResponse error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse<>(errorCode.getCode(), null, message, description);
    }

    public static BaseResponse error(int code, String message, String description) {
        return new BaseResponse(code, null, message, description);
    }

    public static BaseResponse error(ErrorCode errorCode, String description) {
        return new BaseResponse(errorCode.getCode(), null, description);
    }


}
