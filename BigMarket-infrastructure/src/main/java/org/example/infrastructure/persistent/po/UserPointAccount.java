package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserPointAccount {
    /** auto increasing id */
    private Long id;
    /** user id */
    private String userId;
    /** total credit amount */
    private BigDecimal totalAmount;
    /** available credit amount */
    private BigDecimal availableAmount;
    /** account status */
    private String accountStatus;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

}
