package org.example.infrastructure.event;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.types.event.BaseEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventPublisher {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publish(String topic, BaseEvent.EventMessage<?> eventMessage) {
        try {
            String messageJson = JSON.toJSONString(eventMessage);
            rabbitTemplate.convertAndSend(topic, messageJson);
            log.info("send MQ success topic:{} message:{}", topic, messageJson);
        } catch (Exception e) {
            log.error("send MQ fail topic:{} message:{}", topic, JSON.toJSONString(eventMessage), e);
            throw e;
        }
    }

}
