package org.example.trigger.job;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.vo.StrategyAwardStockKeyVO;
import org.example.domain.strategy.service.IRaffleStock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component()
public class UpdateAwardStockJob {
    @Resource
    private IRaffleStock iRaffleStock;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() {
        try {
            log.info("scheduled task，update the award stock [using delayQueue to fetch, decrease the access frequency to the database]");
            StrategyAwardStockKeyVO strategyAwardStockKeyVO = iRaffleStock.takeQueueValue();
            if (strategyAwardStockKeyVO == null) return;
            log.info("scheduled task，update the award stock strategyId:{} awardId:{}", strategyAwardStockKeyVO.getStrategyId(), strategyAwardStockKeyVO.getAwardId());
            iRaffleStock.updateStrategyAwardStock(strategyAwardStockKeyVO.getStrategyId(), strategyAwardStockKeyVO.getAwardId());
        } catch (Exception e) {
            log.error("scheduled task，update the award stock fail", e);
        }
    }

}
