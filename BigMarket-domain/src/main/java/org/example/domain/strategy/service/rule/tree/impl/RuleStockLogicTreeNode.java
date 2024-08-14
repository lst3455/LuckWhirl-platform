package org.example.domain.strategy.service.rule.tree.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import org.example.domain.strategy.model.vo.StrategyAwardStockKeyVO;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.domain.strategy.service.armory.IStrategyDispatch;
import org.example.domain.strategy.service.rule.tree.ILogicTreeNode;
import org.example.domain.strategy.service.rule.tree.factory.DefaultLogicTreeFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component("rule_stock")
public class RuleStockLogicTreeNode implements ILogicTreeNode {

    @Resource
    private IStrategyDispatch iStrategyDispatch;

    private IStrategyRepository iStrategyRepository;

    @Override
    public DefaultLogicTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Long awardId, String ruleValue) {
        log.info("inside RuleStockLogicTreeNode userId:{} strategyId:{} ruleModel:{}",userId, strategyId, ruleValue);

        /** subtract the corresponding award stock amount, return true if success */
        boolean status = iStrategyDispatch.subtractAwardStock(strategyId,awardId);
        if (status) {
            /** add strategyId and awardId to delay queue, in order to delayed consume the database stock */
            iStrategyRepository.awardStockConsumeSendQueue(StrategyAwardStockKeyVO.builder()
                    .strategyId(strategyId)
                    .awardId(awardId)
                    .build());

            return DefaultLogicTreeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.TAKE_OVER)
                    .strategyAwardVO(DefaultLogicTreeFactory.StrategyAwardVO.builder()
                            .awardId(awardId)
                            .ruleModel("")
                            .build())
                    .build();
        }

        return DefaultLogicTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.ALLOW)
                .build();
    }
}
