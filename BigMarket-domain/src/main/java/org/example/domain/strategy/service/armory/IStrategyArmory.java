package org.example.domain.strategy.service.armory;

public interface IStrategyArmory {

    Long getRandomAwardId(Long strategyId);

    Long getRandomAwardId(Long strategyId, Long raffleTimes);

}
