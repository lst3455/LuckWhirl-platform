package org.example.domain.strategy.repository;

import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.model.entity.StrategyEntity;
import org.example.domain.strategy.model.entity.StrategyRuleEntity;
import org.example.domain.strategy.model.vo.RuleTreeVO;
import org.example.domain.strategy.model.vo.StrategyAwardRuleModelVO;
import org.example.domain.strategy.model.vo.StrategyAwardStockKeyVO;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public interface IStrategyRepository {

    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardTable(String strategyIdAsKey, int awardRateRange, HashMap<Long, Long> shuffleStrategyAwardTable);

    Integer getRateRange(String strategyIdAsKey);

    Long getStrategyAwardId(String strategyIdAsKey, Long rateKey);

    StrategyEntity getStrategyEntityByStrategyId(Long strategyId);

    StrategyRuleEntity getStrategyRule(Long strategyId, String ruleModel);

    String queryStrategyRuleValue(Long strategyId, Long awardId, String ruleModel);

    String queryStrategyRuleValue(Long strategyId, String ruleModel);

    StrategyEntity queryStrategyEntityByStrategyId(Long strategyId);

    StrategyAwardRuleModelVO queryStrategyAwardRuleModelVO(Long strategyId, Long awardId);

    RuleTreeVO queryRuleTreeByTreeId(String treeId);

    void storeStrategyAwardAmount(Long strategyId, Long awardId, Long awardAmount);

    Boolean subtractAwardStock(String cacheKey);

    void awardStockConsumeSendQueue(StrategyAwardStockKeyVO build);

    StrategyAwardStockKeyVO takeQueueValue();

    void updateStrategyAwardStock(Long strategyId, Long awardId);

    StrategyAwardEntity queryStrategyAwardEntityByStrategyIdAndAwardId(Long strategyId, Long awardId);

    Long queryStrategyIdByActivityId(Long activityId);
}

