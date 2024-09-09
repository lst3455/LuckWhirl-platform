package org.example.domain.award.repository;

import org.example.domain.award.model.aggregate.UserAwardRecordAggregate;

public interface IAwardRepository {
    void doSaveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);
}
