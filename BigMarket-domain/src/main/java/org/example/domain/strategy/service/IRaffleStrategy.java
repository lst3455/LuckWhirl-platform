package org.example.domain.strategy.service;

import org.example.domain.strategy.model.entity.RaffleAwardEntity;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;

public interface IRaffleStrategy {

    RaffleAwardEntity performRaffleLogicFilter(RaffleFactorEntity raffleFactorEntity);
    RaffleAwardEntity performRaffleLogicChain(RaffleFactorEntity raffleFactorEntity);
    RaffleAwardEntity performRaffleLogicChainWithRuleTree(RaffleFactorEntity raffleFactorEntity);
}
