package org.example.test.domain;


import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.service.armory.StrategyArmory;
import org.example.infrastructure.persistent.redis.IRedisService;
import org.example.types.common.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyArmoryTest {

    @Resource
    private StrategyArmory strategyArmory;

    @Resource
    private IRedisService iRedisService;

    @Test
    public void test_strategyArmory(){
        strategyArmory.assembleRaffleStrategy(10001L);
        log.info("awardTable size: {} - int",iRedisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + 10001L).size());
        Map<Long,Long> map = iRedisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + 10001L);
        System.out.println(map.get(1L));
        Long awardId = iRedisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + 10001L,1L);
        log.info("awardId: {} - awardId",awardId);
        /*log.info("awardId: {} - awardId",String.valueOf(iRedisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + 10001L,1L)));*/
    }

    @Test
    public void test_getRandomAwardId(){
        log.info("test result: {} - awardId",strategyArmory.getRandomAwardId(10001L));
        log.info("test result: {} - awardId",strategyArmory.getRandomAwardId(10001L));
        log.info("test result: {} - awardId",strategyArmory.getRandomAwardId(10001L));
        log.info("test result: {} - awardId",strategyArmory.getRandomAwardId(10001L));
        log.info("test result: {} - awardId",strategyArmory.getRandomAwardId(10001L));
        log.info("test result: {} - awardId",strategyArmory.getRandomAwardId(10001L));
        log.info("test result: {} - awardId",strategyArmory.getRandomAwardId(10001L));
        log.info("test result: {} - awardId",strategyArmory.getRandomAwardId(10001L));
        log.info("test result: {} - awardId",strategyArmory.getRandomAwardId(10001L));
        log.info("test result: {} - awardId",strategyArmory.getRandomAwardId(10001L));
        log.info("test result: {} - awardId",strategyArmory.getRandomAwardId(10001L));
        log.info("test result: {} - awardId",strategyArmory.getRandomAwardId(10001L));
        log.info("test result: {} - awardId",strategyArmory.getRandomAwardId(10001L));
        log.info("test result: {} - awardId",strategyArmory.getRandomAwardId(10001L));
    }

}
