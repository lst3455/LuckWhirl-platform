package org.example.test.domain.point;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.point.model.entity.TradeEntity;
import org.example.domain.point.model.vo.TradeNameVO;
import org.example.domain.point.model.vo.TradeTypeVO;
import org.example.domain.point.service.IPointUpdateService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class UpdateUserPointTest {

    @Resource
    private IPointUpdateService iPointUpdateService;

    @Test
    public void test_createUserPointOrder(){
        TradeEntity tradeEntity = TradeEntity.builder()
                .userId("user003")
                .tradeName(TradeNameVO.REBATE)
                .tradeType(TradeTypeVO.SUBTRACTION)
                .tradeAmount(new BigDecimal("-10.91"))
                .outBusinessNo("100009900004")
                .build();
        iPointUpdateService.createUserPointOrder(tradeEntity);
    }
}
