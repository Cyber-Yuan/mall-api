package org.csu.api.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.csu.api.common.CommonResponse;
import org.csu.api.domain.Product;
import org.csu.api.service.ProductService;
import org.csu.api.vo.ProductDetailVO;
import org.csu.api.vo.ProductListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/product/detail")
    public CommonResponse<ProductDetailVO> getProductDetail(
            @Valid @NotBlank(message = "商品ID不能为空") Integer productId) {
        return productService.getProductDetail(productId);
    }

    @GetMapping("/product/list")
    public CommonResponse<Page<ProductListVO>> getProductList(@RequestParam(required = false) Integer categoryId,
                                                              @RequestParam(required = false) String keyword,
                                                              @RequestParam(defaultValue = "") String orderBy,
                                                              @RequestParam(defaultValue = "1") int pageNum,
                                                              @RequestParam(defaultValue = "5") int pageSize) {
        return productService.getProductList(categoryId, keyword, orderBy, pageNum, pageSize);
    }
}
