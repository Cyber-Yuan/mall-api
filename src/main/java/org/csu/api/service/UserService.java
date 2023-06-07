package org.csu.api.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.csu.api.common.CommonResponse;
import org.csu.api.domain.User;
import org.csu.api.dto.UpdateUserDTO;
import org.csu.api.dto.loginUserDTO;
import org.csu.api.dto.registerUserDTO;
import org.csu.api.vo.UserVO;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserService {

    CommonResponse<UserVO> getLoginUser(loginUserDTO loginDto);
    CommonResponse<Object> checkField(String fieldName, String fieldValue);
    CommonResponse<Object> register(registerUserDTO registerUserdto);
    CommonResponse<Object> getLoginUserInfo();
    CommonResponse<String> getForgetQuestion(String username);
    CommonResponse<String> checkForgetAnswer(String username, String question, String answer);
    CommonResponse<String> resetForgetPassword(String username, String newPassword, String forgetToken);
    CommonResponse<String> resetPassword(String oldPassword, String newPassword);
    CommonResponse<Object> updateUserInfo(UpdateUserDTO updateUserDTO);
}
