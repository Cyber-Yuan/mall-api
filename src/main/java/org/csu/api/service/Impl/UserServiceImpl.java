package org.csu.api.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.domain.User;
import org.csu.api.dto.UpdateUserDTO;
import org.csu.api.dto.loginUserDTO;
import org.csu.api.dto.registerUserDTO;
import org.csu.api.persistence.UserMapper;
import org.csu.api.util.CaffeineCacheConfig;
import org.csu.api.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.csu.api.service.UserService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HttpServletRequest request;

    @Resource // 和自己写的类区分开，但和@Autowired都是注入
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    private Cache<String, String> localCache;

    @Override
    public CommonResponse<UserVO> getLoginUser(loginUserDTO loginDto) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", loginDto.getUsername());
        User user = userMapper.selectOne(queryWrapper);
        //用户名没有查询到，返回登录错误
        if (user == null) {
            return CommonResponse.createForErrorMessage("用户名或密码错误");
        }
//        user.setPassword(StringUtils.EMPTY);
        if (bCryptPasswordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return CommonResponse.createForSuccess("登录成功", userVO);
        }
        // 密码错误
        return CommonResponse.createForErrorMessage("用户名或密码错误");
    }

    @Override
    public CommonResponse<Object> checkField(String fieldName, String fieldValue) {
        if (StringUtils.equals(fieldName, CONSTANT.USER_FIELD.USERNAME)) {
            // 用户名不重复
            long rows_username = userMapper.selectCount(Wrappers.<User>query().eq("username", fieldValue));
            if (rows_username > 0) {
                return CommonResponse.createForErrorMessage("用户名已存在");
            }
        } else if (StringUtils.equals(fieldName, CONSTANT.USER_FIELD.EMAIL)) {
            // 邮箱不重复
            long rows_email = userMapper.selectCount(Wrappers.<User>query().eq("email", fieldValue));
            if (rows_email > 0) {
                return CommonResponse.createForErrorMessage("邮箱已存在");
            }
        } else if (StringUtils.equals(fieldName, CONSTANT.USER_FIELD.PHONE)) {
            // 手机号码不重复
            long rows_number = userMapper.selectCount(Wrappers.<User>query().eq("phone", fieldValue));
            if (rows_number > 0) {
                return CommonResponse.createForErrorMessage("电话号码已存在");
            }
        } else {
            return CommonResponse.createForErrorMessage("fieldName参数错误");
        }
        return CommonResponse.createForSuccess();
    }

    @Override
    public CommonResponse<Object> register(registerUserDTO registerUserdto) {
        // 校验
        CommonResponse<Object> checkResult = checkField(CONSTANT.USER_FIELD.USERNAME,registerUserdto.getUsername());
        if (!checkResult.isSuccess()) {
            return checkResult;
        }
        checkResult = checkField(CONSTANT.USER_FIELD.EMAIL,registerUserdto.getEmail());
        if (!checkResult.isSuccess()) {
            return checkResult;
        }
        checkResult = checkField(CONSTANT.USER_FIELD.PHONE,registerUserdto.getPhone());
        if (!checkResult.isSuccess()) {
            return checkResult;
        }
        // 转换成User对象
        User registerUser = new User();

        registerUserdto.setPassword(bCryptPasswordEncoder.encode(registerUserdto.getPassword()));
        BeanUtils.copyProperties(registerUserdto, registerUser);
        registerUser.setRole(CONSTANT.ROLE.CUSTOMER);
        registerUser.setCreateTime(LocalDateTime.now());
        registerUser.setUpdateTime(LocalDateTime.now());

        // 插入数据库，若失败则返回错误
        int rows = userMapper.insert(registerUser);
        if (rows == 0) {
            return CommonResponse.createForErrorMessage("插入数据库失败");
        }

        // 成功则构造vo对象，并向前端返回
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(registerUser, userVO);
        return CommonResponse.createForSuccess("注册成功", userVO);
    }

    @Override
    public CommonResponse<Object> getLoginUserInfo() {
        HttpSession session = request.getSession();
        Object data = session.getAttribute(CONSTANT.Login_User);
        if (data == null) {
            return CommonResponse.createForErrorMessage("用户未登录");
        }
        return CommonResponse.createForSuccess(data);
    }

    @Override
    public CommonResponse<String> getForgetQuestion(String username) {
        CommonResponse<Object> checkResult = checkField(CONSTANT.USER_FIELD.USERNAME,username);
        if (checkResult.isSuccess()) {
            return CommonResponse.createForErrorMessage("用户名不存在");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CONSTANT.USER_FIELD.USERNAME,username);
        String question = userMapper.selectOne(queryWrapper).getQuestion();

        if (StringUtils.isNotEmpty(question)) {
            return CommonResponse.createForSuccess(question);
        }
        return CommonResponse.createForErrorMessage("该用户名不存在或没有设置忘记密码问题");
    }

    @Override
    public CommonResponse<String> checkForgetAnswer(String username, String question, String answer) {
        // 查找数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CONSTANT.USER_FIELD.USERNAME,username)
                    .eq(CONSTANT.USER_FIELD.QUESTION,question)
                    .eq(CONSTANT.USER_FIELD.ANSWER,answer);
        Long rows = userMapper.selectCount(queryWrapper);
        // 若找到数据则视为答案正确，返回token
        if (rows > 0) {
            //使用UUID生成一段token字符串
            String token = username + UUID.randomUUID();
            //将生成的token放入本地CaffeineCache缓存中，用户名为key，token为value，失效时间为5分钟
            localCache.put(username,token);
            //输出日志，记录存入缓存成功，打印时间
            log.info("Put into localCache: ({}, {}) {}",username, token, LocalDateTime.now());
            return CommonResponse.createForSuccess(token);
        }
        // 若没有找到返回错误
        return CommonResponse.createForErrorMessage("答案错误");
    }

    @Override
    public CommonResponse<String> resetForgetPassword(String username, String newPassword, String forgetToken) {
        // 判断用户名是否存在
        CommonResponse<Object> checkResult = checkField(CONSTANT.USER_FIELD.USERNAME, username);
        if (checkResult.isSuccess()) {
            return CommonResponse.createForErrorMessage("用户名不存在");
        }
        // 判断token是否存在、是否失效
        String token = localCache.getIfPresent(username);
        if (StringUtils.equals(forgetToken, token)) { // 同时判断了forgetToken、token不为空，以及二者相同
            // 输出日志
            log.info("Get token from localCache: ({}, {}) {}",username, token, LocalDateTime.now());
            // token未失效，重置密码
            User user = new User();
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CONSTANT.USER_FIELD.USERNAME, username);

            String md5Password = bCryptPasswordEncoder.encode(newPassword);
            updateWrapper.set(CONSTANT.USER_FIELD.PASSWORD, md5Password);

            int rows = userMapper.update(user, updateWrapper);
            if (rows > 0) {
                return CommonResponse.createForSuccess();
            }
            // 数据库更新失败，重置密码失败
            return CommonResponse.createForErrorMessage("数据库更新失败，重置密码失败，请重新获取token");
        }
        // token失效，返回错误
        return CommonResponse.createForErrorMessage("token不存在或已过期");
    }

    @Override
    public CommonResponse<String> resetPassword(String oldPassword, String newPassword) {
        if (getLoginUserInfo().isSuccess()) {
            HttpSession session = request.getSession();
            Object data = session.getAttribute(CONSTANT.Login_User);
            User user = new User();
            BeanUtils.copyProperties(data, user);
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CONSTANT.USER_FIELD.USERNAME,user.getUsername());

            if (bCryptPasswordEncoder.matches(oldPassword, userMapper.selectOne(updateWrapper).getPassword())) {
                String md5Password = bCryptPasswordEncoder.encode(newPassword);
                updateWrapper.set(CONSTANT.USER_FIELD.PASSWORD,md5Password);

                int rows = userMapper.update(user,updateWrapper);
                if (rows > 0) {
                    return CommonResponse.createForSuccess();
                }
                return CommonResponse.createForErrorMessage("数据库更新失败，重置密码失败");
            }
            return CommonResponse.createForErrorMessage("旧密码错误，请重新输入");
        }
        return CommonResponse.createForErrorMessage("重设密码失败，用户未登录");
    }

    @Override
    public CommonResponse<Object> updateUserInfo(UpdateUserDTO updateUserDTO) {
        if (getLoginUserInfo().isSuccess()) {
            HttpSession session = request.getSession();
            Object data = session.getAttribute(CONSTANT.Login_User);
            User loginUser = new User();
            BeanUtils.copyProperties(data, loginUser);
            // 校验除去当前登录的用户外的信息
            CommonResponse<Object> checkResult =
                    checkField(CONSTANT.USER_FIELD.USERNAME,updateUserDTO.getUsername());
            if (!(checkResult.isSuccess() || StringUtils.equals(updateUserDTO.getUsername(), loginUser.getUsername()))) {
                return checkResult;
            }
            checkResult = checkField(CONSTANT.USER_FIELD.EMAIL,updateUserDTO.getEmail());
            if (!(checkResult.isSuccess() || StringUtils.equals(updateUserDTO.getEmail(), loginUser.getEmail()))) {
                return checkResult;
            }
            checkResult = checkField(CONSTANT.USER_FIELD.PHONE,updateUserDTO.getPhone());
            if (!(checkResult.isSuccess() || StringUtils.equals(updateUserDTO.getPhone(), loginUser.getPhone()))) {
                return checkResult;
            }

            BeanUtils.copyProperties(updateUserDTO, loginUser);
            UpdateWrapper<User> updateWrapper = new UpdateWrapper();
            updateWrapper.eq(CONSTANT.USER_FIELD.USERNAME,updateUserDTO.getUsername());

            int rows = userMapper.update(loginUser,updateWrapper);
            if (rows > 0) {
                return CommonResponse.createForSuccess();
            }
            return CommonResponse.createForErrorMessage("数据库更新失败，重置密码失败");
        }
        return CommonResponse.createForErrorMessage("修改个人信息失败，用户未登录");
    }
}
