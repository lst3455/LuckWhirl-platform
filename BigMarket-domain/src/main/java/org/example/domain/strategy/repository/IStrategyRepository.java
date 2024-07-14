package org.example.domain.strategy.repository;

import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.model.entity.StrategyEntity;
import org.example.domain.strategy.model.entity.StrategyRuleEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public interface IStrategyRepository {

    List<StrategyAwardEntity> getStrategyAwardList(Long strategyId);

    void storeStrategyAwardTable(String strategyIdAsKey, int awardRateRange, HashMap<Long, Long> shuffleStrategyAwardTable);

    Integer getRateRange(String strategyIdAsKey);

    Long getStrategyAwardId(String strategyIdAsKey, Long rateKey);

    StrategyEntity getStrategyEntityByStrategyId(Long strategyId);

    StrategyRuleEntity getStrategyRule(Long strategyId, String ruleModel);
}
