package org.example.domain.point.repository;

import org.example.domain.point.model.aggregate.TradeAggregate;
import org.example.domain.point.model.entity.UserPointAccountEntity;

public interface IPointRepository {

    String createUserPointOrder();

    void doSaveUserCreditTradeOrder(TradeAggregate tradeAggregate);

    UserPointAccountEntity queryUserPointAccount(String userId);
}
