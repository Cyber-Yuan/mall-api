package org.csu.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.csu.api.common.CommonResponse;
import org.csu.api.domain.Product;
import org.csu.api.vo.ProductDetailVO;
import org.csu.api.vo.ProductListVO;
import org.springframework.web.bind.annotation.RequestParam;

public interface ProductService {
    CommonResponse<ProductDetailVO> getProductDetail(Integer productId);
    CommonResponse<Page<Product>> getProductList(Integer categoryId, String keyword, String orderBy, int pageNum, int pageSize);
}
