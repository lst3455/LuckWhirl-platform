package org.example.trigger.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.entity.ActivitySkuChargeEntity;
import org.example.domain.activity.model.vo.OrderTradeTypeVO;
import org.example.domain.activity.service.IRaffleActivityAccountQuotaService;
import org.example.domain.point.model.entity.TradeEntity;
import org.example.domain.point.model.vo.TradeNameVO;
import org.example.domain.point.model.vo.TradeTypeVO;
import org.example.domain.point.service.IPointUpdateService;
import org.example.domain.rebate.event.SendRebateMessageEvent;
import org.example.types.event.BaseEvent;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@Component
public class SendRebateConsumer {
    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String topic;

    @Resource
    private IRaffleActivityAccountQuotaService iRaffleActivityAccountQuotaService;
    @Resource
    private IPointUpdateService iPointUpdateService;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.send_rebate}"))
    public void listener(String message) {
        try {
            log.info("listen to sendTaskMessage, topic: {}, message: {}", topic, message);
            /** convert message */
            BaseEvent.EventMessage<SendRebateMessageEvent.SendRebateMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<SendRebateMessageEvent.SendRebateMessage>>() {
            }.getType());
            SendRebateMessageEvent.SendRebateMessage rebateMessage = eventMessage.getData();

            ActivitySkuChargeEntity activitySkuChargeEntity;
            switch (rebateMessage.getRebateType()) {
                case "sku":
                    log.info("listen to sendTaskMessage - sku rebate, topic: {}, message: {}", topic, message);
                    /** create ActivitySkuChargeEntity and save to database */
                    activitySkuChargeEntity = new ActivitySkuChargeEntity();
                    activitySkuChargeEntity.setUserId(rebateMessage.getUserId());
                    activitySkuChargeEntity.setSku(Long.valueOf(rebateMessage.getRebateConfig()));
                    activitySkuChargeEntity.setOutBusinessNo(rebateMessage.getBizId());
                    activitySkuChargeEntity.setOrderTradeTypeVO(OrderTradeTypeVO.non_pay_trade);
                    iRaffleActivityAccountQuotaService.createSkuChargeOrder(activitySkuChargeEntity);
                    break;
                case "point":
                    log.info("listen to sendTaskMessage - point rebate, topic: {}, message: {}", topic, message);
                    TradeEntity tradeEntity = TradeEntity.builder()
                            .userId(rebateMessage.getUserId())
                            .tradeName(TradeNameVO.REBATE)
                            .tradeType(TradeTypeVO.ADDITION)
                            .tradeAmount(new BigDecimal(rebateMessage.getRebateConfig()))
                            .outBusinessNo(rebateMessage.getBizId())
                            .build();
                    iPointUpdateService.createNonPayTypeUserPointOrder(tradeEntity);
                    break;
            }
        } catch (Exception e) {
            log.error("listen to sendTaskMessage fail, topic: {}, message: {}", topic, message);
            throw e;
        }
    }
}
