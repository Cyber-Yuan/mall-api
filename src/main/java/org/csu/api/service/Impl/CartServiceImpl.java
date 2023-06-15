package org.csu.api.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseStatus;
import org.csu.api.domain.CartItem;
import org.csu.api.domain.Product;
import org.csu.api.persistence.CartItemMapper;
import org.csu.api.persistence.ProductMapper;
import org.csu.api.service.CartService;
import org.csu.api.util.BigDecimalUtil;
import org.csu.api.util.ListBeanUtils;
import org.csu.api.vo.CartItemVO;
import org.csu.api.vo.CartVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public CommonResponse<Object> addCart(Integer userId, Integer productId, Integer quantity) {
        //校验：userId不会错，判断productId是否存在
        Product product = productMapper.selectById(productId);
        if(product == null) {
            return CommonResponse.createForErrorMessage("商品不存在或已下架");
        }

        //productId存在，查询该用户的原购物车中是否包含该商品
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId).eq("user_id",userId);
        CartItem cartItem = cartItemMapper.selectOne(queryWrapper);

        //如果包含，修改数量
        if (cartItem == null) {
            //如果不包含，新建购物车项
            cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
            cartItem.setChecked(CONSTANT.CART_ITEM_STATUS.CHECKED);
            cartItem.setCreateTime(LocalDateTime.now());
            cartItem.setUpdateTime(LocalDateTime.now());
            cartItemMapper.insert(cartItem);
        }else {
            //如果包含，修改数量
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("product_id", productId).eq("user_id",userId);
            cartItemMapper.update(cartItem, updateWrapper);
        }

        //判断数量是否超过库存，并读取用户的完整购物车列表
        CartVO cartVO = getCartVOAndCheckStock(userId);

        return CommonResponse.createForSuccess(cartVO);
    }

    @Override
    public CommonResponse<Object> updateCart(Integer userId, Integer productId, Integer quantity) {
        //校验：userId不会错，判断productId是否存在
        Product product = productMapper.selectById(productId);
        if(product == null) {
            return CommonResponse.createForErrorMessage("商品不存在或已下架");
        }

        //productId存在，查询该用户的原购物车中是否包含该商品
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId).eq("user_id",userId);
        CartItem cartItem = cartItemMapper.selectOne(queryWrapper);

        // 判断要更新的条目是否为null
        if(cartItem == null) {
            return CommonResponse.createForErrorMessage("更新的条目不存在");
        }else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("product_id", productId);
            cartItemMapper.update(cartItem, updateWrapper);
        }

        //判断数量是否超过库存，并读取用户的完整购物车列表
        CartVO cartVO = getCartVOAndCheckStock(userId);

        return CommonResponse.createForSuccess(cartVO);
    }

    @Override
    public CommonResponse<Object> deleteCart(Integer userId, String productIds) {
        String[] productIdArray = productIds.split(",");
        for(String s:productIdArray) {
            Integer productId = null;
            try {
                productId = Integer.valueOf(s);
            } catch (NumberFormatException e) {
                return CommonResponse.createForErrorMessage(
                        ResponseStatus.ARGUMENT_INVALID.getCode(),ResponseStatus.ARGUMENT_INVALID.getDescription());
            }
            if (productId != null) {
                QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("product_id", productId).eq("user_id",userId);
                cartItemMapper.delete(queryWrapper);
            }
        }
        CartVO cartVO = getCartVOAndCheckStock(userId);

        return CommonResponse.createForSuccess(cartVO);
    }

    @Override
    public CommonResponse<Object> list(Integer userId) {
        return CommonResponse.createForSuccess(getCartVOAndCheckStock(userId));
    }

    @Override
    public CommonResponse<Object> setAllChecked(Integer userId) {
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<CartItem> cartItemList= cartItemMapper.selectList(queryWrapper);
        for (CartItem cartItem:cartItemList) {
            UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("user_id", userId);
            updateWrapper.eq("product_id", cartItem.getProductId());
            updateWrapper.set("checked", 1);
            cartItemMapper.update(cartItem, updateWrapper);
        }
        CartVO cartVO = getCartVOAndCheckStock(userId);
        return CommonResponse.createForSuccess(cartVO);
    }

    @Override
    public CommonResponse<Object> setAllUnchecked(Integer userId) {
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<CartItem> cartItemList= cartItemMapper.selectList(queryWrapper);
        for (CartItem cartItem:cartItemList) {
            UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("user_id", userId);
            updateWrapper.eq("product_id", cartItem.getProductId());
            updateWrapper.set("checked", 0);
            cartItemMapper.update(cartItem, updateWrapper);
        }
        CartVO cartVO = getCartVOAndCheckStock(userId);
        return CommonResponse.createForSuccess(cartVO);
    }

    @Override
    public CommonResponse<Object> setCartItemChecked(Integer userId, Integer productId) {
        UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId).eq("product_id", productId);
        updateWrapper.set("checked", 1);
        CartItem cartItem = new CartItem();
        cartItemMapper.update(cartItem, updateWrapper);
        CartVO cartVO = getCartVOAndCheckStock(userId);
        return CommonResponse.createForSuccess(cartVO);
    }

    @Override
    public CommonResponse<Object> setCartItemUnchecked(Integer userId, Integer productId) {
        UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId).eq("product_id", productId);
        updateWrapper.set("checked", 0);
        CartItem cartItem = new CartItem();
        cartItemMapper.update(cartItem, updateWrapper);
        CartVO cartVO = getCartVOAndCheckStock(userId);
        return CommonResponse.createForSuccess(cartVO);
    }

    @Override
    public CommonResponse<Object> getCartCount(Integer userId) {
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<CartItem> cartItemList= cartItemMapper.selectList(queryWrapper);
        int totalNum = cartItemList.size();
        return CommonResponse.createForSuccess(totalNum);
    }

    private CartVO getCartVOAndCheckStock(Integer userId) {
        // 从数据库中查询出该用户的购物车信息，返回list
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<CartItem> cartItemList = cartItemMapper.selectList(queryWrapper);

        List<CartItemVO> cartItemVOList = Lists.newArrayList();

        AtomicReference<BigDecimal> cartTotalPrice = new AtomicReference<>(new BigDecimal(0));
        AtomicReference<Boolean> allSelected = new AtomicReference<>(true);

        // 通过ListBeanUtils返回cartItemVOList,在回调函数中处理库存
        if(!CollectionUtils.isEmpty(cartItemList)) {
            cartItemVOList = ListBeanUtils.copyListProperties(cartItemList, CartItemVO::new, (cartItem, cartItemVO) -> {
                Product product = productMapper.selectById(cartItem.getProductId());
                if(product != null) {
//                    BeanUtils.copyProperties(product, cartItemVO);
                    cartItemVO.setId(cartItem.getID());
                    cartItemVO.setProductId(product.getId());
                    cartItemVO.setProductStock(product.getStock());
                    cartItemVO.setProductName(product.getName());
                    cartItemVO.setProductPrice(product.getPrice());
                    cartItemVO.setProductSubtitle(product.getSubtitle());
                    cartItemVO.setProductMainImage(product.getMainImage());
                    // 统一库存处理
                    if(cartItem.getQuantity() > product.getStock()) {
                        UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
                        updateWrapper.eq("id", cartItem.getID()).eq("user_id",userId);
                        updateWrapper.set("quantity", product.getStock());
                        cartItemMapper.update(cartItem, updateWrapper);
                        cartItemVO.setQuantity(product.getStock());
                        cartItemVO.setCheckStock(true);
                    }else {
                        cartItemVO.setCheckStock(false);
                    }
                    BigDecimal productTotalPrice = BigDecimalUtil.multiply(cartItemVO.getQuantity(),product.getPrice().doubleValue());
                    cartItemVO.setProductTotalPrice(productTotalPrice);
                    // 计算购物车总价格以及是否全选
                    if(cartItem.getChecked() == 1) {
                        cartTotalPrice.set(cartTotalPrice.get().add(productTotalPrice));
                    } else {
                        allSelected.set(false);
                    }
                }
            });
        }
        CartVO cartVO = new CartVO();
        cartVO.setCartItemVOList(cartItemVOList);
        cartVO.setCartTotalPrice(cartTotalPrice.get());
        cartVO.setAllSelected(allSelected.get());
        cartVO.setProductImageServer("image server");
        return cartVO;
    }
}
