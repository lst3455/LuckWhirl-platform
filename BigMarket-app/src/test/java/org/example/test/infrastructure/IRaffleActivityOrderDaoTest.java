package org.example.test.infrastructure;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.RandomStringUtils;
import org.example.infrastructure.persistent.dao.IRaffleActivityOrderDao;
import org.example.infrastructure.persistent.po.RaffleActivityOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class IRaffleActivityOrderDaoTest {

    @Resource
    private IRaffleActivityOrderDao iRaffleActivityOrderDao;

    @Test
    public void test_insert(){

        RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
        raffleActivityOrder.setUserId("test_user_02");
        raffleActivityOrder.setActivityId(100301L);
        raffleActivityOrder.setActivityName("test");
        raffleActivityOrder.setStrategyId(10004L);
        raffleActivityOrder.setOrderId(RandomStringUtils.randomNumeric(12));
        raffleActivityOrder.setOrderTime(new Date());
        raffleActivityOrder.setStatus("not_used");

        iRaffleActivityOrderDao.insert(raffleActivityOrder);
    }

    @Test
    public void test_queryRaffleActivityOrderByUserId(){
        List<RaffleActivityOrder> raffleActivityOrderList = iRaffleActivityOrderDao.queryRaffleActivityOrderByUserId("test_user_02");
        log.info("test result:{}", JSON.toJSONString(raffleActivityOrderList));
    }
}
