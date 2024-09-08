package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class UserAwardRecord {
    /** auto increasing id */
    private String id;
    /** user id */
    private String userId;
    /** activity id */
    private Long activityId;
    /** raffle strategy id */
    private Long strategyId;
    /** raffle orderId[作为幂等使用] */
    private String orderId;
    /** awardId */
    private Integer awardId;
    /** award title */
    private String awardTitle;
    /** award getting time */
    private Date awardTime;
    /** award status；create,completed */
    private String awardStatus;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

}
