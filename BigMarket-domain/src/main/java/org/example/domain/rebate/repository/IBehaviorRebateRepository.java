package org.example.domain.rebate.repository;

import org.example.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import org.example.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import org.example.domain.rebate.model.vo.BehaviorTypeVO;
import org.example.domain.rebate.model.vo.DailyBehaviorRebateVO;

import java.util.List;

public interface IBehaviorRebateRepository {

    List<DailyBehaviorRebateVO> queryDailyBehaviorRebateConfig(BehaviorTypeVO behaviorTypeVO);

    void doSaveUserRebateOrder(String userId, List<BehaviorRebateAggregate> behaviorRebateAggregateList);

    List<BehaviorRebateOrderEntity> queryBehaviorRebateOrderByOutBusinessNo(String userId, String outBusinessNo);
}
