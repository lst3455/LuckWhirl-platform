package org.example.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleStrategyRuleWeightAwardResponseDTO {

    private Integer ruleWeightCount;

    private Integer userTotalRaffleCount;

    private List<?> strategyAwardList;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAward{
        /** award id */
        private Long awardId;
        /** award award title */
        private String awardTitle;
    }
}
