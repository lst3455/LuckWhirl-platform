package org.example.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.activity.model.vo.ActivityStatusVO;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityEntity {

    /** activity id */
    private Long activityId;
    /** activity name */
    private String activityName;
    /** activity describe */
    private String activityDesc;
    /** activity start date */
    private Date beginDateTime;
    /** activity end date */
    private Date endDateTime;
    /** raffle strategy id */
    private Long strategyId;
    /** activity status */
    private ActivityStatusVO status;

}
