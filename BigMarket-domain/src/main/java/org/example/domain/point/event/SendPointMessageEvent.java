package org.example.domain.point.event;

import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.types.event.BaseEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Component
public class SendPointMessageEvent extends BaseEvent<SendPointMessageEvent.SendPointMessage> {
    @Value("${spring.rabbitmq.topic.update_point_success}")
    private String topic;

    @Override
    public EventMessage<SendPointMessage> buildEventMessage(SendPointMessage data) {
        return EventMessage.<SendPointMessageEvent.SendPointMessage>builder()
                .id(RandomStringUtils.randomNumeric(11))
                .timestamp(new Date())
                .data(data)
                .build();
    }

    @Override
    public String topic() {
        return topic;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendPointMessage {

        private String userId;

        private String orderId;

        private BigDecimal amount;

        private String outBusinessNo;
    }

}
