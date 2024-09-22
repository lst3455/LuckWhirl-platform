package org.example.domain.strategy.service.rule.chain;

import org.example.domain.strategy.service.rule.chain.factory.DefaultLogicChainFactory;

public interface ILogicChain extends ILogicChainArmory{

    Long logic(String userId, Long strategyId);

    DefaultLogicChainFactory.StrategyAwardVO treeVersionLogic(String userId, Long strategyId, Long awardId);

}
