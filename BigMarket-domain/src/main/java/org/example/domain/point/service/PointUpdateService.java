package org.example.domain.point.service;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.point.model.aggregate.TradeAggregate;
import org.example.domain.point.model.entity.TradeEntity;
import org.example.domain.point.model.entity.UserPointAccountEntity;
import org.example.domain.point.model.entity.UserPointOrderEntity;
import org.example.domain.point.repository.IPointRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class PointUpdateService implements IPointUpdateService{

    @Resource
    private IPointRepository iPointRepository;

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

        /** build the aggregate object */
        TradeAggregate tradeAggregate = TradeAggregate.builder()
                .userId(tradeEntity.getUserId())
                .userPointAccountEntity(userPointAccountEntity)
                .userPointOrderEntity(userPointOrderEntity)
                .build();

        iPointRepository.doSaveUserCreditTradeOrder(tradeAggregate);
        log.info("update user point account point complete, userId:{}, orderId:{}", tradeEntity.getUserId(), userPointOrderEntity.getOrderId());

        return userPointOrderEntity.getOrderId();
    }
}
