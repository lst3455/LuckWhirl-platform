package org.example.domain.strategy.service.armory;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class StrategyArmory implements IStrategyArmory{

    @Resource
    private IStrategyRepository iStrategyRepository;

    @Override
    public void assembleRaffleStrategy(Long strategyId) {
        List<StrategyAwardEntity> strategyAwardEntities = iStrategyRepository.queryStrategyAwardList(strategyId);


    }
}
