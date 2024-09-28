package org.example.domain.activity.service.quota.policy.impl;

import org.example.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import org.example.domain.activity.model.entity.ActivityOrderEntity;
import org.example.domain.activity.model.vo.OrderStatusVO;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.domain.activity.service.quota.policy.ITradePolicy;
import org.springframework.stereotype.Service;

@Service("pay_trade")
public class PayTradePolicy implements ITradePolicy {

    private final IActivityRepository iActivityRepository;

    public PayTradePolicy(IActivityRepository iActivityRepository) {
        this.iActivityRepository = iActivityRepository;
    }

    @Override
    public void doSaveQuotaOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        ActivityOrderEntity activityOrderEntity = createQuotaOrderAggregate.getActivityOrderEntity();
        activityOrderEntity.setStatus(OrderStatusVO.pending);
        iActivityRepository.doSavePayTypeQuotaOrder(createQuotaOrderAggregate);

    }
}
