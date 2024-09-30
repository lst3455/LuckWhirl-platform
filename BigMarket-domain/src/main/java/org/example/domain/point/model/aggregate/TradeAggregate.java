package org.example.domain.point.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.domain.point.event.SendPointMessageEvent;
import org.example.domain.point.model.entity.TaskEntity;
import org.example.domain.point.model.entity.UserPointAccountEntity;
import org.example.domain.point.model.entity.UserPointOrderEntity;
import org.example.domain.point.model.vo.TaskStatusVO;
import org.example.domain.point.model.vo.TradeNameVO;
import org.example.domain.point.model.vo.TradeTypeVO;
import org.example.types.event.BaseEvent;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeAggregate {
    private String userId;
    private UserPointOrderEntity userPointOrderEntity;
    private UserPointAccountEntity userPointAccountEntity;
    private TaskEntity taskEntity;

    public static UserPointAccountEntity createUserPointAccountEntity(String userId, BigDecimal availableAmount) {
        return UserPointAccountEntity.builder()
                .userId(userId)
                .availableAmount(availableAmount)
                .build();
    }

    public static UserPointOrderEntity createUserPointOrderEntity(String userId,
                                                                  TradeNameVO tradeName,
                                                                  TradeTypeVO tradeType,
                                                                  BigDecimal tradeAmount,
                                                                  String outBusinessNo) {
        return UserPointOrderEntity.builder()
                .userId(userId)
                .orderId(RandomStringUtils.randomNumeric(12))
                .tradeName(tradeName)
                .tradeType(tradeType)
                .tradeAmount(tradeAmount)
                .outBusinessNo(outBusinessNo)
                .build();
    }

    public static TaskEntity createTaskEntity(String userId, String topic, String messageId, BaseEvent.EventMessage<SendPointMessageEvent.SendPointMessage> message) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setUserId(userId);
        taskEntity.setTopic(topic);
        taskEntity.setMessageId(messageId);
        taskEntity.setMessage(message);
        taskEntity.setStatus(TaskStatusVO.create);
        return taskEntity;
    }



}
