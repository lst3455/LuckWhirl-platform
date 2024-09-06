package org.example.domain.activity.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.activity.model.entity.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePartakeOrderAggregate {

    /** user id */
    private String userId;
    /** activity id */
    private Long activityId;
    /** activityAccountEntity */
    private ActivityAccountEntity activityAccountEntity;
    /** is activityAccountMonth exist */
    private boolean isExistActivityAccountMonth = true;
    /** activityAccountMonthEntity */
    private ActivityAccountMonthEntity activityAccountMonthEntity;
    /** is activityAccountDay exist */
    private boolean isExistActivityAccountDay = true;
    /** activityAccountDayEntity */
    private ActivityAccountDayEntity activityAccountDayEntity;
    /** userRaffleOrderEntity */
    private UserRaffleOrderEntity userRaffleOrderEntity;

}
