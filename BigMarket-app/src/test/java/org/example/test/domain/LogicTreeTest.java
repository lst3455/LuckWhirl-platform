package org.example.test.domain;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.vo.*;
import org.example.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import org.example.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LogicTreeTest {

    @Resource
    private DefaultTreeFactory defaultTreeFactory;

    @Test
    public void test_tree_rule(){
        // 构建参数
        RuleTreeNodeVO rule_lock = RuleTreeNodeVO.builder()
                .treeId("tree_lock")
                .ruleKey("rule_lock")
                .ruleDesc("unlock if user raffle times greater than N")
                .ruleValue("1")
                .treeNodeLineVOList(new ArrayList<RuleTreeNodeLineVO>() {{
                    add(RuleTreeNodeLineVO.builder()
                            .treeId("tree_lock")
                            .ruleNodeFrom("rule_lock")
                            .ruleNodeTo("rule_lucky")
                            .ruleLimitType(RuleLimitTypeVO.EQUAL)
                            .ruleLimitValue(RuleLogicCheckTypeVO.TAKE_OVER)
                            .build());

                    add(RuleTreeNodeLineVO.builder()
                            .treeId("tree_lock")
                            .ruleNodeFrom("rule_lock")
                            .ruleNodeTo("rule_stock")
                            .ruleLimitType(RuleLimitTypeVO.EQUAL)
                            .ruleLimitValue(RuleLogicCheckTypeVO.ALLOW)
                            .build());
                }})
                .build();

        RuleTreeNodeVO rule_lucky = RuleTreeNodeVO.builder()
                .treeId("tree_lock")
                .ruleKey("rule_lucky")
                .ruleDesc("unlock if user raffle times greater than N")
                .ruleValue("1")
                .treeNodeLineVOList(null)
                .build();

        RuleTreeNodeVO rule_stock = RuleTreeNodeVO.builder()
                .treeId("tree_lock")
                .ruleKey("rule_stock")
                .ruleDesc("depend on the stock to handle")
                .ruleValue(null)
                .treeNodeLineVOList(new ArrayList<RuleTreeNodeLineVO>() {{
                    add(RuleTreeNodeLineVO.builder()
                            .treeId("tree_lock")
                            .ruleNodeFrom("rule_lock")
                            .ruleNodeTo("rule_lucky")
                            .ruleLimitType(RuleLimitTypeVO.EQUAL)
                            .ruleLimitValue(RuleLogicCheckTypeVO.TAKE_OVER)
                            .build());
                }})
                .build();

        RuleTreeVO ruleTreeVO = new RuleTreeVO();
        ruleTreeVO.setTreeId("tree_lock");
        ruleTreeVO.setTreeName("strategy tree rule；test name");
        ruleTreeVO.setTreeDesc("strategy tree rule；test describe");
        ruleTreeVO.setRuleTreeRootNode("rule_lock");

        ruleTreeVO.setTreeNodeMap(new HashMap<String, RuleTreeNodeVO>() {{
            put("rule_lock", rule_lock);
            put("rule_stock", rule_stock);
            put("rule_lucky", rule_lucky);
        }});

        IDecisionTreeEngine treeEngine = defaultTreeFactory.openLogicTree(ruleTreeVO);

        DefaultTreeFactory.StrategyAwardVO data = treeEngine.process("user004", 10001L, 100L);
        log.info("test result：{}", JSON.toJSONString(data));
    }
}
