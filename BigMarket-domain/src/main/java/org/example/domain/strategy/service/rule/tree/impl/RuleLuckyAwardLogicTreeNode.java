package org.example.domain.strategy.service.rule.tree.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import org.example.domain.strategy.service.rule.tree.ILogicTreeNode;
import org.example.domain.strategy.service.rule.tree.factory.DefaultLogicTreeFactory;
import org.example.types.common.Constants;
import org.springframework.stereotype.Component;

@Slf4j
@Component("rule_lucky")
public class RuleLuckyAwardLogicTreeNode implements ILogicTreeNode {
    @Override
    public DefaultLogicTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Long awardId, String ruleValue) {
        log.info("inside RuleLuckyAwardLogicTreeNode, userId:{}, strategyId:{}, ruleModel:{}",userId, strategyId, ruleValue);
        String[] ruleValueSplit = ruleValue.split(Constants.SPLIT_COLON);
        if (ruleValueSplit.length == 0) {
            log.error("inside RuleLuckyAwardLogicTreeNode, wrong configuration userId:{} strategyId:{} awardId:{}", userId, strategyId, awardId);
            throw new RuntimeException("wrong ruleValue " + ruleValue);
        }
        /** get lucky awardId */
        Long luckAwardId = Long.valueOf(ruleValueSplit[0]);
        String awardRuleValue = ruleValueSplit.length > 1 ? ruleValueSplit[1] : "";

        log.info("inside RuleLuckyAwardLogicTreeNode, userId:{}, strategyId:{}, awardId:{}, awardRuleValue:{}", userId, strategyId, luckAwardId, awardRuleValue);

        return DefaultLogicTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.TAKE_OVER)
                .strategyAwardVO(DefaultLogicTreeFactory.StrategyAwardVO.builder()
                        .awardId(luckAwardId)
                        .ruleModel(awardRuleValue)
                        .build())
                .build();
    }
}
