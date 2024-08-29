package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class RaffleActivityAmount {

    /** auto increasing id */
    private Long id;
    /** times of attending activity */
    private Long activityAmountId;
    /** total amount */
    private Integer totalAmount;
    /** day amount */
    private Integer dayAmount;
    /** month amount */
    private Integer monthAmount;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

}


