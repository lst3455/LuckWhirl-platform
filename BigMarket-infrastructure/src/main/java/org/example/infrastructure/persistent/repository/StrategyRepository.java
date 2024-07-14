package org.example.infrastructure.persistent.repository;

import lombok.val;
import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.model.entity.StrategyEntity;
import org.example.domain.strategy.model.entity.StrategyRuleEntity;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.domain.strategy.service.armory.IStrategyArmory;
import org.example.infrastructure.persistent.dao.IStrategyAwardDao;
import org.example.infrastructure.persistent.dao.IStrategyDao;
import org.example.infrastructure.persistent.dao.IStrategyRuleDao;
import org.example.infrastructure.persistent.po.Strategy;
import org.example.infrastructure.persistent.po.StrategyAward;
import org.example.infrastructure.persistent.po.StrategyRule;
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
    private IStrategyDao iStrategyDao;

    @Resource
    private IRedisService iRedisService;

    @Resource
    private IStrategyRuleDao iStrategyRuleDao;

    @Override
    public List<StrategyAwardEntity> getStrategyAwardList(Long strategyId) {
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
    public void storeStrategyAwardTable(String strategyIdAsKey, int awardRateRange, HashMap<Long, Long> shuffleStrategyAwardTable) {
        iRedisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyIdAsKey,awardRateRange);
        Map<Long,Long> cacheStrategyAwardTable = iRedisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyIdAsKey);
        cacheStrategyAwardTable.putAll(shuffleStrategyAwardTable);
    }

    @Override
    public Integer getRateRange(String strategyIdAsKey) {
        return iRedisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyIdAsKey);
    }

    @Override
    public Long getStrategyAwardId(String strategyIdAsKey, Long rateKey) {
        return iRedisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyIdAsKey,rateKey);
    }

    @Override
    public StrategyEntity getStrategyEntityByStrategyId(Long strategyId) {
        String cacheKey = Constants.RedisKey.STRATEGY_KEY + strategyId;
        StrategyEntity strategyEntity = iRedisService.getValue(cacheKey);
        /** get data from cache */
        if (strategyEntity != null) return strategyEntity;
        /** get data from database */
        Strategy strategy = iStrategyDao.queryStrategyByStrategyId(strategyId);
        strategyEntity = StrategyEntity.builder()
                .strategyId(strategy.getStrategyId())
                .strategyDesc(strategy.getStrategyDesc())
                .ruleModel(strategy.getRuleModel())
                .build();
        /** put data into cache*/
        iRedisService.setValue(cacheKey,strategyEntity);
        return strategyEntity;
    }

    @Override
    public StrategyRuleEntity getStrategyRule(Long strategyId, String ruleModel) {
        String cacheKey = ruleModel.concat("_").concat(String.valueOf(strategyId));
        StrategyRuleEntity strategyRuleEntity = iRedisService.getValue(cacheKey);
        /** get data from cache */
        if (strategyRuleEntity != null) return strategyRuleEntity;
        StrategyRule strategyRule = new StrategyRule();
        strategyRule.setStrategyId(strategyId);
        strategyRule.setRuleModel(ruleModel);
        /** get data from database */
        strategyRule = iStrategyRuleDao.queryStrategyRule(strategyRule);
        strategyRuleEntity = StrategyRuleEntity.builder()
                .strategy_id(strategyRule.getStrategyId())
                .awardId(strategyRule.getAwardId())
                .ruleType(strategyRule.getRuleType())
                .ruleModel(strategyRule.getRuleModel())
                .ruleValue(strategyRule.getRuleValue())
                .ruleDesc(strategyRule.getRuleDesc())
                .build();
        /** put data into cache*/
        iRedisService.setValue(cacheKey,strategyRuleEntity);
        return strategyRuleEntity;
    }
}
