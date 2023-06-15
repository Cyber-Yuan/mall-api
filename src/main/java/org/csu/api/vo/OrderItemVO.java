package org.csu.api.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderItemVO {
    private Integer ID;
    private Integer productId;
    private String productName;
    private String productImage;
    private BigDecimal currentPrice;

    private Integer quantity;
    private BigDecimal totalPrice;
}
