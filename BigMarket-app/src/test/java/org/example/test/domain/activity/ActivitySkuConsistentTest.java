package org.example.test.domain.activity;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.example.domain.activity.model.entity.ActivitySkuChargeEntity;
import org.example.domain.activity.model.entity.PendingActivityOrderEntity;
import org.example.domain.activity.service.IRaffleActivityAccountQuotaService;
import org.example.domain.activity.service.armory.ActivityArmory;
import org.example.types.exception.AppException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivitySkuConsistentTest {

    @Resource
    private ActivityArmory activityArmory;
    @Resource
    private IRaffleActivityAccountQuotaService iRaffleActivityAccountQuotaService;


    @Before
    public void setUp(){
        activityArmory.assembleActivitySku(9011L);
    }

    @Test
    public void test_createSkuChargeOrder() throws InterruptedException {
        for (int i = 0; i < 1; i++) {
            try{
                ActivitySkuChargeEntity activitySkuChargeEntity = new ActivitySkuChargeEntity();
                activitySkuChargeEntity.setSku(9011L);
                activitySkuChargeEntity.setUserId("xiaofuge");
                activitySkuChargeEntity.setOutBusinessNo(RandomStringUtils.randomNumeric(12));
                PendingActivityOrderEntity pendingActivityOrderEntity = iRaffleActivityAccountQuotaService.createSkuChargeOrder(activitySkuChargeEntity);
                log.info("test result: {}", JSON.toJSONString(pendingActivityOrderEntity));
            }catch (AppException e){
                log.warn(e.getInfo());
            }
        }
        new CountDownLatch(1).await();
    }
}
