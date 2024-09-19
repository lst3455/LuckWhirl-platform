package org.example.domain.strategy.repository;

import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.model.entity.StrategyEntity;
import org.example.domain.strategy.model.entity.StrategyRuleEntity;
import org.example.domain.strategy.model.vo.RuleTreeVO;
import org.example.domain.strategy.model.vo.RuleWeightVO;
import org.example.domain.strategy.model.vo.StrategyAwardRuleModelVO;
import org.example.domain.strategy.model.vo.StrategyAwardStockKeyVO;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    Boolean subtractAwardStock(String cacheKey, Date endDateTime);

    void awardStockConsumeSendQueue(StrategyAwardStockKeyVO build);

    StrategyAwardStockKeyVO takeQueueValue();

    void updateStrategyAwardStock(Long strategyId, Long awardId);

    StrategyAwardEntity queryStrategyAwardEntityByStrategyIdAndAwardId(Long strategyId, Long awardId);

    Long queryStrategyIdByActivityId(Long activityId);

    Long queryTodayUserRaffleCount(String userId, Long strategyId);

    Map<String, Integer> queryRuleTreeLockNodeValueByTreeIds(String[] treeIds);

    Long queryTotalUserRaffleCount(String userId, Long strategyId);

    List<RuleWeightVO> queryAwardRuleWeight(Long strategyId);
}

