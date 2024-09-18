package org.example.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityAccountMonthEntity {

    /** user id */
    private String userId;
    /** activity id */
    private Long activityId;
    /** day (yyyy-mm-dd) */
    private String month;
    /** day amount */
    private Integer monthAmount;
    /** day remain */
    private Integer monthRemain;
}
