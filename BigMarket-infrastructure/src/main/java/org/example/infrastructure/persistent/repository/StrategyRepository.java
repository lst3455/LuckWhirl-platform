package org.example.infrastructure.persistent.repository;

import lombok.val;
import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.domain.strategy.service.armory.IStrategyArmory;
import org.example.infrastructure.persistent.dao.IStrategyAwardDao;
import org.example.infrastructure.persistent.po.StrategyAward;
import org.example.infrastructure.persistent.redis.IRedisService;
import org.example.types.common.Constants;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private IStrategyAwardDao iStrategyAwardDao;

    @Resource
    private IRedisService iRedisService;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;
        List<StrategyAwardEntity> strategyAwardEntities = iRedisService.getValue(cacheKey);
        /** get data from cache */
        if (strategyAwardEntities != null && !strategyAwardEntities.isEmpty()) return strategyAwardEntities;
        /** get data from database*/
        List<StrategyAward> strategyAwards = iStrategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        strategyAwardEntities = new ArrayList<>(strategyAwards.size());
        /** loop to add transformed StrategyAwardEntity instance to list*/
        for (StrategyAward strategyAward : strategyAwards) {
            StrategyAwardEntity strategyAwardEntity = new StrategyAwardEntity();
            strategyAwardEntity.setId(strategyAward.getId());
            strategyAwardEntity.setStrategyId(strategyAward.getStrategyId());
            strategyAwardEntity.setAwardId(strategyAward.getAwardId());
            strategyAwardEntity.setAwardAmount(strategyAward.getAwardAmount());
            strategyAwardEntity.setAwardRemain(strategyAward.getAwardRemain());
            strategyAwardEntity.setAwardRate(strategyAward.getAwardRate());
            strategyAwardEntities.add(strategyAwardEntity);
        }
        /** put data into cache*/
        iRedisService.setValue(cacheKey,strategyAwardEntities);
        return strategyAwardEntities;
    }

    @Override
    public void storeStrategyAwardTable(Long strategyId, BigDecimal awardRateRange, HashMap<Long, Long> shuffleStrategyAwardTable) {
        iRedisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId,awardRateRange.intValue());
        Map<Long,Long> cacheStrategyAwardTable = iRedisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId);
        cacheStrategyAwardTable.putAll(shuffleStrategyAwardTable);
    }

    @Override
    public Integer getRateRange(Long strategyId) {
        return iRedisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId);
    }

    @Override
    public Long getStrategyAwardId(Long strategyId, Long rateKey) {
        return iRedisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId,rateKey);
    }
}
