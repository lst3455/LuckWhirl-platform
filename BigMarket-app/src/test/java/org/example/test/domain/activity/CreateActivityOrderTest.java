package org.example.test.domain.activity;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.entity.ActivityOrderEntity;
import org.example.domain.activity.model.entity.ActivityShopCartEntity;
import org.example.domain.activity.model.entity.ActivitySkuChargeEntity;
import org.example.domain.activity.model.vo.OrderTradeTypeVO;
import org.example.domain.activity.service.IRaffleActivityAccountQuotaService;
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
    private IRaffleActivityAccountQuotaService iRaffleActivityAccountQuotaService;

    @Test
    public void test_createActivityOrder(){
        ActivityOrderEntity activityOrder = iRaffleActivityAccountQuotaService.createActivityOrder(ActivityShopCartEntity.builder()
                        .sku(9011L)
                        .userId("user01")
                .build());

        log.info("test result: {}", JSON.toJSONString(activityOrder));
    }

    @Test
    public void test_createSkuChargeOrder(){
        ActivitySkuChargeEntity activitySkuChargeEntity = new ActivitySkuChargeEntity();
        activitySkuChargeEntity.setSku(9011L);
        activitySkuChargeEntity.setUserId("xiaofuge");
        activitySkuChargeEntity.setOutBusinessNo("700091009116");
        String orderId = iRaffleActivityAccountQuotaService.createSkuChargeOrder(activitySkuChargeEntity);
        log.info("test result: {}",orderId);
    }

    @Test
    public void test_createSkuChargeOrder_Nonpay(){
        ActivitySkuChargeEntity activitySkuChargeEntity = new ActivitySkuChargeEntity();
        activitySkuChargeEntity.setSku(9011L);
        activitySkuChargeEntity.setUserId("xiaofuge");
        activitySkuChargeEntity.setOutBusinessNo("700091009118");
        activitySkuChargeEntity.setOrderTradeTypeVO(OrderTradeTypeVO.non_pay_trade);
        String orderId = iRaffleActivityAccountQuotaService.createSkuChargeOrder(activitySkuChargeEntity);
        log.info("test result: {}",orderId);
    }

    @Test
    public void test_createSkuChargeOrder_pay(){
        ActivitySkuChargeEntity activitySkuChargeEntity = new ActivitySkuChargeEntity();
        activitySkuChargeEntity.setSku(9011L);
        activitySkuChargeEntity.setUserId("xiaofuge");
        activitySkuChargeEntity.setOutBusinessNo("700091009120");
        activitySkuChargeEntity.setOrderTradeTypeVO(OrderTradeTypeVO.pay_trade);
        String orderId = iRaffleActivityAccountQuotaService.createSkuChargeOrder(activitySkuChargeEntity);
        log.info("test result: {}",orderId);
    }



}
