package org.example.domain.activity.service.quota.policy;

import org.example.domain.activity.model.aggregate.CreateQuotaOrderAggregate;

public interface ITradePolicy {
    void doSaveQuotaOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);
}
