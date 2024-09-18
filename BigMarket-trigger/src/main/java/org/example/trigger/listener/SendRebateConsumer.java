package org.example.trigger.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.entity.ActivitySkuChargeEntity;
import org.example.domain.activity.service.IRaffleActivityAccountQuotaService;
import org.example.domain.rebate.event.SendRebateMessageEvent;
import org.example.domain.rebate.model.vo.RebateTypeVO;
import org.example.types.event.BaseEvent;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class SendRebateConsumer {
    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String topic;

    @Resource
    private IRaffleActivityAccountQuotaService iRaffleActivityAccountQuotaService;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.send_rebate}"))
    public void listener(String message) {
        try {
            log.info("listen to sendTaskMessage, topic: {}, message: {}", topic, message);
            /** convert message */
            BaseEvent.EventMessage<SendRebateMessageEvent.SendRebateMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<SendRebateMessageEvent.SendRebateMessage>>() {
            }.getType());
            SendRebateMessageEvent.SendRebateMessage rebateMessage = eventMessage.getData();
            if (!rebateMessage.getRebateType().equals(RebateTypeVO.SKU.getCode())) {
                log.info("listen to sendTaskMessage - nonSku rebate, topic: {}, message: {}", topic, message);
                return;
            }
            /** create ActivitySkuChargeEntity and save to database */
            ActivitySkuChargeEntity activitySkuChargeEntity = new ActivitySkuChargeEntity();
            activitySkuChargeEntity.setUserId(rebateMessage.getUserId());
            activitySkuChargeEntity.setSku(Long.valueOf(rebateMessage.getRebateConfig()));
            activitySkuChargeEntity.setOutBusinessNo(rebateMessage.getBizId());
            iRaffleActivityAccountQuotaService.createSkuChargeOrder(activitySkuChargeEntity);

        } catch (Exception e) {
            log.error("listen to sendTaskMessage fail, topic: {}, message: {}", topic, message);
            throw e;
        }
    }
}
