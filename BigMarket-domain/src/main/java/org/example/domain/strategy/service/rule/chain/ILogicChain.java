package org.example.domain.strategy.service.rule.chain;

public interface ILogicChain extends ILogicChainArmory{

    Long logic(String userId, Long strategyId);

}
