package org.example.domain.strategy.service.rule.chain.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.service.armory.IStrategyDispatch;
import org.example.domain.strategy.service.rule.chain.AbstractLogicChain;
import org.example.domain.strategy.service.rule.chain.factory.DefaultLogicChainFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component("default")
public class DefaultLogicChain extends AbstractLogicChain {

    @Resource
    protected IStrategyDispatch iStrategyDispatch;

    @Override
    public Long logic(String userId, Long strategyId) {
        Long awardId = iStrategyDispatch.getRandomAwardId(strategyId);
        log.info("raffle rule chain - default userId: {} strategyId: {} ruleModel: {} awardId: {}",userId,strategyId,ruleModel(),awardId);
        return awardId;
    }

    @Override
    public DefaultLogicChainFactory.StrategyAwardVO treeVersionLogic(String userId, Long strategyId) {
        Long awardId = iStrategyDispatch.getRandomAwardId(strategyId);
        log.info("raffle rule chain - default userId: {} strategyId: {} ruleModel: {} awardId: {}",userId,strategyId,ruleModel(),awardId);
        return DefaultLogicChainFactory.StrategyAwardVO.builder()
                .awardId(awardId)
                .ruleModel(ruleModel())
                .build();
    }

    @Override
    protected String ruleModel() {
        return DefaultLogicChainFactory.LogicModel.RULE_DEFAULT.getCode();
    }
}
