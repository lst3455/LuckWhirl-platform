package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class UserBehaviorRebateOrder {
    /** auto increasing id */
    private Long id;
    /** user id */
    private String userId;
    /** order id */
    private String orderId;
    /** behavior type（sign, or others） */
    private String behaviorType;
    /** describe */
    private String rebateDesc;
    /** rebate type（sku, integral） */
    private String rebateType;
    /** configuration */
    private String rebateConfig;
    /** business id */
    private String bizId;
    /** creat time */
    private Date createTime;
    /** update time */
    private Date updateTime;
}
