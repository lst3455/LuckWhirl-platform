package org.example.domain.activity.service.quota;

import org.apache.commons.lang3.RandomStringUtils;
import org.example.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import org.example.domain.activity.model.entity.*;
import org.example.domain.activity.model.vo.ActivitySkuStockKeyVO;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.domain.activity.service.IRaffleActivitySkuStockService;
import org.example.domain.activity.service.armory.ActivityArmory;
import org.example.domain.activity.service.quota.policy.ITradePolicy;
import org.example.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;
import org.example.domain.point.repository.IPointRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class RaffleActivityAccountQuotaService extends AbstractRaffleActivityAccountQuota implements IRaffleActivitySkuStockService {

    public RaffleActivityAccountQuotaService(IActivityRepository iActivityRepository, DefaultActivityChainFactory defaultActivityChainFactory, ActivityArmory activityArmory, Map<String, ITradePolicy> tradePolicyMap, IPointRepository iPointRepository) {
        super(iActivityRepository, defaultActivityChainFactory, activityArmory, tradePolicyMap, iPointRepository);
    }

    @Override
    protected void doSaveQuotaOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        iActivityRepository.doSaveNonPayTypeQuotaOrder(createQuotaOrderAggregate);
    }

    @Override
    protected CreateQuotaOrderAggregate buildOrderAggregate(ActivitySkuChargeEntity activitySkuChargeEntity, ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityAmountEntity activityAmountEntity) {
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
        /*activityOrderEntity.setStatus(OrderStatusVO.completed);*/
        activityOrderEntity.setPointAmount(activitySkuEntity.getPointAmount());
        activityOrderEntity.setOutBusinessNo(activitySkuChargeEntity.getOutBusinessNo());

        return CreateQuotaOrderAggregate.builder()
                .userId(activitySkuChargeEntity.getUserId())
                .activityId(activityEntity.getActivityId())
                .totalAmount(activityAmountEntity.getTotalAmount())
                .dayAmount(activityAmountEntity.getDayAmount())
                .monthAmount(activityAmountEntity.getMonthAmount())
                .activityOrderEntity(activityOrderEntity)
                .build();
    }

    @Override
    public ActivitySkuStockKeyVO takeQueueValue(Long sku) throws InterruptedException {
        return iActivityRepository.takeQueueValue(sku);
    }

    @Override
    public void clearQueueValue(Long sku) {
        iActivityRepository.clearQueueValue(sku);
    }

    @Override
    public void updateActivitySkuStock(Long sku) {
        iActivityRepository.updateActivitySkuStock(sku);
    }

    @Override
    public void clearActivitySkuStock(Long sku) {
        iActivityRepository.clearActivitySkuStock(sku);
    }


    @Override
    public void updateActivityOrder(DeliveryOrderEntity deliveryOrderEntity) {
        iActivityRepository.updateActivityOrder(deliveryOrderEntity);
    }

    @Override
    public Integer queryRaffleActivityAccountDayPartakeAmount(String userId, Long activityId) {
        return iActivityRepository.queryRaffleActivityAccountDayPartakeAmount(userId,activityId);
    }

    @Override
    public ActivityAccountEntity queryActivityAccountEntity(String userId, Long activityId) {
        return iActivityRepository.queryActivityAccountEntity(userId,activityId);
    }

    @Override
    public Integer queryRaffleActivityAccountPartakeAmount(String userId, Long activityId) {
        return iActivityRepository.queryRaffleActivityAccountPartakeAmount(userId,activityId);
    }
}
