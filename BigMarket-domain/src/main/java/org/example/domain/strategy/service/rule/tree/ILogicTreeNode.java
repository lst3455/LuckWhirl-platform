package org.example.domain.strategy.service.rule.tree;

import org.example.domain.strategy.service.rule.tree.factory.DefaultLogicTreeFactory;

import java.util.Date;

public interface ILogicTreeNode {

    DefaultLogicTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Long awardId, String ruleValue, Date endDateTime);


}
