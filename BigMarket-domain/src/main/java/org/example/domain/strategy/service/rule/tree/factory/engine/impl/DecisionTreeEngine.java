package org.example.domain.strategy.service.rule.tree.factory.engine.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import org.example.domain.strategy.model.vo.RuleTreeNodeLineVO;
import org.example.domain.strategy.model.vo.RuleTreeNodeVO;
import org.example.domain.strategy.model.vo.RuleTreeVO;
import org.example.domain.strategy.service.rule.tree.ILogicTreeNode;
import org.example.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import org.example.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;

import java.util.List;
import java.util.Map;

@Slf4j
public class DecisionTreeEngine implements IDecisionTreeEngine {

    private final Map<String, ILogicTreeNode> logicTreeNodeMap;

    private final RuleTreeVO ruleTreeVO;

    public DecisionTreeEngine(Map<String, ILogicTreeNode> logicTreeNodeMap, RuleTreeVO ruleTreeVO) {
        this.logicTreeNodeMap = logicTreeNodeMap;
        this.ruleTreeVO = ruleTreeVO;
    }

    @Override
    public DefaultTreeFactory.StrategyAwardData process(String userId, Long strategyId, Integer awardId) {

        DefaultTreeFactory.StrategyAwardData strategyAwardData = null;
        /** get root node of the tree */
        String curTreeNode = ruleTreeVO.getRuleNodeTreeRoot();
        /** get root node map of the tree */
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeMap();

        /** get root node object */
        RuleTreeNodeVO ruleTreeNodeVO = treeNodeMap.get(curTreeNode);
        while (ruleTreeNodeVO != null) {
            /** get root node object */
            ILogicTreeNode iLogicTreeNode = logicTreeNodeMap.get(ruleTreeNodeVO.getRuleKey());

            /** handle through cur tree node logic, return TreeActionEntity */
            DefaultTreeFactory.TreeActionEntity treeActionEntity = iLogicTreeNode.logic(userId, strategyId, awardId);
            /** handle through cur tree node logic, return TAKE_OVER or ALLOW */
            RuleLogicCheckTypeVO ruleLogicCheckTypeVO = treeActionEntity.getRuleLogicCheckTypeVO();
            /** get strategyAwardData after cur tree node logic */
            strategyAwardData = treeActionEntity.getStrategyAwardData();
            log.info("strategyTree engine [{}] treeId:{} treeNode:{} code:{}",ruleTreeVO.getTreeName(),ruleTreeVO.getTreeId(),curTreeNode,ruleLogicCheckTypeVO);

            /** get next tree node key */
            curTreeNode = nextNode(ruleLogicCheckTypeVO.getCode(), ruleTreeNodeVO.getTreeNodeLineVOList());
            /** get next tree node object */
            ruleTreeNodeVO = treeNodeMap.get(curTreeNode);

        }

        /** return final strategyAwardData after all tree node logic */
        return strategyAwardData;
    }

    private String nextNode(String matterValue, List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList) {
        if (ruleTreeNodeLineVOList == null || ruleTreeNodeLineVOList.isEmpty()) return new String();
        /** for current situation, ruleTreeNodeLineVOList only contain one line (TAKE_OVER or ALLOW) */
        for (RuleTreeNodeLineVO ruleTreeNodeLineVO : ruleTreeNodeLineVOList) {
            if (decisionLogic(matterValue,ruleTreeNodeLineVO)) return ruleTreeNodeLineVO.getRuleNodeTo();
        }
        throw new RuntimeException("strategy tree configuration error: can't find nextNode");
    }

    public boolean decisionLogic(String matterValue, RuleTreeNodeLineVO nodeLine) {
        switch (nodeLine.getRuleLimitType()) {
            case EQUAL:
                return matterValue.equals(nodeLine.getRuleLimitValue().getCode());
            // 以下规则暂时不需要实现
            case GT:
            case LT:
            case GE:
            case LE:
            default:
                return false;
        }
    }

}
