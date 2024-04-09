package com.cjy.usercenterbackend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * Function: 用户注册请求体
 * Author: cjy
 * Date: 2024/4/7 21:42
 */
@Data
public class UserLoginRequest implements Serializable {
    private String userAccount;
    private String userPassword;
}
