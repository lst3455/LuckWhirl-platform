package org.example.domain.strategy.service.armory;

public interface IStrategyDispatch {

    Long getRandomAwardId(Long strategyId, Long userRaffleTimes);

    Long getRandomAwardId(Long strategyId);
}
