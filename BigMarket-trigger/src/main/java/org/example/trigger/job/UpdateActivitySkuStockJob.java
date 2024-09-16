package org.example.trigger.job;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.vo.ActivitySkuStockKeyVO;
import org.example.domain.activity.service.IRaffleActivitySkuStockService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component()
public class UpdateActivitySkuStockJob {

    @Resource
    private IRaffleActivitySkuStockService iRaffleActivitySkuStockService;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() {
        try {
            log.info("scheduled task，update the sku stock [using delayQueue to fetch, decrease the access frequency to the database]");
            ActivitySkuStockKeyVO activitySkuStockKeyVO = iRaffleActivitySkuStockService.takeQueueValue();
            if (activitySkuStockKeyVO == null) return;
            /**
             * todo
             * if sku stock is 0, no need to update again
             */
            log.info("scheduled task，update the sku stock success, sku:{}, activityId:{}", activitySkuStockKeyVO.getSku(), activitySkuStockKeyVO.getActivityId());
            iRaffleActivitySkuStockService.updateActivitySkuStock(activitySkuStockKeyVO.getSku());
        } catch (Exception e) {
            log.error("scheduled task，update the sku stock fail", e);
        }
    }

}

