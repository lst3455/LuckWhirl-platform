package org.example.test.domain;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.RaffleAwardEntity;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;
import org.example.domain.strategy.service.IRaffleStrategy;
import org.example.domain.strategy.service.rule.chain.impl.RuleWeightLogicChain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LogicRuleFlowTest {

    @Resource
    private RuleWeightLogicChain ruleWeightLogicChain;

    @Resource
    private IRaffleStrategy iraffleStrategy;

    @Test
    public void test_performRaffle_blacklist() {
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("user003")  // blacklist user: user001,user002,user003
                .strategyId(10001L)
                .build();

        for (int i = 0; i < 50; i++) {
            RaffleAwardEntity raffleAwardEntity = iraffleStrategy.performRaffleLogicChainWithRuleTree(raffleFactorEntity);

            log.info("request parameter：{}", JSON.toJSONString(raffleFactorEntity));
            log.info("test result：{}", JSON.toJSONString(raffleAwardEntity));
        }
    }

    @Test
    public void test_performRaffle_weight() {
        ReflectionTestUtils.setField(ruleWeightLogicChain, "userRaffleTimes", 5000L);
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("user004")  // blacklist user: user001,user002,user003
                .strategyId(10004L)
                .build();

        for (int i = 0; i < 1; i++) {
            RaffleAwardEntity raffleAwardEntity = iraffleStrategy.performRaffleLogicChainWithRuleTree(raffleFactorEntity);

            log.info("request parameter：{}", JSON.toJSONString(raffleFactorEntity));
            log.info("test result：{}", JSON.toJSONString(raffleAwardEntity));
        }
    }

    @Test
    public void test_performRaffle_lucky() {
        ReflectionTestUtils.setField(ruleWeightLogicChain, "userRaffleTimes", 0L);
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("user004")  // blacklist user: user001,user002,user003
                .strategyId(10004L)
                .build();

        for (int i = 0; i < 1; i++) {
            RaffleAwardEntity raffleAwardEntity = iraffleStrategy.performRaffleLogicChainWithRuleTree(raffleFactorEntity);

            log.info("request parameter：{}", JSON.toJSONString(raffleFactorEntity));
            log.info("test result：{}", JSON.toJSONString(raffleAwardEntity));
        }
    }
}
