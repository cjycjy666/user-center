package com.cjy.usercenterbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cjy.usercenterbackend.constant.UserConstant;
import com.cjy.usercenterbackend.model.domain.User;
import com.cjy.usercenterbackend.model.request.UserLoginRequest;
import com.cjy.usercenterbackend.model.request.UserRegisterRequest;
import com.cjy.usercenterbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.cjy.usercenterbackend.constant.UserConstant.ADMIN_ROLE;
import static com.cjy.usercenterbackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * Function: 用户接口
 * Author: cjy
 * Date: 2024/4/7 21:30
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        return userService.userRegister(userAccount, userPassword, checkPassword);
    }
    @PostMapping("/login")
    public User userRegister(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        return userService.userLogin(userAccount, userPassword, request);
    }
    @GetMapping("/search")
    public List<User> searchUsers(String username, HttpServletRequest request) {
        //鉴权：仅管理员可查询
        if(!isAdmin(request)) {
            return new ArrayList<>();
        }
        //查数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        return userList.stream().map(user -> {
            user.setUserPassword(null);
            return user;
        }).collect(Collectors.toList());
    }

    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody long id, HttpServletRequest request) {
        //鉴权：仅管理员可删除
        if (!isAdmin(request)) {
            return false;
        }
        System.out.println("1121212121212121");
        //删除
        if (id <= 0) {
            return false;
        }
        return userService.removeById(id);
    }

    /**
     * 鉴权
     * @param request
     * @return 是否为管理员
     */
    private boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user == null || user.getUserRole() != ADMIN_ROLE) {
            System.out.println(user);
            return false;
        }
        return true;
    }

}
