package org.csu.api.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Integer ID;
    private BigInteger orderNo;
    private Integer userId;
    private BigDecimal paymentPrice;
    private Integer paymentType;
    private Integer postage;
    private Integer status;
    private LocalDateTime paymentTime;
    private LocalDateTime sendTime;
    private LocalDateTime endTime;
    private LocalDateTime closeTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private AdressVO adressVO;
    private List<OrderItemVO> orderItemVOList;
}
