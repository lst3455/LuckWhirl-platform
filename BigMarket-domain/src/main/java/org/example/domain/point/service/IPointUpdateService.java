package org.example.domain.point.service;

import org.example.domain.point.model.entity.TradeEntity;
import org.example.domain.point.model.entity.UserPointAccountEntity;

public interface IPointUpdateService {
    String createPayTypeUserPointOrder(TradeEntity tradeEntity);

    String createNonPayTypeUserPointOrder(TradeEntity tradeEntity);

    UserPointAccountEntity queryUserPointAccount(String userId);

}
