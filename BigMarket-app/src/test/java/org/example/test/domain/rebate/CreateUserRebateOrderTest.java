package org.example.test.domain.rebate;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.service.IRaffleActivityAccountQuotaService;
import org.example.domain.rebate.model.entity.BehaviorEntity;
import org.example.domain.rebate.model.vo.BehaviorTypeVO;
import org.example.domain.rebate.service.IBehaviorRebateService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CreateUserRebateOrderTest {

    @Resource
    private IBehaviorRebateService iBehaviorRebateService;

    @Resource
    private IRaffleActivityAccountQuotaService iRaffleActivityAccountQuotaService;

    @Test
    public void test_createRebateOrder() throws InterruptedException {
        List<String> rebateOrderList = iBehaviorRebateService.createRebateOrder(BehaviorEntity.builder()
                        .userId("xiaofuge")
                        .behaviorTypeVO(BehaviorTypeVO.SIGN)
                        .outBusinessNo("20240916")
                        .build());
        log.info("test result: {}", JSON.toJSONString(rebateOrderList));
        new CountDownLatch(1).await();
    }
}
