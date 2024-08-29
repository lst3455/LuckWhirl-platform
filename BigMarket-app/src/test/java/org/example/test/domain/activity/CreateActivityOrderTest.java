package org.example.test.domain.activity;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.domain.activity.model.entity.ActivityOrderEntity;
import org.example.domain.activity.model.entity.ActivityShopCartEntity;
import org.example.domain.activity.service.IRaffleOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CreateActivityOrderTest {

    @Resource
    private IRaffleOrder iRaffleOrder;

    @Test
    public void test_createActivityOrder(){
        ActivityOrderEntity activityOrder = iRaffleOrder.createActivityOrder(ActivityShopCartEntity.builder()
                        .sku(9011L)
                        .userId("user01")
                .build());

        log.info("test result: {}", JSON.toJSONString(activityOrder));
    }



}
