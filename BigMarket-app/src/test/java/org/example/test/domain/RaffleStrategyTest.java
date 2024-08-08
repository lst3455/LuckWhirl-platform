package org.example.test.domain;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.RaffleAwardEntity;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;
import org.example.domain.strategy.service.IRaffleStrategy;
import org.example.domain.strategy.service.rule.filter.impl.RuleLockLogicFilter;
import org.example.domain.strategy.service.rule.filter.impl.RuleWeightLogicFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleStrategyTest {
    @Resource
    private IRaffleStrategy iraffleStrategy;

    @Resource
    private RuleWeightLogicFilter ruleWeightLogicFilter;
    @Autowired
    private RuleLockLogicFilter ruleLockLogicFilter;

    @Before
    public void setUp_weight() {
        ReflectionTestUtils.setField(ruleWeightLogicFilter, "userRaffleTimes", 1L);
    }
    @Before
    public void setUp_lock() {
        ReflectionTestUtils.setField(ruleLockLogicFilter, "userRaffleTimes", 15L);
    }

    @Test
    public void test_performRaffle() {
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("test_user_1")
                .strategyId(10001L)
                .build();

        for (int i = 0; i < 50; i++) {
            RaffleAwardEntity raffleAwardEntity = iraffleStrategy.performRaffleLogicFilter(raffleFactorEntity);
            log.info("request parameter：{}", JSON.toJSONString(raffleFactorEntity));
            log.info("test result：{}", JSON.toJSONString(raffleAwardEntity));
        }
    }

    @Test
    public void test_performRaffle_blacklist() {
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("user003")  // blacklist user: user001,user002,user003
                .strategyId(10001L)
                .build();

        for (int i = 0; i < 50; i++) {
            RaffleAwardEntity raffleAwardEntity = iraffleStrategy.performRaffleLogicFilter(raffleFactorEntity);

            log.info("request parameter：{}", JSON.toJSONString(raffleFactorEntity));
            log.info("test result：{}", JSON.toJSONString(raffleAwardEntity));
        }
    }

    @Test
    public void test_performRaffle_lock() {
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("user004")  // blacklist user: user001,user002,user003
                .strategyId(10003L)
                .build();

        for (int i = 0; i < 500; i++) {
            RaffleAwardEntity raffleAwardEntity = iraffleStrategy.performRaffleLogicChain(raffleFactorEntity);

            log.info("request parameter：{}", JSON.toJSONString(raffleFactorEntity));
            log.info("test result：{}", JSON.toJSONString(raffleAwardEntity));
        }
    }

}
