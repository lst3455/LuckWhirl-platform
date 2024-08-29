package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class RaffleActivity {

    /** auto increasing id */
    private Long id;
    /** activity id */
    private Long activityId;
    /** activity name */
    private String activityName;
    /** activity describe */
    private String activityDesc;
    /** activity start date */
    private Date beginDateTime;
    /** activity end date */
    private Date endDateTime;
    /** stock amount */
    private Integer stockAmount;
    /** stock remain */
    private Integer stockRemain;
    /** times of attending activity */
    private Long activityAmountId;
    /** raffle strategy id */
    private Long strategyId;
    /** activity status */
    private String status;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

}
