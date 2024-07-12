package org.example.domain.strategy.service.armory;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

@Service
@Slf4j
public class StrategyArmory implements IStrategyArmory{

    @Resource
    private IStrategyRepository iStrategyRepository;

    @Override
    public void assembleRaffleStrategy(Long strategyId) {
        List<StrategyAwardEntity> strategyAwardEntities = iStrategyRepository.queryStrategyAwardList(strategyId);

        /** calculate sum of rate */
        BigDecimal totalAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        /** get the minimum rate */
        BigDecimal minAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal awardRateRange = totalAwardRate.divide(minAwardRate);
        /*BigDecimal awardRateRange = totalAwardRate.divide(minAwardRate,0, RoundingMode.CEILING);*/

        /** initialize awardTable to store awardId */
        ArrayList<Long> strategyAwardTable = new ArrayList<>();

        /** fill in awardId */
        for (StrategyAwardEntity strategyAwardEntity : strategyAwardEntities) {
            Long awardId = strategyAwardEntity.getAwardId();
            BigDecimal awardRate = strategyAwardEntity.getAwardRate();
            for(long i = 0; i < awardRateRange.multiply(awardRate).setScale(0,RoundingMode.CEILING).longValue(); i ++){
                strategyAwardTable.add(awardId);
            }
        }

        Collections.shuffle(strategyAwardTable);

        /** create map to store in redis */
        HashMap<Long,Long> shuffleStrategyAwardTable = new HashMap<>();
        for (long i = 0; i < strategyAwardTable.size(); i++) {
            shuffleStrategyAwardTable.put(i,strategyAwardTable.get((int)i));
        }

        iStrategyRepository.storeStrategyAwardTable(strategyId,awardRateRange,shuffleStrategyAwardTable);

    }

    @Override
    public Long getRandomAwardId(Long strategyId) {
        /** get rateRange for producing random number(up bound) */
        Integer rateRange = iStrategyRepository.getRateRange(strategyId);
        return iStrategyRepository.getStrategyAwardId(strategyId,(long) new SecureRandom().nextInt(rateRange));
    }
}
