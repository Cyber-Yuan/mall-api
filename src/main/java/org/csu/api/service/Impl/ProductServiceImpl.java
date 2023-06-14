package org.csu.api.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseStatus;
import org.csu.api.domain.Category;
import org.csu.api.domain.Product;
import org.csu.api.persistence.CategoryMapper;
import org.csu.api.persistence.ProductMapper;
import org.csu.api.service.CategoryService;
import org.csu.api.service.ProductService;
import org.csu.api.util.ListBeanUtilsForPage;
import org.csu.api.vo.ProductDetailVO;
import org.csu.api.vo.ProductListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryService categoryService;

    @Override
    public CommonResponse<ProductDetailVO> getProductDetail(Integer productId) {
        // to-do:判断productId是否为空

        // 判断商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return CommonResponse.createForErrorMessage("商品不存在或已删除");
        }

        // 判断商品是否下架
        if (product.getStatus() == CONSTANT.PRODUCT_STATUS.OFF_SALE) {
            return CommonResponse.createForErrorMessage("商品已下架");
        }

        // 商品存在，返回VO
        ProductDetailVO productDetailVO = ProductToProductDetailVO(product);
        return CommonResponse.createForSuccess(productDetailVO);
    }

    @Override
    public CommonResponse<Page<ProductListVO>> getProductList(Integer categoryId,String keyword,String orderBy,int pageNum,int pageSize) {
        // 判断categoryId和keyword是否都为空，若是，则返回参数错误
        if (StringUtils.isEmpty(keyword) && categoryId == null) {
            return CommonResponse.createForErrorMessage(
                    ResponseStatus.ARGUMENT_INVALID.getCode(),
                    ResponseStatus.ARGUMENT_INVALID.getDescription());
        }

        // 在数据库中根据categoryId查找
        Category category = categoryMapper.selectById(categoryId);
        // categoryId非空而keyword为空，但是categoryId没有在数据库中找到，说明该商品不存在或其他问题
        if (category == null && StringUtils.isEmpty(keyword)) {
            return CommonResponse.createForErrorMessage("该类别不存在或其他错误");
        }

        // 建立查询条件
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();

        // 若categoryId非空，在查询条件中加入categoryId限制
        if (categoryId != null) {
            // 需要递归
            List<Integer> categoryIdList = categoryService.getCategoryAndAllChildren(categoryId);
            if (categoryIdList != null) {
                queryWrapper.in("category_id", categoryIdList);
            }
        }

        // 若keyword非空，在查询条件中加入keyword的模糊匹配限制
        if (StringUtils.isNotEmpty(keyword)) {
            queryWrapper.like("name", "%"+keyword+"%");
        }

        // 若排序条件非空，在查询条件中加入排序限制
        if (StringUtils.isNotEmpty(orderBy)) {
            if (StringUtils.equals(orderBy,CONSTANT.PRODUCT_ORDER_BY_PRICE_ASC)) {
                queryWrapper.orderByAsc("price");
            } else if (StringUtils.equals(orderBy,CONSTANT.PRODUCT_ORDER_BY_PRICE_DESC)) {
                queryWrapper.orderByDesc("price");
            }
        }

        // 创建分页对象，根据传入参数设置分页条件后进行查询
        Page<Product> result = new Page<>();
        result.setCurrent(pageNum);
        result.setSize(pageSize);
        result = productMapper.selectPage(result,queryWrapper);
        result.setTotal(result.getRecords().size());

        Page<ProductListVO> exactResult = ListBeanUtilsForPage.copyPageList(result, ProductListVO::new, (product, productListVO) -> {
            productListVO.setImageServer("to do");
        });

        // 返回CommonResponse，范型为分页对象
        return CommonResponse.createForSuccess(exactResult);
    }

    private ProductDetailVO ProductToProductDetailVO(Product product) {
        ProductDetailVO productDetailVO = new ProductDetailVO();
        BeanUtils.copyProperties(product, productDetailVO);

        Category parentCategory = categoryMapper.selectById(product.getCategoryId());
        Integer parentCategoryId = parentCategory.getParentId();
        productDetailVO.setParentCategoryId(parentCategoryId);

        productDetailVO.setImageServer("to do");
        return productDetailVO;
    }
}
