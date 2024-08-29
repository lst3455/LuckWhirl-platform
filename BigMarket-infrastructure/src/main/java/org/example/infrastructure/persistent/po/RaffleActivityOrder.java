package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class RaffleActivityOrder {

    /** auto increasing id */
    private Long id;
    /** user id */
    private String userId;
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
    /** order status（not_used、used、expire） */
    private String state;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

}