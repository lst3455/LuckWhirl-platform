package org.example.domain.rebate.service;

import org.example.domain.rebate.model.entity.BehaviorEntity;
import org.example.domain.rebate.model.entity.BehaviorRebateOrderEntity;

import java.util.List;

public interface IBehaviorRebateService {

    List<String> createRebateOrder(BehaviorEntity behaviorEntity);

    List<BehaviorRebateOrderEntity> queryBehaviorRebateOrderByOutBusinessNo(String userId, String outBusinessNo);
}
