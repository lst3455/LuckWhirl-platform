package org.example.test.domain.activity;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.domain.activity.model.entity.PartakeRaffleActivityEntity;
import org.example.domain.activity.model.entity.UserRaffleOrderEntity;
import org.example.domain.activity.service.IRaffleActivityPartakeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleActivityPartakeServiceTest {

    @Resource
    private IRaffleActivityPartakeService iRaffleActivityPartakeService;

    @Test
    public void test_(){
        PartakeRaffleActivityEntity partakeRaffleActivityEntity = PartakeRaffleActivityEntity.builder()
                .userId("xiaofuge")
                .activityId(100301L)
                .build();

        for (int i = 0; i < 1; i++) {
            UserRaffleOrderEntity userRaffleOrderEntity = iRaffleActivityPartakeService.createRaffleOrder(partakeRaffleActivityEntity);
            log.info("request parameter: {}", JSON.toJSONString(partakeRaffleActivityEntity));
            log.info("test result: {}", JSON.toJSONString(userRaffleOrderEntity));
        }
    }


}
