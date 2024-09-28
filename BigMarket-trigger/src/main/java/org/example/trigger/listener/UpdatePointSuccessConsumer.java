package org.example.trigger.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.entity.DeliveryOrderEntity;
import org.example.domain.activity.service.IRaffleActivityAccountQuotaService;
import org.example.domain.point.event.SendPointMessageEvent;
import org.example.types.enums.ResponseCode;
import org.example.types.event.BaseEvent;
import org.example.types.exception.AppException;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class UpdatePointSuccessConsumer {
    @Value("${spring.rabbitmq.topic.update_point_success}")
    private String topic;
    @Resource
    private IRaffleActivityAccountQuotaService iRaffleActivityAccountQuotaService;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.update_point_success}"))
    public void listener(String message) {
        try {
            log.info("listen to update_point_success, topic: {}, message: {}", topic, message);
            BaseEvent.EventMessage<SendPointMessageEvent.SendPointMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<SendPointMessageEvent.SendPointMessage>>() {
            }.getType());
            SendPointMessageEvent.SendPointMessage sendPointMessage = eventMessage.getData();

            /** update activity order status and update activity account */
            DeliveryOrderEntity deliveryOrderEntity = new DeliveryOrderEntity();
            deliveryOrderEntity.setUserId(sendPointMessage.getUserId());
            deliveryOrderEntity.setOutBusinessNo(sendPointMessage.getOutBusinessNo());
            iRaffleActivityAccountQuotaService.updateActivityOrder(deliveryOrderEntity);
            log.info("listen to update_point_success - success, topic: {}, message: {}", topic, message);
        } catch (AppException e) {
            if (ResponseCode.INDEX_DUPLICATE.getCode().equals(e.getCode())) {
                log.warn("listen to update_point_success - update fail, duplicate key topic: {} message: {}", topic, message, e);
                return;
            }
            throw e;
        } catch (Exception e) {
            log.error("listen to update_point_success fail: {} message: {}", topic, message, e);
            throw e;
        }
    }

}
