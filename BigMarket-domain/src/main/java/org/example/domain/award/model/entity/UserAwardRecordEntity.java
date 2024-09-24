package org.example.domain.award.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.award.model.vo.AwardStatusVO;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAwardRecordEntity {
    /** user id */
    private String userId;
    /** activity id */
    private Long activityId;
    /** raffle strategy id */
    private Long strategyId;
    /** raffle orderId[作为幂等使用] */
    private String orderId;
    /** awardId */
    private Long awardId;
    /** award title */
    private String awardTitle;
    /** award getting time */
    private Date awardTime;
    /** award status；create,completed */
    private AwardStatusVO awardStatus;

    /** award config, for delivery point, do not exist in PO */
    private String awardConfig;
}
