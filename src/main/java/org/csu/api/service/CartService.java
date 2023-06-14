package org.csu.api.service;

import org.csu.api.common.CommonResponse;

public interface CartService {

    // 添加一个购物车项
    CommonResponse<Object> addCart(Integer userId, Integer productId, Integer quantity);
    // 更新一个购物车项
    CommonResponse<Object> updateCart(Integer userId, Integer productId, Integer quantity);
    // 删除一个购物车项
    CommonResponse<Object> deleteCart(Integer userId, String productIds);
    // 获取购物车列表
    CommonResponse<Object> list(Integer userId);
    // 全选购物车
    CommonResponse<Object> setAllChecked(Integer userId);
    // 全不选购物车
    CommonResponse<Object> setAllUnchecked(Integer userId);
    // 选中一项购物车
    CommonResponse<Object> setCartItemChecked(Integer userId, Integer productId);
    // 取消选中一项购物车
    CommonResponse<Object> setCartItemUnchecked(Integer userId, Integer productId);
    // 获取购物车总数
    CommonResponse<Object> getCartCount(Integer userId);
}
