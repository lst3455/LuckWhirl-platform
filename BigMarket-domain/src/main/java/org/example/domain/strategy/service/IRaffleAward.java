package org.example.domain.strategy.service;

import org.example.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;

public interface IRaffleAward {

    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);
}
