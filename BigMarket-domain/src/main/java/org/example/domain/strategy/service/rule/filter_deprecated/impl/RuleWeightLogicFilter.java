package org.example.domain.strategy.service.rule.filter_deprecated.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.RuleActionEntity;
import org.example.domain.strategy.model.entity.RuleMatterEntity;
import org.example.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.domain.strategy.service.annotation.LogicStrategy;
import org.example.domain.strategy.service.rule.filter_deprecated.ILogicFilter;
import org.example.domain.strategy.service.rule.filter_deprecated.factory.DefaultLogicFactory;
import org.example.types.common.Constants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_WEIGHT)
public class RuleWeightLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {

    @Resource
    private IStrategyRepository iStrategyRepository;

    private Long userRaffleTimes = 4500L;

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("rule filter weight userId:{} strategyId:{} ruleModel:{}",ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());
        String userId = ruleMatterEntity.getUserId();
        Long strategyId = ruleMatterEntity.getStrategyId();
        String ruleModel = ruleMatterEntity.getRuleModel();
        String ruleValue = iStrategyRepository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(), ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());


        /** ruleValue sample => 4000:102,103,104,105 5000:102,103,104,105,106 6000:102,103,104,105,106,107 */
        Map<Long,Set<Long>> ruleValueMap = getRuleValueMap(ruleValue,ruleModel);
        /** if ruleValueMap is empty or error in initialization, allow to pass filter engine */
        if (ruleValueMap == null || ruleValueMap.isEmpty()) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        List<Long> ruleValueKeyMap = new ArrayList<>(ruleValueMap.keySet());
        /** binary search a biggest key smaller than userRaffleTimes */
        Long validKey = binarySearchKey(ruleValueKeyMap,userRaffleTimes);

        /** if can find valid key, should be caught by filter engine */
        if (validKey != null) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_WEIGHT.getCode())
                    .data(RuleActionEntity.RaffleBeforeEntity.builder()
                            .strategyId(ruleMatterEntity.getStrategyId())
                            .ruleWeightValueKey(ruleValueMap.get(validKey))
                            .userRaffleTimes(validKey)
                            .build())
                    .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                    .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                    .build();
        }
        /** if can't find valid key, pass filter engine */
        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();
    }

    private Map<Long, Set<Long>> getRuleValueMap(String ruleValue, String ruleModel){
        /** data sample: 4000:102,103,104,105 5000:102,103,104,105,106 6000:102,103,104,105,106,107 */
        Map<Long, Set<Long>> ruleValueMap = new HashMap<>();
        if (!"rule_weight".equals(ruleModel)) return null;
        String[] configs = ruleValue.split(Constants.SPLIT_SPACE);
        for (String config : configs) {
            int colonIndex = config.indexOf(":");
            /** wrong ruleValue data format */
            if (colonIndex == -1) return null;
            Long key = Long.valueOf(config.substring(0,colonIndex));
            long[] valueArray = Arrays.stream(config.substring(colonIndex + 1).split(Constants.SPLIT_COMMA))
                    .mapToLong(x -> Long.valueOf(x))
                    .toArray();
            Set<Long> valueSet = new HashSet<>();
            for (long value : valueArray) {
                valueSet.add(value);
            }
            ruleValueMap.put(key,valueSet);
        }
        /** sort map by key in ascending order using TreeMap */
        Map<Long, Set<Long>> ruleValueMapSortedByKey = new TreeMap<>((key1,key2) -> Long.compare(key1,key2));
        ruleValueMapSortedByKey.putAll(ruleValueMap);
        return ruleValueMapSortedByKey;
    }
    private Long binarySearchKey(List<Long> ruleValueKeyMap,Long target){
        int left = -1;
        int right = ruleValueKeyMap.size();
        int mid;
        long targetInt = target;
        while(left + 1 != right){
            mid = (left + right) / 2;
            if (ruleValueKeyMap.get(mid) <= targetInt) left = mid;
            else right = mid;
        }
        return left == -1? null : ruleValueKeyMap.get(left);
    }
}
