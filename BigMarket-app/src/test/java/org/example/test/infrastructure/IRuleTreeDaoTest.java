package org.example.test.infrastructure;


import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.domain.strategy.model.vo.RuleTreeVO;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.infrastructure.persistent.dao.IRuleTreeDao;
import org.example.infrastructure.persistent.dao.IRuleTreeNodeDao;
import org.example.infrastructure.persistent.dao.IRuleTreeNodeLineDao;
import org.example.infrastructure.persistent.po.RuleTree;
import org.example.infrastructure.persistent.po.RuleTreeNode;
import org.example.infrastructure.persistent.po.RuleTreeNodeLine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class IRuleTreeDaoTest {

    @Resource
    IRuleTreeDao iRuleTreeDao;

    @Resource
    IRuleTreeNodeLineDao iRuleTreeNodeLineDao;

    @Resource
    IRuleTreeNodeDao iRuleTreeNodeDao;

    @Resource
    IStrategyRepository iStrategyRepository;

    @Test
    public void test_queryRuleTreeByTreeId(){
        RuleTree ruleTree = iRuleTreeDao.queryRuleTreeByTreeId("tree_lock");
        log.info("test result: ruleTree:{}",ruleTree.toString());
        RuleTreeVO ruleTreeVO = iStrategyRepository.queryRuleTreeByTreeId("tree_lock");
        log.info("test result: ruleTree:{}",ruleTreeVO.toString());
    }
    @Test
    public void test_queryRuleTreeNodeLineListByTreeId(){
        List<RuleTreeNodeLine> ruleTreeNodeLineList = iRuleTreeNodeLineDao.queryRuleTreeNodeLineListByTreeId("tree_lock");
        log.info("test result: ruleTreeNodeLineList:{}",ruleTreeNodeLineList);
    }
    @Test
    public void test_queryRuleTreeNodeListByTreeId(){
        List<RuleTreeNode> ruleTreeNodeList = iRuleTreeNodeDao.queryRuleTreeNodeListByTreeId("tree_lock");
        log.info("test result: ruleTreeNodeList:{}",ruleTreeNodeList);
    }


}
