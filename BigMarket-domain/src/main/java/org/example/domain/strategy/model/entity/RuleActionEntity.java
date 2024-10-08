package org.example.domain.strategy.model.entity;

import lombok.*;
import org.example.domain.strategy.model.vo.RuleLogicCheckTypeVO;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleActionEntity <T extends RuleActionEntity.RaffleEntity>{

    private String code = RuleLogicCheckTypeVO.ALLOW.getCode();

    private String info = RuleLogicCheckTypeVO.ALLOW.getInfo();

    private String ruleModel;

    private T data;

    public static class RaffleEntity {

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RaffleBeforeEntity extends RaffleEntity {

        private Long strategyId;

        private Set<Long> ruleWeightValueKey;

        private Long awardId;

        private Long userRaffleTimes;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RaffleCentreEntity extends RaffleEntity {

        private Long strategyId;

        private Set<Long> ruleWeightValueKey;

        private Long awardId;

        private Long userRaffleTimes;
    }
}
