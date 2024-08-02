package org.example.domain.strategy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import org.example.types.common.Constants;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyAwardRuleModelVO {

    private String ruleModels;

    public String[] raffleCentreRuleModelList(){
        List<String> ruleModelList = new ArrayList<>();
        String[] ruleModelValues = ruleModels.split(Constants.SPLIT_COMMA);
        for (String ruleModelValue : ruleModelValues){
            if (DefaultLogicFactory.LogicModel.isCenter(ruleModelValue)){
                ruleModelList.add(ruleModelValue);
            }
        }
        return ruleModelList.toArray(new String[0]);
    }
}
