package org.example.domain.point.service;

import org.example.domain.point.model.entity.TradeEntity;
import org.example.domain.point.model.entity.UserPointAccountEntity;

public interface IPointUpdateService {
    String createUserPointOrder(TradeEntity tradeEntity);

    UserPointAccountEntity queryUserPointAccount(String userId);
}
