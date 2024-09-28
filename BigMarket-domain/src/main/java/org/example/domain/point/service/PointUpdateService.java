package org.example.domain.point.service;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.point.event.SendPointMessageEvent;
import org.example.domain.point.model.aggregate.TradeAggregate;
import org.example.domain.point.model.entity.TaskEntity;
import org.example.domain.point.model.entity.TradeEntity;
import org.example.domain.point.model.entity.UserPointAccountEntity;
import org.example.domain.point.model.entity.UserPointOrderEntity;
import org.example.domain.point.repository.IPointRepository;
import org.example.types.event.BaseEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class PointUpdateService implements IPointUpdateService{

    @Resource
    private IPointRepository iPointRepository;

    @Resource
    private SendPointMessageEvent sendPointMessageEvent;

    @Override
    public String createUserPointOrder(TradeEntity tradeEntity) {
        log.info("update user point account point start, userId:{}, tradeName:{}, amount:{}", tradeEntity.getUserId(), tradeEntity.getTradeName(), tradeEntity.getTradeAmount());
        UserPointAccountEntity userPointAccountEntity = TradeAggregate.createUserPointAccountEntity(
                tradeEntity.getUserId(),
                tradeEntity.getTradeAmount());

        UserPointOrderEntity userPointOrderEntity = TradeAggregate.createUserPointOrderEntity(
                tradeEntity.getUserId(),
                tradeEntity.getTradeName(),
                tradeEntity.getTradeType(),
                tradeEntity.getTradeAmount(),
                tradeEntity.getOutBusinessNo());

        SendPointMessageEvent.SendPointMessage sendPointMessage = new SendPointMessageEvent.SendPointMessage();
        sendPointMessage.setUserId(tradeEntity.getUserId());
        sendPointMessage.setOrderId(userPointOrderEntity.getOrderId());
        sendPointMessage.setAmount(userPointOrderEntity.getTradeAmount());
        sendPointMessage.setOutBusinessNo(tradeEntity.getOutBusinessNo());
        BaseEvent.EventMessage<SendPointMessageEvent.SendPointMessage> sendPointMessageEventMessage = sendPointMessageEvent.buildEventMessage(sendPointMessage);
        TaskEntity taskEntity = TradeAggregate.createTaskEntity(tradeEntity.getUserId(), sendPointMessageEvent.topic(), sendPointMessageEventMessage.getId(), sendPointMessageEventMessage);

        /** build the aggregate object */
        TradeAggregate tradeAggregate = TradeAggregate.builder()
                .userId(tradeEntity.getUserId())
                .userPointAccountEntity(userPointAccountEntity)
                .userPointOrderEntity(userPointOrderEntity)
                .taskEntity(taskEntity)
                .build();

        iPointRepository.doSaveUserCreditTradeOrder(tradeAggregate);
        log.info("update user point account point complete, userId:{}, orderId:{}", tradeEntity.getUserId(), userPointOrderEntity.getOrderId());

        return userPointOrderEntity.getOrderId();
    }

    @Override
    public UserPointAccountEntity queryUserPointAccount(String userId) {
        return iPointRepository.queryUserPointAccount(userId);
    }
}
