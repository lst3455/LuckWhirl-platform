package org.example.domain.strategy.repository;

import org.example.domain.strategy.model.entity.StrategyAwardEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public interface IStrategyRepository {

    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardTable(Long strategyId, BigDecimal awardRateRange, HashMap<Long, Long> shuffleStrategyAwardTable);

    Integer getRateRange(Long strategyId);

    Long getStrategyAwardId(Long strategyId, Long rateKey);
}
