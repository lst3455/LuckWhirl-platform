package org.example.domain.rebate.service;

import org.example.domain.rebate.model.entity.BehaviorEntity;

import java.util.List;

public interface IBehaviorRebateService {

    List<String> createRebateOrder(BehaviorEntity behaviorEntity);
}
