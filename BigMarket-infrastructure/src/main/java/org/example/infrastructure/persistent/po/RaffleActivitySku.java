package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RaffleActivitySku {

    /** auto increasing id */
    private Long id;
    /** sku */
    private Long sku;
    /** activity id */
    private Long activityId;
    /** activity amount id */
    private Long activityAmountId;
    /** stock amount */
    private Integer stockAmount;
    /** stock remain */
    private Integer stockRemain;
    /** point needed to redeem this sku */
    private BigDecimal pointAmount;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

}
