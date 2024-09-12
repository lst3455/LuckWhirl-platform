package org.example.domain.strategy.service;

import java.util.Map;

public interface IRaffleAwardRule {

    Map<String,Integer> queryRuleTreeLockNodeValueByTreeIds(String[] treeIds);
}
