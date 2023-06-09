package org.csu.api.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseStatus;
import org.csu.api.service.OrderService;
import org.csu.api.vo.OrderVO;
import org.csu.api.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/order/create")
    public CommonResponse<Object> createOrder(HttpSession session, Integer addressId) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.Login_User);
        if (loginUser == null) {
            return CommonResponse.createForErrorMessage(ResponseStatus.NEED_LOGIN.getCode(),
                    ResponseStatus.NEED_LOGIN.getDescription());
        }
        return orderService.createOrder(loginUser.getId(), addressId);
    }

    @GetMapping("/order/cart_item_list")
    public CommonResponse<Object> getCartItemList(HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.Login_User);
        if (loginUser == null) {
            return CommonResponse.createForErrorMessage(ResponseStatus.NEED_LOGIN.getCode(),
                    ResponseStatus.NEED_LOGIN.getDescription());
        }
        return orderService.getCartItemList(loginUser.getId());
    }

    @GetMapping("/order/detail")
    public CommonResponse<OrderVO> getDetail(HttpSession session, BigInteger orderNo) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.Login_User);
        if (loginUser == null) {
            return CommonResponse.createForErrorMessage(ResponseStatus.NEED_LOGIN.getCode(),
                    ResponseStatus.NEED_LOGIN.getDescription());
        }
        return orderService.getDetail(loginUser.getId(), orderNo);
    }

    @GetMapping("/order/list")
    public CommonResponse<Page<OrderVO>> getList(HttpSession session,
                                                 @RequestParam(defaultValue = "1") int pageNum,
                                                 @RequestParam(defaultValue = "5") int pageSize) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.Login_User);
        if (loginUser == null) {
            return CommonResponse.createForErrorMessage(ResponseStatus.NEED_LOGIN.getCode(),
                    ResponseStatus.NEED_LOGIN.getDescription());
        }
        return orderService.getList(loginUser.getId(), pageNum, pageSize);
    }

    @PostMapping("/order/cancel")
    public CommonResponse<String> cancel(HttpSession session, BigInteger orderNo) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.Login_User);
        if (loginUser == null) {
            return CommonResponse.createForErrorMessage(ResponseStatus.NEED_LOGIN.getCode(),
                    ResponseStatus.NEED_LOGIN.getDescription());
        }
        return orderService.cancel(loginUser.getId(), orderNo);
    }
}
