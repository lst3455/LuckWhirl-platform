package org.example.test.infrastructure;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.infrastructure.persistent.dao.IRaffleActivityAmountDao;
import org.example.infrastructure.persistent.dao.IRaffleActivitySkuDao;
import org.example.infrastructure.persistent.po.RaffleActivityAmount;
import org.example.infrastructure.persistent.po.RaffleActivitySku;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class IRaffleActivityAmountDaoTest {

    @Resource
    private IRaffleActivityAmountDao iRaffleActivityAmountDao;

    @Test
    public void test_queryRaffleActivityByActivityId(){
        RaffleActivityAmount raffleActivityAmount = iRaffleActivityAmountDao.queryRaffleActivityAmountByActivityAmountId(1L);
        log.info("test result: {}", JSON.toJSONString(raffleActivityAmount));
    }
}
