package org.example.trigger.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.service.IRaffleActivitySkuStockService;
import org.example.types.event.BaseEvent;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@Slf4j
public class ActivitySkuStockZeroConsumer {

    @Resource
    private IRaffleActivitySkuStockService iRaffleActivitySkuStockService;

    @RabbitListener(queuesToDeclare = @Queue(value = "activity_sku_stock_zero"))
    public void listener(String message){
        try{
            log.info("listen to activity sku stock is 0, topic:{}, message:{}","activity_sku_stock_zero",message);
            BaseEvent.EventMessage<Long> eventMessage = JSON.parseObject(message,new TypeReference<BaseEvent.EventMessage<Long>>(){
            }.getType());
            Long sku = eventMessage.getData();
            /** update the sku stock in database */
            iRaffleActivitySkuStockService.clearActivitySkuStock(sku);
        }catch (Exception e){
            log.info("listen to activity sku stock is 0, consume fail, topic:{}, message:{}","activity_sku_stock_zero",message);
            throw e;
        }
    }
}
