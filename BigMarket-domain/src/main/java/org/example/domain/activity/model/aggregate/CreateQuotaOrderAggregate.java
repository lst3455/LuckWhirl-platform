package org.example.domain.activity.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.activity.model.entity.ActivityOrderEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuotaOrderAggregate {

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
