package org.example.domain.activity.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.example.domain.activity.model.aggregate.CreateOrderAggregate;
import org.example.domain.activity.model.entity.*;
import org.example.domain.activity.model.vo.ActivitySkuStockKeyVO;
import org.example.domain.activity.model.vo.OrderStatusVO;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.domain.activity.service.rule.factory.DefaultActivityChainFactory;
import org.example.domain.strategy.service.IRaffleStock;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RaffleActivityService extends AbstractRaffleActivity implements ISkuStock {

    public RaffleActivityService(IActivityRepository iActivityRepository, DefaultActivityChainFactory defaultActivityChainFactory) {
        super(iActivityRepository, defaultActivityChainFactory);
    }

    @Override
    protected void doSaveOrder(CreateOrderAggregate createOrderAggregate) {
        iActivityRepository.doSaveOrder(createOrderAggregate);
    }

    @Override
    protected CreateOrderAggregate buildOrderAggregate(ActivitySkuChargeEntity activitySkuChargeEntity, ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityAmountEntity activityAmountEntity) {
        /** create ActivityOrderEntity object */
        ActivityOrderEntity activityOrderEntity = new ActivityOrderEntity();
        activityOrderEntity.setUserId(activitySkuChargeEntity.getUserId());
        activityOrderEntity.setSku(activitySkuChargeEntity.getSku());
        activityOrderEntity.setActivityId(activityEntity.getActivityId());
        activityOrderEntity.setActivityName(activityEntity.getActivityName());
        activityOrderEntity.setStrategyId(activityEntity.getStrategyId());
        /** here for convenience, use 12 digits random number */
        activityOrderEntity.setOrderId(RandomStringUtils.randomNumeric(12));
        activityOrderEntity.setOrderTime(new Date());
        activityOrderEntity.setTotalAmount(activityAmountEntity.getTotalAmount());
        activityOrderEntity.setDayAmount(activityAmountEntity.getDayAmount());
        activityOrderEntity.setMonthAmount(activityAmountEntity.getMonthAmount());
        activityOrderEntity.setStatus(OrderStatusVO.completed);
        activityOrderEntity.setOutBusinessNo(activitySkuChargeEntity.getOutBusinessNo());

        return CreateOrderAggregate.builder()
                .userId(activitySkuChargeEntity.getUserId())
                .activityId(activityEntity.getActivityId())
                .totalAmount(activityAmountEntity.getTotalAmount())
                .dayAmount(activityAmountEntity.getDayAmount())
                .monthAmount(activityAmountEntity.getMonthAmount())
                .activityOrderEntity(activityOrderEntity)
                .build();
    }

    @Override
    public ActivitySkuStockKeyVO takeQueueValue() throws InterruptedException {
        return iActivityRepository.takeQueueValue();
    }

    @Override
    public void clearQueueValue() {
        iActivityRepository.clearQueueValue();
    }

    @Override
    public void updateActivitySkuStock(Long sku) {
        iActivityRepository.updateActivitySkuStock(sku);
    }

    @Override
    public void clearActivitySkuStock(Long sku) {
        iActivityRepository.clearActivitySkuStock(sku);
    }
}
