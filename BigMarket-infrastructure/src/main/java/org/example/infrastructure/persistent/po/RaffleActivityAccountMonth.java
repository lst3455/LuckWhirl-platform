package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class RaffleActivityAccountMonth {
    /** auto increasing id */
    private Long id;
    /** user id */
    private String userId;
    /** activity id */
    private Long activityId;
    /** day (yyyy-mm-dd) */
    private String month;
    /** day amount */
    private Integer monthAmount;
    /** day remain */
    private Integer monthRemain;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;
}
