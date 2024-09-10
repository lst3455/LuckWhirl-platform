package org.example.test.domain.award;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.award.service.IAwardService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AwardServiceTest {

    @Resource
    private IAwardService awardService;

    @Test
    public void test_sendTaskMessageJob() throws InterruptedException {

        new CountDownLatch(1).await();
    }
}
