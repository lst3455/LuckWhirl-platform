package org.example.domain.strategy.service.armory;

import java.util.Date;

public interface IStrategyDispatch {

    Long getRandomAwardId(Long strategyId, Long userRaffleTimes);

    Long getRandomAwardId(Long strategyId);

    Boolean subtractAwardStock(Long strategyId, Long awardId, Date endDateTime);
}
