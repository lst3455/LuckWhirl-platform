package org.example.test.infrastructure;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.infrastructure.persistent.dao.IRaffleActivityDao;
import org.example.infrastructure.persistent.dao.IRaffleActivitySkuDao;
import org.example.infrastructure.persistent.po.RaffleActivity;
import org.example.infrastructure.persistent.po.RaffleActivitySku;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class IRaffleActivitySkuDaoTest {

    @Resource
    private IRaffleActivitySkuDao iRaffleActivitySkuDao;

    @Test
    public void test_queryRaffleActivityByActivityId(){
        RaffleActivitySku raffleActivitySku = iRaffleActivitySkuDao.queryRaffleActivitySkuBySku(9011L);
        log.info("test result: {}", JSON.toJSONString(raffleActivitySku));
    }
}