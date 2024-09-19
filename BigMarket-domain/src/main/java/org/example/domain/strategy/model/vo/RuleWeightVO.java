package org.example.domain.strategy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleWeightVO {

    private String ruleValue;

    private Long weight;

    private Set<Long> awardIdSet;

    private List<Award> awardList;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Award{

        private Long awardId;

        private String awardTitle;
    }
}
