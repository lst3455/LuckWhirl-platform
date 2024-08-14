package org.example.domain.strategy.service.rule.tree.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.RuleActionEntity;
import org.example.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import org.example.domain.strategy.service.rule.tree.ILogicTreeNode;
import org.example.domain.strategy.service.rule.tree.factory.DefaultLogicTreeFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component("rule_lock")
public class RuleLockLogicTreeNode implements ILogicTreeNode {

    private Long userRaffleTimes = 10L;

    @Override
    public DefaultLogicTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Long awardId, String ruleValue) {

        log.info("inside RuleLockLogicTreeNode userId:{} strategyId:{} ruleModel:{}",userId, strategyId, ruleValue);
        Long raffleTimesLimit;

        try{
            raffleTimesLimit = Long.valueOf(ruleValue);
        }catch (Exception e){
            throw new RuntimeException("inside RuleLockLogicTreeNode ruleValue: " + ruleValue + " config error");
        }

        /** userRaffleTimes greater than the lock config value */
        if (userRaffleTimes >= raffleTimesLimit){
            return DefaultLogicTreeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.ALLOW)
                    .build();
        }
        /** userRaffleTimes smaller than the lock config value */
        return DefaultLogicTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.TAKE_OVER)
                .build();
    }
}
