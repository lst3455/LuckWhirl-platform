package org.example.test.domain;


import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.service.armory.StrategyArmoryDispatch;
import org.example.infrastructure.persistent.redis.IRedisService;
import org.example.types.common.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyArmoryDispatchTest {

    @Resource
    private StrategyArmoryDispatch strategyArmoryDispatch;

    @Resource
    private IRedisService iRedisService;

    @Before
    public void test_strategyArmory(){
        strategyArmoryDispatch.assembleRaffleStrategy(10001L);
        log.info("awardTable size: {} - int",iRedisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + 10001L).size());
        Map<Long,Long> map = iRedisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + 10001L);
        System.out.println(map.get(1L));
        Long awardId = iRedisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + 10001L,1L);
        log.info("awardId: {} - awardId",awardId);
        /*log.info("awardId: {} - awardId",String.valueOf(iRedisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + 10001L,1L)));*/
    }

    @Test
    public void test_getRandomAwardId(){
        for (int i = 0; i < 500; i++) {
            log.info("test result: {} - awardId", strategyArmoryDispatch.getRandomAwardId(10001L));
        }
    }

    @Test
    public void test_getRandomAwardId2(){
        for (int i = 0; i < 500; i++) {
            log.info("test result: {} - awardId", strategyArmoryDispatch.getRandomAwardId(10001L,4000L));
        }
    }


}
