package org.example.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.activity.model.vo.UserRaffleOrderStatusVO;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRaffleOrderEntity {

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
    private UserRaffleOrderStatusVO orderStatus;
}
