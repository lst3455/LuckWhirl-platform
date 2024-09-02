package org.example.domain.activity.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.activity.model.entity.ActivityAccountEntity;
import org.example.domain.activity.model.entity.ActivityOrderEntity;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderAggregate {

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
    /** ActivityOrderEntity */
    private ActivityOrderEntity activityOrderEntity;

}
