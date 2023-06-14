package org.csu.api.controller.front;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseStatus;
import org.csu.api.dto.DeleteCartDTO;
import org.csu.api.dto.PostCartDTO;
import org.csu.api.service.CartService;
import org.csu.api.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/cart/add")
    public CommonResponse<Object> addCart(@Valid @RequestBody PostCartDTO postCartDTO,
                                          HttpSession session) {
        // 判断用户是否登录，session的方式
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.Login_User);
        if (loginUser == null) {
            return CommonResponse.createForErrorMessage(ResponseStatus.NEED_LOGIN.getCode(),
                    ResponseStatus.NEED_LOGIN.getDescription());
        }
        return cartService.addCart(loginUser.getId(), postCartDTO.getProductID(), postCartDTO.getQuantity());
    }

    @PostMapping("/cart/update")
    public CommonResponse<Object> updateCart(@Valid @RequestBody PostCartDTO postCartDTO,
                                          HttpSession session) {
        // 判断用户是否登录，session的方式
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.Login_User);
        if (loginUser == null) {
            return CommonResponse.createForErrorMessage(ResponseStatus.NEED_LOGIN.getCode(),
                    ResponseStatus.NEED_LOGIN.getDescription());
        }
        return cartService.updateCart(loginUser.getId(), postCartDTO.getProductID(), postCartDTO.getQuantity());
    }

    @PostMapping("/cart/delete")
    public CommonResponse<Object> deleteCart(@Valid @RequestBody DeleteCartDTO deleteCartDTO,
                                             HttpSession session) {
        // 判断用户是否登录，session的方式
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.Login_User);
        if (loginUser == null) {
            return CommonResponse.createForErrorMessage(ResponseStatus.NEED_LOGIN.getCode(),
                    ResponseStatus.NEED_LOGIN.getDescription());
        }
        return cartService.deleteCart(loginUser.getId(), deleteCartDTO.getProductIds());
    }

    @GetMapping("/cart/list")
    public CommonResponse<Object> getCartList(HttpSession session) {
        // 判断用户是否登录，session的方式
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.Login_User);
        if (loginUser == null) {
            return CommonResponse.createForErrorMessage(ResponseStatus.NEED_LOGIN.getCode(),
                    ResponseStatus.NEED_LOGIN.getDescription());
        }
        return cartService.list(loginUser.getId());
    }

    @PostMapping("/cart/set_all_checked")
    public CommonResponse<Object> setAllChecked(HttpSession session) {
        // 判断用户是否登录，session的方式
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.Login_User);
        if (loginUser == null) {
            return CommonResponse.createForErrorMessage(ResponseStatus.NEED_LOGIN.getCode(),
                    ResponseStatus.NEED_LOGIN.getDescription());
        }
        return cartService.setAllChecked(loginUser.getId());
    }

    @PostMapping("/cart/set_all_unchecked")
    public CommonResponse<Object> setAllUnchecked(HttpSession session) {
        // 判断用户是否登录，session的方式
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.Login_User);
        if (loginUser == null) {
            return CommonResponse.createForErrorMessage(ResponseStatus.NEED_LOGIN.getCode(),
                    ResponseStatus.NEED_LOGIN.getDescription());
        }
        return cartService.setAllUnchecked(loginUser.getId());
    }

    @PostMapping("/cart/set_cart_item_checked")
    public CommonResponse<Object> setCartItemChecked(HttpSession session, Integer productId) {
        // 判断用户是否登录，session的方式
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.Login_User);
        if (loginUser == null) {
            return CommonResponse.createForErrorMessage(ResponseStatus.NEED_LOGIN.getCode(),
                    ResponseStatus.NEED_LOGIN.getDescription());
        }
        return cartService.setCartItemChecked(loginUser.getId(), productId);
    }

    @PostMapping("/cart/set_cart_item_unchecked")
    public CommonResponse<Object> setCartItemUnchecked(HttpSession session, Integer productId) {
        // 判断用户是否登录，session的方式
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.Login_User);
        if (loginUser == null) {
            return CommonResponse.createForErrorMessage(ResponseStatus.NEED_LOGIN.getCode(),
                    ResponseStatus.NEED_LOGIN.getDescription());
        }
        return cartService.setCartItemUnchecked(loginUser.getId(), productId);
    }

    @PostMapping("/cart/get_cart_count")
    public CommonResponse<Object> getCartCount(HttpSession session) {
        // 判断用户是否登录，session的方式
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.Login_User);
        if (loginUser == null) {
            return CommonResponse.createForErrorMessage(ResponseStatus.NEED_LOGIN.getCode(),
                    ResponseStatus.NEED_LOGIN.getDescription());
        }
        return cartService.getCartCount(loginUser.getId());
    }
}
