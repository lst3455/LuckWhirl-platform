package org.example.domain.activity.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.activity.model.entity.ActivityAccountEntity;
import org.example.domain.activity.model.entity.ActivityOrderEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderAggregate {

    private ActivityAccountEntity activityAccountEntity;

    private ActivityOrderEntity activityOrderEntity;

}
