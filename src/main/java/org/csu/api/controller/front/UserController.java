package org.csu.api.controller.front;

import jakarta.validation.constraints.NotBlank;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.domain.User;
import org.csu.api.dto.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.csu.api.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.csu.api.service.UserService;

import java.util.Enumeration;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/login")
    public CommonResponse<UserVO> loginUser(@Valid @RequestBody loginUserDTO loginDto,
                                          HttpSession session) {
        CommonResponse<UserVO> result = userService.getLoginUser(loginDto);
        System.out.println();
        if (result.isSuccess()) {
            session.setAttribute(CONSTANT.Login_User, result.getData());
        }
        return result;
    }

    @PostMapping("/user/check_field")
    public CommonResponse<Object> checkField(@Valid @RequestBody CheckUserFieldDTO checkUserFieldDTO) {
        return userService.checkField(checkUserFieldDTO.getFieldName(), checkUserFieldDTO.getFieldValue());
    }

    @PostMapping("/user/register")
    public  CommonResponse<Object> registerUser(@Valid @RequestBody registerUserDTO registerUserdto) {
        // DTO在控制器层就截止了
        CommonResponse<Object> result = userService.register(registerUserdto);
//        System.out.println("This is register.");
        return result;
        // 所以说从service返回来的userVO能够直接转换成json格式是@RestController这个注解的作用，
        // 而@RestController 是@Controller和@ResponseBody 的结合
    }

    @PostMapping("/user/get_user_detail")
    public CommonResponse<Object> getLoginUserInfo() {
        return userService.getLoginUserInfo();
    }

    @PostMapping("/user/get_forget_question")
    public CommonResponse<String> getForgetQuestion(@Valid @NotBlank(message = "用户名不能为空") String username) {
        return userService.getForgetQuestion(username);
    }

    @PostMapping("/user/check_forget_answer")
    public CommonResponse<String> checkForgetAnswer(@Valid @RequestBody CheckAnswerUserDTO checkAnswerUserDTO) {
        return userService.checkForgetAnswer(
                checkAnswerUserDTO.getUsername(),checkAnswerUserDTO.getQuestion(),checkAnswerUserDTO.getAnswer());
    }

    @PostMapping("/user/reset_forget_password")
    public CommonResponse<String> resetForgetPassword(@Valid @RequestBody ResetUserDTO resetUserDTO) {
        return userService.resetForgetPassword(
                resetUserDTO.getUsername(),resetUserDTO.getNewPassword(),resetUserDTO.getForgetToken());
    }

    @PostMapping("/user/reset_password")
    public CommonResponse<String> resetPassword(@Valid @NotBlank(message = "旧密码不能为空") String oldPassword,
                                                       @NotBlank(message = "新密码不能为空") String newPassword) {
        return userService.resetPassword(oldPassword,newPassword);
    }

    @PostMapping("/user/update_user_info")
    public CommonResponse<Object> updateUserInfo(@Valid @RequestBody UpdateUserDTO updateUserDTO) {
        return userService.updateUserInfo(updateUserDTO);
    }

    @GetMapping("/user/logout")
    public CommonResponse<String> logout(HttpSession seesion) {
        seesion.removeAttribute(CONSTANT.Login_User);
        return CommonResponse.createForSuccess();
    }

    private User DTOToUser(registerUserDTO registerUserdto) {
        User user = new User();
        BeanUtils.copyProperties(registerUserdto, user);
        return user;
    }

}
