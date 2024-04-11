package com.cjy.usercenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjy.usercenterbackend.common.ErrorCode;
import com.cjy.usercenterbackend.exception.BusinessException;
import com.cjy.usercenterbackend.model.domain.User;
import com.cjy.usercenterbackend.service.UserService;
import com.cjy.usercenterbackend.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cjy.usercenterbackend.constant.UserConstant.USER_LOGIN_STATE;

/**
* 用户服务实现类
* @author chenjiangyu
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-04-07 11:18:03
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Resource
    private UserMapper userMapper;
    /**
     * 盐值
     */
    private static final String SALT = "cjy";

    /**
     * 用户注册实现
     * @param userAccount   用户名
     * @param userPassword  密码
     * @param checkPassword 校验码
     * @return 用户的id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能小于 4 位");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能小于 8 位");
        }
        //账户不包含特殊字符
        String validPattern = "[\\p{P}\\p{S}]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不包含特殊字符");
        }
        //密码和校验码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入密码不同");
        }
        //账户不重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }
        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //3.操作数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return user.getId();
    }

    /**
     * 用户登陆实现
     * @param userAccount  用户名
     * @param userPassword 密码
     * @param request 用户登陆态
     * @return 脱敏用户信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能小于 4 位");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能小于 8 位");
        }
        //账户不包含特殊字符
        String validPattern = "[\\p{P}\\p{S}]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能包含特殊字符");
        }
        //2.匹配数据库查询密码
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user login failed, userAccount can not match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }
        //3.用户脱敏
        User safetyUser = getSafetyUser(user);
        //4.记录用户登陆态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);


        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param originUser 原始用户数据
     * @return 脱敏数据
     */
    @Override
    public User getSafetyUser(User originUser) {
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUserRole(originUser.getUserRole());
        return safetyUser;
    }

    /**
     * 用户注销
     * @param request HttpServletReuqest
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        //移除登陆态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




