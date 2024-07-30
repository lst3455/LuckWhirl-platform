package org.example.domain.strategy.service.armory;

public interface IStrategyArmory {

    /** assemble the raffle strategy for corresponding strategyId */
    boolean assembleRaffleStrategy(Long strategyId);

}
