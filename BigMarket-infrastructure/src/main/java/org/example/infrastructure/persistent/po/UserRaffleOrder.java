package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class UserRaffleOrder {
    /** user id */
    private String id;
    /** userId */
    private String userId;
    /** activityId */
    private Long activityId;
    /** activity name */
    private String activityName;
    /** strategyId */
    private Long strategyId;
    /** orderId */
    private String orderId;
    /** order time */
    private Date orderTime;
    /** order status:create,used,cancel */
    private String orderState;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

}
