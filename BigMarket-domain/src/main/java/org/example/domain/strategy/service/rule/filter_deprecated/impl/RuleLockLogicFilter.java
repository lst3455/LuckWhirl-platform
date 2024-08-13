package org.example.domain.strategy.service.rule.filter_deprecated.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.RuleActionEntity;
import org.example.domain.strategy.model.entity.RuleMatterEntity;
import org.example.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.domain.strategy.service.annotation.LogicStrategy;
import org.example.domain.strategy.service.rule.filter_deprecated.ILogicFilter;
import org.example.domain.strategy.service.rule.filter_deprecated.factory.DefaultLogicFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_LOCK)
public class RuleLockLogicFilter implements ILogicFilter<RuleActionEntity.RaffleCentreEntity> {

    @Resource
    private IStrategyRepository iStrategyRepository;


    private Long userRaffleTimes = 4500L;

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleCentreEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("rule filter raffleTimesLock userId:{} strategyId:{} ruleModel:{}",ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());

        String ruleValue = iStrategyRepository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(), ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());
        Long raffleTimesLimit = Long.valueOf(ruleValue);

        if (userRaffleTimes >= raffleTimesLimit){
            return RuleActionEntity.<RuleActionEntity.RaffleCentreEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        return RuleActionEntity.<RuleActionEntity.RaffleCentreEntity>builder()
                .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                .build();
    }
}
