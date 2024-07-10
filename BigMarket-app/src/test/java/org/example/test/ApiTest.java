package org.example.test;

import lombok.extern.slf4j.Slf4j;
import org.example.infrastructure.persistent.redis.IRedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RMap;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Resource
    private IRedisService redisService;

    @Test
    public void test() {
        RMap<Object,Object> map = redisService.getMap("strategy_id_100001");
        map.put(1,101);
        map.put(2,101);
        map.put(3,101);
        map.put(4,102);
        map.put(5,102);
        map.put(6,102);
        map.put(7,103);
        map.put(8,103);
        map.put(9,103);
        map.put(10,104);
        map.put(11,105);
        log.info("test result:{}", redisService.getFromMap("strategy_id_100001",11).toString());
    }

}
