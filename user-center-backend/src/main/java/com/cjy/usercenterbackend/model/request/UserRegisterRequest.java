package com.cjy.usercenterbackend.model.request;

import lombok.Data;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

/**
 * Function: 用户注册请求体
 * Author: cjy
 * Date: 2024/4/7 21:42
 */
@Data
public class UserRegisterRequest implements Serializable {
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
