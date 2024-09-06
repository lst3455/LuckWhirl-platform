package org.example.domain.activity.service.quota;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import org.example.domain.activity.model.entity.*;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.domain.activity.service.IRaffleActivityAccountQuotaService;
import org.example.domain.activity.service.quota.rule.IActionChain;
import org.example.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;

@Slf4j
public abstract class AbstractRaffleActivityAccountQuota implements IRaffleActivityAccountQuotaService {

    protected IActivityRepository iActivityRepository;

    protected DefaultActivityChainFactory defaultActivityChainFactory;

    public AbstractRaffleActivityAccountQuota(IActivityRepository iActivityRepository, DefaultActivityChainFactory defaultActivityChainFactory) {
        this.iActivityRepository = iActivityRepository;
        this.defaultActivityChainFactory = defaultActivityChainFactory;
    }

    @Override
    public ActivityOrderEntity createActivityOrder(ActivityShopCartEntity activityShopCartEntity) {

        ActivitySkuEntity activitySkuEntity = iActivityRepository.queryActivitySkuBySku(activityShopCartEntity.getSku());
        ActivityEntity activityEntity = iActivityRepository.queryActivityByActivityId(activitySkuEntity.getActivityId());
        ActivityAmountEntity activityAmountEntity = iActivityRepository.queryActivityAmountByActivityAmountId(activitySkuEntity.getActivityAmountId());
        log.info("query result: {} {} {}", JSON.toJSONString(activitySkuEntity),JSON.toJSONString(activityEntity),JSON.toJSONString(activityAmountEntity));

        return ActivityOrderEntity.builder().build();
    }

    @Override
    public String createSkuChargeOrder(ActivitySkuChargeEntity activitySkuChargeEntity) {
        /** validate the data first */
        String userId = activitySkuChargeEntity.getUserId();
        Long sku = activitySkuChargeEntity.getSku();
        String outBusinessNo = activitySkuChargeEntity.getOutBusinessNo();
        if (userId == null || sku == null || outBusinessNo == null) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        /** get sku entity */
        ActivitySkuEntity activitySkuEntity = iActivityRepository.queryActivitySkuBySku(activitySkuChargeEntity.getSku());
        ActivityEntity activityEntity = iActivityRepository.queryActivityByActivityId(activitySkuEntity.getActivityId());
        ActivityAmountEntity activityAmountEntity = iActivityRepository.queryActivityAmountByActivityAmountId(activitySkuEntity.getActivityAmountId());
        log.info("query result: {} {} {}", JSON.toJSONString(activitySkuEntity),JSON.toJSONString(activityEntity),JSON.toJSONString(activityAmountEntity));

        /** open the activity chain */
        IActionChain iActionChain = defaultActivityChainFactory.openActionChain();

        /** if fail to pass all chain node, it will return false */
        boolean success = iActionChain.action(activitySkuEntity, activityEntity, activityAmountEntity);
        if(!success) throw new AppException(ResponseCode.ACTIVITY_CHAIN_TAKE_OVER.getCode(),ResponseCode.ACTIVITY_CHAIN_TAKE_OVER.getInfo());

        CreateQuotaOrderAggregate createQuotaOrderAggregate = buildOrderAggregate(activitySkuChargeEntity,activitySkuEntity, activityEntity, activityAmountEntity);

        doSaveQuotaOrder(createQuotaOrderAggregate);

        return createQuotaOrderAggregate.getActivityOrderEntity().getOrderId();
    }

    protected abstract void doSaveQuotaOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    protected abstract CreateQuotaOrderAggregate buildOrderAggregate(ActivitySkuChargeEntity activitySkuChargeEntity, ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityAmountEntity activityAmountEntity);
}
