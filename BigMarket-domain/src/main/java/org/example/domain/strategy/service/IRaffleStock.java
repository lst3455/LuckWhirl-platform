package org.example.domain.strategy.service;

import org.example.domain.strategy.model.vo.StrategyAwardStockKeyVO;

public interface IRaffleStock {

    StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException;

    void updateStrategyAwardStock(Long strategyId, Long awardId);
}
