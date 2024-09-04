package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class RaffleActivityAccountDay {

    /** auto increasing id */
    private Long id;
    /** user id */
    private String userId;
    /** activity id */
    private Long activityId;
    /** day (yyyy-mm-dd) */
    private String day;
    /** day amount */
    private Integer dayAmount;
    /** day remain */
    private Integer dayRemain;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;
}
