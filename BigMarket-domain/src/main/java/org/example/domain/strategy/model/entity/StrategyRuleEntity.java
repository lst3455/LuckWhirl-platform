package org.example.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.types.common.Constants;
import org.springframework.stereotype.Component;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyRuleEntity {
    /** raffle strategy id */
    private Long strategy_id;
    /** award id */
    private Long awardId;
    /** rule type(1. raffle rule 2. award rule) */
    private String ruleType;
    /** rule type (rule_lock) */
    private String ruleModel;
    /** proportion of rule */
    private String ruleValue;
    /** rule describe */
    private String ruleDesc;

    public Map<Long, Set<Long>> getRuleValueMap(){
        /** data sample: 4000:102,103,104,105 5000:102,103,104,105,106 6000:102,103,104,105,106,107 */
        if (!"rule_weight".equals(ruleModel)) return null;
        Map<Long, Set<Long>> ruleValueMap = new HashMap<>();
        String[] configs = ruleValue.split(Constants.SPLIT_SPACE);
        for (String config : configs) {
            int colonIndex = config.indexOf(":");
            /** wrong ruleValue data format */
            if (colonIndex == -1) return null;
            Long key = Long.valueOf(config.substring(0,colonIndex));
            long[] valueArray = Arrays.stream(config.substring(colonIndex + 1).split(Constants.SPLIT_COMMA))
                    .mapToLong(x -> Long.valueOf(x))
                    .toArray();
            Set<Long> valueSet = new HashSet<>();
            for (long value : valueArray) {
                valueSet.add(value);
            }
            ruleValueMap.put(key,valueSet);
        }
        return ruleValueMap;
    }
}
