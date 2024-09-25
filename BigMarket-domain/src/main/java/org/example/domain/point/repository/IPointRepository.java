package org.example.domain.point.repository;

import org.example.domain.point.model.aggregate.TradeAggregate;

public interface IPointRepository {

    String createUserPointOrder();

    void doSaveUserCreditTradeOrder(TradeAggregate tradeAggregate);
}
