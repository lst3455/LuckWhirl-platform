package org.example.trigger.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendTaskMessageCustomer {
    @Value("${spring.rabbitmq.topic.send_award}")
    private String topic;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.send_award}"))
    public void listener(String message) {
        try {
            log.info("listen to sendTaskMessage, topic: {} message: {}", topic, message);
        } catch (Exception e) {
            log.error("listen to sendTaskMessage fail, topic: {} message: {}", topic, message);
            throw e;
        }
    }

}
