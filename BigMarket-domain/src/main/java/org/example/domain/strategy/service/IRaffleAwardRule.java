package org.example.domain.strategy.service;

import org.example.domain.strategy.model.vo.RuleWeightVO;

import java.util.List;
import java.util.Map;

public interface IRaffleAwardRule {

    Map<String,Integer> queryRuleTreeLockNodeValueByTreeIds(String[] treeIds);

    List<RuleWeightVO> queryAwardRuleWeight(Long strategyId);

    List<RuleWeightVO> queryAwardRuleWeightByActivityId(Long activityId);
}
