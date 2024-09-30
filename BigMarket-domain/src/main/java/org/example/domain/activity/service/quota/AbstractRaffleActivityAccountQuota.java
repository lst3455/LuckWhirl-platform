package org.example.domain.activity.service.quota;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import org.example.domain.activity.model.entity.*;
import org.example.domain.activity.model.vo.OrderTradeTypeVO;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.domain.activity.service.IRaffleActivityAccountQuotaService;
import org.example.domain.activity.service.armory.ActivityArmory;
import org.example.domain.activity.service.quota.policy.ITradePolicy;
import org.example.domain.activity.service.quota.rule.IActionChain;
import org.example.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;
import org.example.domain.point.model.entity.UserPointAccountEntity;
import org.example.domain.point.repository.IPointRepository;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
public abstract class AbstractRaffleActivityAccountQuota implements IRaffleActivityAccountQuotaService {

    protected IActivityRepository iActivityRepository;

    protected DefaultActivityChainFactory defaultActivityChainFactory;

    protected ActivityArmory activityArmory;

    protected Map<String, ITradePolicy> tradePolicyMap;

    protected IPointRepository iPointRepository;

    public AbstractRaffleActivityAccountQuota(IActivityRepository iActivityRepository, DefaultActivityChainFactory defaultActivityChainFactory, ActivityArmory activityArmory, Map<String, ITradePolicy> tradePolicyMap, IPointRepository iPointRepository) {
        this.iActivityRepository = iActivityRepository;
        this.defaultActivityChainFactory = defaultActivityChainFactory;
        this.activityArmory = activityArmory;
        this.tradePolicyMap = tradePolicyMap;
        this.iPointRepository = iPointRepository;
    }

    @Override
    public ActivityOrderEntity createActivityOrder(ActivityShopCartEntity activityShopCartEntity) {

        ActivitySkuEntity activitySkuEntity = iActivityRepository.queryActivitySkuBySku(activityShopCartEntity.getSku());
        ActivityEntity activityEntity = iActivityRepository.queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        ActivityAmountEntity activityAmountEntity = iActivityRepository.queryActivityAmountByActivityAmountId(activitySkuEntity.getActivityAmountId());
        log.info("query result: {}, {}, {}", JSON.toJSONString(activitySkuEntity),JSON.toJSONString(activityEntity),JSON.toJSONString(activityAmountEntity));

        return ActivityOrderEntity.builder().build();
    }

    @Override
    public PendingActivityOrderEntity createSkuChargeOrder(ActivitySkuChargeEntity activitySkuChargeEntity) {
        /** validate the data first */
        String userId = activitySkuChargeEntity.getUserId();
        Long sku = activitySkuChargeEntity.getSku();
        String outBusinessNo = activitySkuChargeEntity.getOutBusinessNo();
        if (userId == null || sku == null || outBusinessNo == null) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        /** check if exist pending activity order */
        PendingActivityOrderEntity pendingActivityOrderEntity = iActivityRepository.queryPendingActivityOrder(activitySkuChargeEntity);
        if (pendingActivityOrderEntity != null) return pendingActivityOrderEntity;

        /** get sku entity */
        ActivitySkuEntity activitySkuEntity = iActivityRepository.queryActivitySkuBySku(activitySkuChargeEntity.getSku());
        /** armory sku amount */
        activityArmory.assembleActivitySku(activitySkuEntity.getSku());
        ActivityEntity activityEntity = iActivityRepository.queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        ActivityAmountEntity activityAmountEntity = iActivityRepository.queryActivityAmountByActivityAmountId(activitySkuEntity.getActivityAmountId());
        log.info("query result: {}, {}, {}", JSON.toJSONString(activitySkuEntity),JSON.toJSONString(activityEntity),JSON.toJSONString(activityAmountEntity));

        /** check available point */
        if (OrderTradeTypeVO.pay_trade.equals(activitySkuChargeEntity.getOrderTradeTypeVO())){
            UserPointAccountEntity userPointAccountEntity = iPointRepository.queryUserPointAccount(userId);
            BigDecimal availablePoint = userPointAccountEntity.getAvailableAmount();
            if (availablePoint.compareTo(activitySkuEntity.getPointAmount()) < 0) {
                throw new AppException(ResponseCode.USER_POINT_ACCOUNT_NO_ENOUGH.getCode(), ResponseCode.USER_POINT_ACCOUNT_NO_ENOUGH.getInfo());
            }
        }

        /** open the activity chain */
        IActionChain iActionChain = defaultActivityChainFactory.openActionChain();

        /** if fail to pass all chain node, it will return false */
        boolean success = iActionChain.action(activitySkuEntity, activityEntity, activityAmountEntity);
        if(!success) throw new AppException(ResponseCode.ACTIVITY_CHAIN_TAKE_OVER.getCode(),ResponseCode.ACTIVITY_CHAIN_TAKE_OVER.getInfo());

        CreateQuotaOrderAggregate createQuotaOrderAggregate = buildOrderAggregate(activitySkuChargeEntity,activitySkuEntity, activityEntity, activityAmountEntity);

        /** get the sku trade type */
        OrderTradeTypeVO orderTradeTypeVO = activitySkuChargeEntity.getOrderTradeTypeVO();
        ITradePolicy iTradePolicy = tradePolicyMap.get(orderTradeTypeVO.getCode());
        iTradePolicy.doSaveQuotaOrder(createQuotaOrderAggregate);

        /** convert new create ActivityOrderEntity to PendingActivityOrderEntity to return */
        ActivityOrderEntity activityOrderEntity = createQuotaOrderAggregate.getActivityOrderEntity();
        return PendingActivityOrderEntity.builder()
                .userId(activityOrderEntity.getUserId())
                .orderId(activityOrderEntity.getOrderId())
                .outBusinessNo(activityOrderEntity.getOutBusinessNo())
                .pointAmount(activityOrderEntity.getPointAmount())
                .build();
    }

    @Deprecated
    protected abstract void doSaveQuotaOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    protected abstract CreateQuotaOrderAggregate buildOrderAggregate(ActivitySkuChargeEntity activitySkuChargeEntity, ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityAmountEntity activityAmountEntity);
}
