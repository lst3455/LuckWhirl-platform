package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class RaffleActivityOrder {

    /** auto increasing id */
    private Long id;
    /** user id */
    private String userId;
    /** sku */
    private Long sku;
    /** activity id */
    private Long activityId;
    /** activity name */
    private String activityName;
    /** raffle strategy id */
    private Long strategyId;
    /** order id */
    private String orderId;
    /** order time */
    private Date orderTime;
    /** total amount */
    private Integer totalAmount;
    /** day amount */
    private Integer dayAmount;
    /** month amount */
    private Integer monthAmount;
    /** point needed to redeem this sku */
    private Integer pointAmount;
    /** order status */
    private String status;
    /** avoid duplicate */
    private String outBusinessNo;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

}