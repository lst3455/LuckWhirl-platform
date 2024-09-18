package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class DailyBehaviorRebate {
    /** auto increasing id */
    private Long id;
    /** behavior type（sign, or others） */
    private String behaviorType;
    /** describe */
    private String rebateDesc;
    /** rebate type（sku, integral） */
    private String rebateType;
    /** configuration */
    private String rebateConfig;
    /** status（open, close） */
    private String status;
    /** creat time */
    private Date createTime;
    /** update time */
    private Date updateTime;
}
