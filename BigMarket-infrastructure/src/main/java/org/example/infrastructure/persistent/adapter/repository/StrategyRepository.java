package org.example.infrastructure.persistent.adapter.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.model.entity.StrategyEntity;
import org.example.domain.strategy.model.entity.StrategyRuleEntity;
import org.example.domain.strategy.model.vo.*;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.domain.strategy.service.rule.chain.factory.DefaultLogicChainFactory;
import org.example.infrastructure.persistent.dao.*;
import org.example.infrastructure.persistent.po.*;
import org.example.infrastructure.persistent.redis.IRedisService;
import org.example.types.common.Constants;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
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

    @Resource
    private IRuleTreeDao iRuleTreeDao;

    @Resource
    private IRuleTreeNodeLineDao iRuleTreeNodeLineDao;

    @Resource
    private IRuleTreeNodeDao iRuleTreeNodeDao;

    @Resource
    private IRaffleActivityDao iRaffleActivityDao;

    @Resource
    private IRaffleActivityAccountDayDao iRaffleActivityAccountDayDao;

    @Resource
    private IRaffleActivityAccountDao iRaffleActivityAccountDao;


    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_LIST_KEY + strategyId;
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
            strategyAwardEntity.setAwardTitle(strategyAward.getAwardTitle());
            strategyAwardEntity.setAwardSubtitle(strategyAward.getAwardSubtitle());
            strategyAwardEntity.setSort(strategyAward.getSort());
            strategyAwardEntity.setRuleModel(strategyAward.getRuleModel());
            strategyAwardEntities.add(strategyAwardEntity);
        }
        strategyAwardEntities.sort((a, b) -> Integer.compare(a.getSort(), b.getSort()));
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
        String cacheKey = Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyIdAsKey;
        if (!iRedisService.isExists(cacheKey)) {
            throw new AppException(ResponseCode.UN_ASSEMBLED_STRATEGY_ARMORY.getCode(), cacheKey + Constants.SPLIT_COLON + ResponseCode.UN_ASSEMBLED_STRATEGY_ARMORY.getInfo());
        }
        return iRedisService.getValue(cacheKey);
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
                .ruleModels(strategy.getRuleModels())
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

    @Override
    public String queryStrategyRuleValue(Long strategyId, Long awardId, String ruleModel) {
        StrategyRule strategyRule = new StrategyRule();
        strategyRule.setStrategyId(strategyId);
        strategyRule.setAwardId(awardId);
        strategyRule.setRuleModel(ruleModel);
        return iStrategyRuleDao.queryStrategyRuleValue(strategyRule);
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, String ruleModel) {
        return queryStrategyRuleValue(strategyId,null,ruleModel);
    }

    @Override
    public StrategyEntity queryStrategyEntityByStrategyId(Long strategyId) {
        /** get data from cache */
        String cacheKey = Constants.RedisKey.STRATEGY_KEY + strategyId;
        StrategyEntity strategyEntity = iRedisService.getValue(cacheKey);
        if (null != strategyEntity) return strategyEntity;

        /** get data from database */
        Strategy strategy = iStrategyDao.queryStrategyByStrategyId(strategyId);
        strategyEntity = StrategyEntity.builder()
                .strategyId(strategy.getStrategyId())
                .strategyDesc(strategy.getStrategyDesc())
                .ruleModels(strategy.getRuleModels())
                .build();
        iRedisService.setValue(cacheKey, strategyEntity);
        return strategyEntity;

    }

    @Override
    public StrategyAwardRuleModelVO queryStrategyAwardRuleModelVO(Long strategyId, Long awardId) {
        StrategyAward strategyAward = new StrategyAward();
        strategyAward.setStrategyId(strategyId);
        strategyAward.setAwardId(awardId);
        String ruleModels = iStrategyAwardDao.queryStrategyAwardRuleModel(strategyAward);
        return StrategyAwardRuleModelVO.builder().ruleModels(ruleModels).build();
    }

    @Override
    public RuleTreeVO queryRuleTreeByTreeId(String treeId) {
        /** get data from cache */
        String cacheKey = Constants.RedisKey.RULE_TREE_VO_KEY + treeId;
        RuleTreeVO ruleTreeVOCache = iRedisService.getValue(cacheKey);
        if (ruleTreeVOCache != null) return ruleTreeVOCache;

        /** get data from database */
        RuleTree ruleTree = iRuleTreeDao.queryRuleTreeByTreeId(treeId);
        List<RuleTreeNode> ruleTreeNodes = iRuleTreeNodeDao.queryRuleTreeNodeListByTreeId(treeId);
        List<RuleTreeNodeLine> ruleTreeNodeLines = iRuleTreeNodeLineDao.queryRuleTreeNodeLineListByTreeId(treeId);

        /** tree node line convert to map */
        /** data sample: {"rule_lock" : [(rule_lock,rule_lucky),(rule_lock,rule_stock)],"rule_stock": [(rule_stock,rule_lucky)]} */
        Map<String, List<RuleTreeNodeLineVO>> ruleTreeNodeLineMap = new HashMap<>();
        for (RuleTreeNodeLine ruleTreeNodeLine : ruleTreeNodeLines) {
            RuleTreeNodeLineVO ruleTreeNodeLineVO = RuleTreeNodeLineVO.builder()
                    .treeId(ruleTreeNodeLine.getTreeId())
                    .ruleNodeFrom(ruleTreeNodeLine.getRuleNodeFrom())
                    .ruleNodeTo(ruleTreeNodeLine.getRuleNodeTo())
                    .ruleLimitType(RuleLimitTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitType()))
                    .ruleLimitValue(RuleLogicCheckTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitValue()))
                    .build();

            List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList = ruleTreeNodeLineMap.computeIfAbsent(ruleTreeNodeLine.getRuleNodeFrom(), k -> new ArrayList<>());
            ruleTreeNodeLineVOList.add(ruleTreeNodeLineVO);
        }

        /** tree node line convert to map */
        Map<String, RuleTreeNodeVO> treeNodeMap = new HashMap<>();
        for (RuleTreeNode ruleTreeNode : ruleTreeNodes) {
            RuleTreeNodeVO ruleTreeNodeVO = RuleTreeNodeVO.builder()
                    .treeId(ruleTreeNode.getTreeId())
                    .ruleKey(ruleTreeNode.getRuleKey())
                    .ruleDesc(ruleTreeNode.getRuleDesc())
                    .ruleValue(ruleTreeNode.getRuleValue())
                    .treeNodeLineVOList(ruleTreeNodeLineMap.get(ruleTreeNode.getRuleKey()))
                    .build();
            treeNodeMap.put(ruleTreeNode.getRuleKey(), ruleTreeNodeVO);
        }

        /** build RuleTreeVO */
        RuleTreeVO ruleTreeVO = RuleTreeVO.builder()
                .treeId(ruleTree.getTreeId())
                .treeName(ruleTree.getTreeName())
                .treeDesc(ruleTree.getTreeDesc())
                .ruleTreeRootNode(ruleTree.getTreeRootNodeKey())
                .treeNodeMap(treeNodeMap)
                .build();

        iRedisService.setValue(cacheKey, ruleTreeVO);
        return ruleTreeVO;
    }

    @Override
    public void storeStrategyAwardAmount(Long strategyId, Long awardId, Long awardAmount) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_AMOUNT_KEY + strategyId + "_" + awardId;
        /** get data from cache */
        /*Long cacheAwardAmount = Long.valueOf(iRedisService.getValue(cacheKey));*/
        if (iRedisService.getValue(cacheKey) != null) return;
        /** store data to cache */
        iRedisService.setAtomicLong(cacheKey,awardAmount);
    }

    @Override
    public Boolean subtractAwardStock(String cacheKey) {
        return subtractAwardStock(cacheKey, null);
    }

    @Override
    public Boolean subtractAwardStock(String cacheKey, Date endDateTime) {
        Long remainAmount = iRedisService.decr(cacheKey);
        if (remainAmount < 0) {
            iRedisService.setValue(cacheKey,0);
            return false;
        }
        String lockKey = cacheKey + "_" + remainAmount;
        boolean lock = false;
        /** go different setNX depend on if use endDateTime */
        if (endDateTime == null) lock = iRedisService.setNX(lockKey);
        else {
            long expireMillis = endDateTime.getTime() - System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
            lock = iRedisService.setNX(lockKey,expireMillis,TimeUnit.MILLISECONDS);
        }

        if (!lock) log.info("lock strategy award stock fail, lockKey: {}",lockKey);
        return lock;
    }

    @Override
    public void awardStockConsumeSendQueue(StrategyAwardStockKeyVO strategyAwardStockKeyVO) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_AMOUNT_QUEUE_KEY;
        RBlockingQueue<StrategyAwardStockKeyVO> blockingQueue = iRedisService.getBlockingQueue(cacheKey);
        /** create a delay queue */
        RDelayedQueue<StrategyAwardStockKeyVO> delayedQueue = iRedisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(strategyAwardStockKeyVO,3, TimeUnit.SECONDS);

    }

    @Override
    public StrategyAwardStockKeyVO takeQueueValue() {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_AMOUNT_QUEUE_KEY;
        RBlockingQueue<StrategyAwardStockKeyVO> destinationQueue = iRedisService.getBlockingQueue(cacheKey);
        return destinationQueue.poll();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Long awardId) {
        StrategyAward strategyAward = new StrategyAward();
        strategyAward.setAwardId(awardId);
        strategyAward.setStrategyId(strategyId);
        iStrategyAwardDao.updateStrategyAwardStock(strategyAward);
    }

    @Override
    public StrategyAwardEntity queryStrategyAwardEntityByStrategyIdAndAwardId(Long strategyId, Long awardId) {
        /** get data from cache first */
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId + "_" + awardId;
        StrategyAwardEntity strategyAwardEntity = iRedisService.getValue(cacheKey);
        if (null != strategyAwardEntity) return strategyAwardEntity;

        /** get data from database */
        StrategyAward strategyAward = new StrategyAward();
        strategyAward.setAwardId(awardId);
        strategyAward.setStrategyId(strategyId);
        strategyAward = iStrategyAwardDao.queryStrategyAwardByStrategyIdAndAwardId(strategyAward);

        strategyAwardEntity = StrategyAwardEntity.builder()
                .strategyId(strategyAward.getStrategyId())
                .awardId(strategyAward.getAwardId())
                .awardTitle(strategyAward.getAwardTitle())
                .awardSubtitle(strategyAward.getAwardSubtitle())
                .awardAmount(strategyAward.getAwardAmount())
                .awardRemain(strategyAward.getAwardRemain())
                .awardRate(strategyAward.getAwardRate())
                .sort(strategyAward.getSort())
                .build();

        /** store data to cache */
        iRedisService.setValue(cacheKey, strategyAwardEntity);
        return strategyAwardEntity;
    }

    @Override
    public Long queryStrategyIdByActivityId(Long activityId) {
        return iRaffleActivityDao.queryStrategyIdByActivityId(activityId);
    }

    @Override
    public Long queryTodayUserRaffleCount(String userId, Long strategyId) {
        Long activityId = iRaffleActivityDao.queryRaffleActivityIdByStrategyId(strategyId);
        /** create RaffleActivityAccountDay object */
        RaffleActivityAccountDay raffleActivityAccountDay = new RaffleActivityAccountDay();
        raffleActivityAccountDay.setActivityId(activityId);
        raffleActivityAccountDay.setUserId(userId);
        raffleActivityAccountDay.setDay(raffleActivityAccountDay.currentDay());
        raffleActivityAccountDay = iRaffleActivityAccountDayDao.queryActivityAccountDay(raffleActivityAccountDay);

        if (raffleActivityAccountDay == null) return 0L;
        return (long) (raffleActivityAccountDay.getDayAmount() - raffleActivityAccountDay.getDayRemain());
    }


    @Override
    public Map<String, Integer> queryRuleTreeLockNodeValueByTreeIds(String[] treeIds) {
        if (treeIds == null || treeIds.length == 0) return new HashMap<>();
        Map<String, Integer> ruleLockAmountMap = new HashMap<>();
        Set<RuleTreeNode> ruleTreeNodeSet = new HashSet<>();
        for (String treeId : treeIds){
            List<RuleTreeNode> ruleTreeNodeList = iRuleTreeNodeDao.queryRuleTreeNodeListByTreeId(treeId);
            ruleTreeNodeSet.addAll(ruleTreeNodeList);
        }
        /** filter out those not rule_lock node */
        ruleTreeNodeSet = ruleTreeNodeSet.stream()
                .filter(node -> node != null && node.getRuleKey().equals("rule_lock"))
                .collect(Collectors.toSet());
        /** put treeId and ruleValue pair into map */
        for (RuleTreeNode ruleTreeNode : ruleTreeNodeSet){
            String treeId = ruleTreeNode.getTreeId();
            Integer ruleValue = Integer.valueOf(ruleTreeNode.getRuleValue());
            ruleLockAmountMap.put(treeId,ruleValue);
        }
        return ruleLockAmountMap;
    }

    @Override
    public Long queryTotalUserRaffleCount(String userId, Long strategyId) {
        Long activityId = iRaffleActivityDao.queryRaffleActivityIdByStrategyId(strategyId);
        /** create RaffleActivityAccountDay object */
        RaffleActivityAccount raffleActivityAccount = new RaffleActivityAccount();
        raffleActivityAccount.setActivityId(activityId);
        raffleActivityAccount.setUserId(userId);
        raffleActivityAccount = iRaffleActivityAccountDao.queryActivityAccountByUserId(raffleActivityAccount);

        if (raffleActivityAccount == null) return 0L;
        return (long) (raffleActivityAccount.getTotalAmount() - raffleActivityAccount.getDayRemain());
    }

    @Override
    public List<RuleWeightVO> queryAwardRuleWeight(Long strategyId) {
        /** get data from cache first */
        String cacheKey = Constants.RedisKey.STRATEGY_RULE_WEIGHT_KEY + strategyId;
        List<RuleWeightVO> ruleWeightVOList = iRedisService.getValue(cacheKey);
        if (ruleWeightVOList != null) return ruleWeightVOList;
        /** get data from database */
        ruleWeightVOList = new ArrayList<>();
        /** get rule weight value */
        StrategyRule strategyRule = new StrategyRule();
        strategyRule.setStrategyId(strategyId);
        strategyRule.setRuleModel(DefaultLogicChainFactory.LogicModel.RULE_WEIGHT.getCode());
        String ruleValue = iStrategyRuleDao.queryStrategyRuleValue(strategyRule);
        /** get rule weight value map through StrategyRuleEntity method */
        StrategyRuleEntity strategyRuleEntity = new StrategyRuleEntity();
        strategyRuleEntity.setRuleModel(DefaultLogicChainFactory.LogicModel.RULE_WEIGHT.getCode());
        strategyRuleEntity.setRuleValue(ruleValue);
        Map<Long, Set<Long>> ruleWeightValueMap = strategyRuleEntity.getRuleValueMap();

        Map<Long,String> awardMap = new HashMap<>(); // store id,title pair
        List<StrategyAward> strategyAwardList = iStrategyAwardDao.queryStrategyAwardListByStrategyId(strategyId); // get awardList

        Set<Long> ruleWeightValueMapKeySet = ruleWeightValueMap.keySet();
        for (Long ruleWeightKey : ruleWeightValueMapKeySet) {
            Set<Long> awardIdList = ruleWeightValueMap.get(ruleWeightKey);
            List<RuleWeightVO.Award> awardList = new ArrayList<>();
            for (Long awardId : awardIdList) {
                String awardTitle;
                if (!awardMap.containsKey(awardId)) {
                    awardTitle = searchStrategyAward(awardId, strategyAwardList).getAwardTitle();
                    awardMap.put(awardId,awardTitle);
                }
                awardTitle = awardMap.get(awardId);
                /** store into awardList*/
                awardList.add(RuleWeightVO.Award.builder()
                        .awardId(awardId)
                        .awardTitle(awardTitle)
                        .build());
            }
            ruleWeightVOList.add(RuleWeightVO.builder()
                    .ruleValue(ruleValue)
                    .weight(ruleWeightKey)
                    .awardIdSet(awardIdList)
                    .awardList(awardList)
                    .build());
        }

        iRedisService.setValue(cacheKey, ruleWeightVOList);
        return ruleWeightVOList;
    }

    private StrategyAward searchStrategyAward(Long awardId, List<StrategyAward> strategyAwardList) {
        for (StrategyAward strategyAward : strategyAwardList){
            if (strategyAward.getAwardId().equals(awardId)) return strategyAward;
        }
        return new StrategyAward();
    }
}
