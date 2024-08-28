package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class RaffleActivityAccountFlow {

    /** auto increasing id */
    private Integer id;

    /** user id */
    private String userId;

    /** activity id */
    private Long activityId;

    /** total amount */
    private Integer totalAmount;

    /** day amount */
    private Integer dayAmount;

    /** month amount */
    private Integer monthAmount;

    /** flow id */
    private String flowId;

    /**  flow resource trace（activity、sale、redeem、free）*/
    private String flowChannel;

    /** business id（外部透传，活动ID、订单ID）*/
    private String bizId;

    /** create time */
    private Date createTime;

    /** update time */
    private Date updateTime;

}

