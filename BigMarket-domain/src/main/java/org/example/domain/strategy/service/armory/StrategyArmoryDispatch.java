package org.example.domain.strategy.service.armory;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.model.entity.StrategyEntity;
import org.example.domain.strategy.model.entity.StrategyRuleEntity;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

@Service
@Slf4j
public class StrategyArmoryDispatch implements IStrategyArmory, IStrategyDispatch{

    @Resource
    private IStrategyRepository iStrategyRepository;

    @Override
    public boolean assembleRaffleStrategy(Long strategyId) {
        /** get the raffle config */
        List<StrategyAwardEntity> strategyAwardEntities = iStrategyRepository.getStrategyAwardList(strategyId);

        /** store the award stock data in cache */
        for (StrategyAwardEntity strategyAwardEntity : strategyAwardEntities){
            Long awardId = strategyAwardEntity.getAwardId();
            Long awardAmount = strategyAwardEntity.getAwardAmount();
            iStrategyRepository.storeStrategyAwardAmount(strategyId,awardId,awardAmount);
        }

        assembleRaffleStrategy(String.valueOf(strategyId), strategyAwardEntities);

        StrategyEntity strategyEntity = iStrategyRepository.getStrategyEntityByStrategyId(strategyId);
        if (strategyEntity == null) return true;
        String ruleWeightModel = strategyEntity.getWeightModelFromRuleModels();
        if (ruleWeightModel == null) return true;
        StrategyRuleEntity strategyRuleEntity = iStrategyRepository.getStrategyRule(strategyId, ruleWeightModel);
        if (strategyRuleEntity == null) {
            throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(),ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo());
        }
        /** ruleValueMap sample: {4000=[102, 103, 104, 105], 6000=[102, 103, 104, 105, 106, 107], 5000=[102, 103, 104, 105, 106]} */
        Map<Long, Set<Long>> ruleValueMap = strategyRuleEntity.getRuleValueMap();
        for (Map.Entry<Long, Set<Long>> entry : ruleValueMap.entrySet()) {
            Long raffleTimes = entry.getKey();
            Set<Long> AvailableAwards = entry.getValue();
            List<StrategyAwardEntity> strategyAwardEntitiesClone = new ArrayList<>(strategyAwardEntities);
            strategyAwardEntitiesClone.removeIf(strategyAwardEntity -> !AvailableAwards.contains(strategyAwardEntity.getAwardId()));
            assembleRaffleStrategy(String.valueOf(strategyId).concat("_").concat(String.valueOf(raffleTimes)),strategyAwardEntitiesClone);
        }
        return true;
    }

    public void assembleRaffleStrategy(String strategyIdAsKey, List<StrategyAwardEntity> strategyAwardEntities) {
        /** calculate sum of rate */
        BigDecimal totalAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        /** get the minimum rate */
        BigDecimal minAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal awardRateRange = totalAwardRate.divide(minAwardRate,0, RoundingMode.CEILING);

        /** initialize awardTable to store awardId */
        ArrayList<Long> strategyAwardTable = new ArrayList<>();

        /** fill in awardId */
        for (StrategyAwardEntity strategyAwardEntity : strategyAwardEntities) {
            Long awardId = strategyAwardEntity.getAwardId();
            BigDecimal awardRate = strategyAwardEntity.getAwardRate();
            for (long i = 0; i < awardRateRange.multiply(awardRate).setScale(0, RoundingMode.CEILING).longValue(); i++) {
                strategyAwardTable.add(awardId);
            }
        }

        Collections.shuffle(strategyAwardTable);

        /** create map to store in redis */
        HashMap<Long, Long> shuffleStrategyAwardTable = new HashMap<>();
        for (long i = 0; i < strategyAwardTable.size(); i++) {
            shuffleStrategyAwardTable.put(i, strategyAwardTable.get((int) i));
        }

        iStrategyRepository.storeStrategyAwardTable(strategyIdAsKey, strategyAwardTable.size(), shuffleStrategyAwardTable);
    }

    @Override
    public Long getRandomAwardId(Long strategyId) {
        /** get rateRange for producing random number(up bound) */
        Integer rateRange = iStrategyRepository.getRateRange(String.valueOf(strategyId));
        return iStrategyRepository.getStrategyAwardId(String.valueOf(strategyId),(long) new SecureRandom().nextInt(rateRange));
    }

    @Override
    public Long getRandomAwardId(Long strategyId, Long raffleTimes) {
        /** get rateRange for producing random number(up bound) */
        String key = String.valueOf(strategyId).concat("_").concat(String.valueOf(raffleTimes));
        Integer rateRange = iStrategyRepository.getRateRange(key);
        return iStrategyRepository.getStrategyAwardId(key,(long) new SecureRandom().nextInt(rateRange));
    }
}
