package org.example.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRaffleOrder {
    /** user id */
    private String id;
    /** userId */
    private String userId;
    /** activityId */
    private Long activityId;
    /** activity name */
    private String activityName;
    /** strategyId */
    private Long strategyId;
    /** orderId */
    private String orderId;
    /** order time */
    private Date orderTime;
    /** order status:create,used,cancel */
    private String orderStatus;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

}
