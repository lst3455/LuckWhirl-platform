package org.example.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.types.common.Constants;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyEntity {
    /** raffle strategy */
    private Long strategyId;
    /** describe of raffle strategy */
    private String strategyDesc;
    /** describe of raffle strategy */
    private String ruleModel;

    public String[] getRuleModels(){
        if (ruleModel.isEmpty()) return null;
        return ruleModel.split(Constants.SPLIT_COMMA);
    }

    public String getWeightModelFromRuleModels(){
        String[] ruleModels = getRuleModels();
        for(String ruleModel: ruleModels){
            if (ruleModel.equals("rule_weight")) return ruleModel;
        }
        return null;
    }
}
