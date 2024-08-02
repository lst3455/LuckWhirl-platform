package org.example.test.domain;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.service.armory.IStrategyArmory;
import org.example.domain.strategy.service.rule.chain.ILogicChain;
import org.example.domain.strategy.service.rule.chain.factory.DefaultLogicChainFactory;
import org.example.domain.strategy.service.rule.chain.impl.RuleWeightLogicChain;
import org.example.domain.strategy.service.rule.filter.impl.RuleLockLogicFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleStrategyChainTest {

    @Resource
    private RuleWeightLogicChain ruleWeightLogicChain;
    @Resource
    private DefaultLogicChainFactory defaultChainFactory;
    @Resource
    private RuleLockLogicFilter ruleLockLogicFilter;



    @Test
    public void test_LogicChain_rule_blacklist() {
        ILogicChain logicChain = defaultChainFactory.openLogicChain(10001L);
        Long awardId = logicChain.logic("user001",10001L);
        log.info("test result：{}", JSON.toJSONString(awardId));
    }

    @Test
    public void test_LogicChain_rule_weight() {
        ReflectionTestUtils.setField(ruleWeightLogicChain, "userRaffleTimes", 0L);
        ReflectionTestUtils.setField(ruleLockLogicFilter, "userRaffleTimes", 0L);
        for (int i = 0; i < 50; i++) {
            ILogicChain logicChain = defaultChainFactory.openLogicChain(10002L);
            Long awardId = logicChain.logic("user004",10002L);
            log.info("test result：{}", JSON.toJSONString(awardId));
        }
    }

    @Test
    public void test_LogicChain_rule_default() {
        for (int i = 0; i < 50; i++) {
            ILogicChain logicChain = defaultChainFactory.openLogicChain(10003L);
            Long awardId = logicChain.logic("user004",10003L);
            log.info("test result：{}", JSON.toJSONString(awardId));
        }
    }

}
