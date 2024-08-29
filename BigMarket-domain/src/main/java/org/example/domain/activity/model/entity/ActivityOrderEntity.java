package org.example.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityOrderEntity {

    /** user id */
    private String userId;
    /** sku */
    private Long sku;
    /** activity id */
    private Long activityId;
    /** activity name */
    private String activityName;
    /** raffle strategy id */
    private Long strategyId;
    /** order id */
    private String orderId;
    /** order time */
    private Date orderTime;
    /** total amount */
    private Integer totalAmount;
    /** day amount */
    private Integer dayAmount;
    /** month amount */
    private Integer monthAmount;
    /** order status */
    private String status;

}