package org.example.domain.award.adapter.repository;

import org.example.domain.award.model.aggregate.DeliveryAwardAggregate;
import org.example.domain.award.model.aggregate.UserAwardRecordAggregate;
import org.example.domain.award.model.entity.UserAwardRecordEntity;

import java.util.List;

public interface IAwardRepository {
    void doSaveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);

    String queryAwardConfigByAwardId(Long awardId);

    void doSaveDeliveryAwardAggregate(DeliveryAwardAggregate deliveryAwardAggregate);

    String queryAwardKeyByAwardId(Long awardId);

    List<UserAwardRecordEntity> queryUserAwardRecordList(UserAwardRecordEntity userAwardRecordEntity);
}
