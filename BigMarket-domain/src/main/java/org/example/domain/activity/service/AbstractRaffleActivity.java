package org.example.domain.activity.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.domain.activity.model.aggregate.CreateOrderAggregate;
import org.example.domain.activity.model.entity.*;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.domain.activity.service.rule.IActionChain;
import org.example.domain.activity.service.rule.factory.DefaultActivityChainFactory;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;

@Slf4j
public abstract class AbstractRaffleActivity implements IRaffleOrder{

    protected IActivityRepository iActivityRepository;

    protected DefaultActivityChainFactory defaultActivityChainFactory;

    public AbstractRaffleActivity(IActivityRepository iActivityRepository, DefaultActivityChainFactory defaultActivityChainFactory) {
        this.iActivityRepository = iActivityRepository;
        this.defaultActivityChainFactory = defaultActivityChainFactory;
    }

    @Override
    public ActivityOrderEntity createActivityOrder(ActivityShopCartEntity activityShopCartEntity) {

        ActivitySkuEntity activitySkuEntity = iActivityRepository.queryActivitySkuEntity(activityShopCartEntity.getSku());
        ActivityEntity activityEntity = iActivityRepository.queryActivityByActivityEntityById(activitySkuEntity.getActivityId());
        ActivityAmountEntity activityAmountEntity = iActivityRepository.queryActivityAmountEntityByActivityAmountId(activitySkuEntity.getActivityAmountId());
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
        ActivitySkuEntity activitySkuEntity = iActivityRepository.queryActivitySkuEntity(activitySkuChargeEntity.getSku());
        ActivityEntity activityEntity = iActivityRepository.queryActivityByActivityEntityById(activitySkuEntity.getActivityId());
        ActivityAmountEntity activityAmountEntity = iActivityRepository.queryActivityAmountEntityByActivityAmountId(activitySkuEntity.getActivityAmountId());
        log.info("query result: {} {} {}", JSON.toJSONString(activitySkuEntity),JSON.toJSONString(activityEntity),JSON.toJSONString(activityAmountEntity));

        /** open the activity chain */
        IActionChain iActionChain = defaultActivityChainFactory.openActionChain();
        boolean success = iActionChain.action(activitySkuEntity, activityEntity, activityAmountEntity);

        CreateOrderAggregate createOrderAggregate = buildOrderAggregate(activitySkuChargeEntity,activitySkuEntity, activityEntity, activityAmountEntity);

        doSaveOrder(createOrderAggregate);

        return createOrderAggregate.getActivityOrderEntity().getOrderId();
    }

    protected abstract void doSaveOrder(CreateOrderAggregate createOrderAggregate);

    protected abstract CreateOrderAggregate buildOrderAggregate(ActivitySkuChargeEntity activitySkuChargeEntity, ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityAmountEntity activityAmountEntity);
}
