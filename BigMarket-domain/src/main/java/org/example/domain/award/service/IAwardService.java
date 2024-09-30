package org.example.domain.award.service;

import org.example.domain.award.model.entity.DeliveryAwardEntity;
import org.example.domain.award.model.entity.UserAwardRecordEntity;

import java.util.List;

public interface IAwardService {

    void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity);

    void deliveryAward(DeliveryAwardEntity deliveryAwardEntity);

    List<UserAwardRecordEntity> queryUserAwardRecordList(UserAwardRecordEntity userAwardRecordEntity);
}
