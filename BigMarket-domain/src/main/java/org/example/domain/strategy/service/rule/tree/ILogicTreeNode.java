package org.example.domain.strategy.service.rule.tree;

import org.example.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

public interface ILogicTreeNode {

    DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Long awardId);


}
