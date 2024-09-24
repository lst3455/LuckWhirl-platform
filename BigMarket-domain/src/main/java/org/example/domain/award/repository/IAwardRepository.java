package org.example.domain.award.repository;

import org.example.domain.award.model.aggregate.DeliveryAwardAggregate;
import org.example.domain.award.model.aggregate.UserAwardRecordAggregate;

public interface IAwardRepository {
    void doSaveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);

    String queryAwardConfigByAwardId(Long awardId);

    void doSaveDeliveryAwardAggregate(DeliveryAwardAggregate deliveryAwardAggregate);

    String queryAwardKeyByAwardId(Long awardId);
}
