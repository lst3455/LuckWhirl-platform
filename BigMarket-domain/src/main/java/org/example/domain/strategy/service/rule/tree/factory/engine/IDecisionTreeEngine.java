package org.example.domain.strategy.service.rule.tree.factory.engine;

import org.example.domain.strategy.service.rule.tree.factory.DefaultLogicTreeFactory;

import java.util.Date;

public interface IDecisionTreeEngine {
    DefaultLogicTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Long awardId, Date endDateTime);
}
