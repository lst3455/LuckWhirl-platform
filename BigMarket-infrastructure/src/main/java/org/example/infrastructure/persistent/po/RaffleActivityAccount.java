package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class RaffleActivityAccount {

    /** auto increasing id */
    private Long id;

    /** user id */
    private String userId;

    /** activity id */
    private Long activityId;

    /** total amount */
    private Integer totalAmount;

    /** total remain */
    private Integer totalRemain;

    /** day amount */
    private Integer dayAmount;

    /** day remain */
    private Integer dayRemain;

    /** month amount */
    private Integer monthAmount;

    /** month remain */
    private Integer monthRemain;

    /** create time */
    private Date createTime;

    /** update time */
    private Date updateTime;

}
