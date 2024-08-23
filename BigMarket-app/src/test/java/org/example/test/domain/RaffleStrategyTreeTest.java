package org.example.test.domain;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.RaffleAwardEntity;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;
import org.example.domain.strategy.service.IRaffleStrategy;
import org.example.domain.strategy.service.armory.IStrategyArmory;
import org.example.domain.strategy.service.rule.chain.impl.RuleWeightLogicChain;
import org.example.domain.strategy.service.rule.tree.impl.RuleLockLogicTreeNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleStrategyTreeTest {
    @Resource
    private IStrategyArmory iStrategyArmory;

    @Resource
    private RuleWeightLogicChain ruleWeightLogicChain;

    @Resource
    private RuleLockLogicTreeNode ruleLockLogicTreeNode;

    @Resource
    private IRaffleStrategy iraffleStrategy;

    @Before
    public void setup(){
        log.info("test result: {}",iStrategyArmory.assembleRaffleStrategy(10004L));
        ReflectionTestUtils.setField(ruleWeightLogicChain, "userRaffleTimes", 0L);
        ReflectionTestUtils.setField(ruleLockLogicTreeNode, "userRaffleTimes", 10L);
    }

    @Test
    public void test_performRaffle() throws InterruptedException {
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("user004")  // blacklist user: user001,user002,user003
                .strategyId(10004L)
                .build();

        for (int i = 0; i < 100; i++) {
            RaffleAwardEntity raffleAwardEntity = iraffleStrategy.performRaffleLogicChainWithRuleTree(raffleFactorEntity);

            log.info("request parameter：{}", JSON.toJSONString(raffleFactorEntity));
            log.info("test result：{}", JSON.toJSONString(raffleAwardEntity));
        }

        new CountDownLatch(1).await();
    }
}
