package org.example.trigger.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.award.adapter.event.SendAwardMessageEvent;
import org.example.domain.award.model.entity.DeliveryAwardEntity;
import org.example.domain.award.service.IAwardService;
import org.example.types.event.BaseEvent;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class SendAwardConsumer {
    @Value("${spring.rabbitmq.topic.send_award}")
    private String topic;
    @Resource
    private IAwardService iAwardService;


    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.send_award}"))
    public void listener(String message) throws Exception {
        try {
            log.info("listen to sendTaskMessage, topic: {}, message: {}", topic, message);
            BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage>>() {
            }.getType());
            SendAwardMessageEvent.SendAwardMessage sendAwardMessage = eventMessage.getData();

            /**
             * delivery award
             * but currently just can handle point delivery
             */
            DeliveryAwardEntity deliveryAwardEntity = new DeliveryAwardEntity();
            deliveryAwardEntity.setUserId(sendAwardMessage.getUserId());
            deliveryAwardEntity.setOrderId(sendAwardMessage.getOrderId());
            deliveryAwardEntity.setAwardId(sendAwardMessage.getAwardId());
            deliveryAwardEntity.setAwardConfig(sendAwardMessage.getAwardConfig());
            iAwardService.deliveryAward(deliveryAwardEntity);
        } catch (Exception e) {
            log.error("listen to sendTaskMessage fail, topic: {}, message: {}", topic, message);
            throw e;
        }
    }

}
