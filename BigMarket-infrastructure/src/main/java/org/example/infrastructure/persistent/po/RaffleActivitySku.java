package org.example.infrastructure.persistent.po;

import lombok.Data;

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
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

}
