package org.example.infrastructure.persistent.po;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserPointOrder {
    /** auto increasing id */
    private Long id;
    /** user id */
    private String userId;
    /** order id */
    private String orderId;
    /** trade name */
    private String tradeName;
    /** trade type */
    private String tradeType;
    /** trade amount */
    private BigDecimal tradeAmount;
    /** avoid duplicate */
    private String outBusinessNo;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

}
