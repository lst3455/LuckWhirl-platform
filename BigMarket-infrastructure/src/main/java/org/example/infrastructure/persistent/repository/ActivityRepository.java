package org.example.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.aggregate.CreateOrderAggregate;
import org.example.domain.activity.model.entity.ActivityAmountEntity;
import org.example.domain.activity.model.entity.ActivityEntity;
import org.example.domain.activity.model.entity.ActivityOrderEntity;
import org.example.domain.activity.model.entity.ActivitySkuEntity;
import org.example.domain.activity.model.vo.ActivityStatusVO;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.infrastructure.persistent.dao.*;
import org.example.infrastructure.persistent.po.*;
import org.example.infrastructure.persistent.redis.IRedisService;
import org.example.types.common.Constants;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

@Slf4j
@Repository
public class ActivityRepository implements IActivityRepository {

    @Resource
    private IRedisService iRedisService;
    @Resource
    private IRaffleActivityDao iRaffleActivityDao;
    @Resource
    private IRaffleActivitySkuDao iRaffleActivitySkuDao;
    @Resource
    private IRaffleActivityAmountDao iRaffleActivityAmountDao;
    @Resource
    private IRaffleActivityOrderDao iRaffleActivityOrderDao;
    @Resource
    private IRaffleActivityAccountDao iRaffleActivityAccountDao;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private IDBRouterStrategy idbRouterStrategy;


    @Override
    public ActivitySkuEntity queryActivitySkuEntity(Long sku) {
        RaffleActivitySku raffleActivitySku = iRaffleActivitySkuDao.queryRaffleActivitySkuBySku(sku);
        return ActivitySkuEntity.builder()
                .sku(raffleActivitySku.getSku())
                .activityId(raffleActivitySku.getActivityId())
                .activityAmountId(raffleActivitySku.getActivityAmountId())
                .stockAmount(raffleActivitySku.getStockAmount())
                .stockRemain(raffleActivitySku.getStockRemain())
                .build();
    }

    @Override
    public ActivityEntity queryActivityByActivityEntityById(Long activityId) {
        /** first get data from cache */
        String cacheKey = Constants.RedisKey.ACTIVITY_KEY + activityId;
        ActivityEntity activityEntity = iRedisService.getValue(cacheKey);
        if (null != activityEntity) return activityEntity;
        /** then get data from database */
        RaffleActivity raffleActivity = iRaffleActivityDao.queryRaffleActivityByActivityId(activityId);
        activityEntity = ActivityEntity.builder()
                .activityId(raffleActivity.getActivityId())
                .activityName(raffleActivity.getActivityName())
                .activityDesc(raffleActivity.getActivityDesc())
                .beginDateTime(raffleActivity.getBeginDateTime())
                .endDateTime(raffleActivity.getEndDateTime())
                .strategyId(raffleActivity.getStrategyId())
                .status(ActivityStatusVO.valueOf(raffleActivity.getStatus()))
                .build();
        iRedisService.setValue(cacheKey, activityEntity);
        return activityEntity;
    }

    @Override
    public ActivityAmountEntity queryActivityAmountEntityByActivityAmountId(Long activityAmountId) {
        /** first get data from cache */
        String cacheKey = Constants.RedisKey.ACTIVITY_AMOUNT_KEY + activityAmountId;
        ActivityAmountEntity activityAmountEntity = iRedisService.getValue(cacheKey);
        if (null != activityAmountEntity) return activityAmountEntity;
        /** then get data from database */
        RaffleActivityAmount raffleActivityAmount = iRaffleActivityAmountDao.queryRaffleActivityAmountByActivityAmountId(activityAmountId);
        activityAmountEntity = ActivityAmountEntity.builder()
                .activityAmountId(raffleActivityAmount.getActivityAmountId())
                .totalAmount(raffleActivityAmount.getTotalAmount())
                .dayAmount(raffleActivityAmount.getDayAmount())
                .monthAmount(raffleActivityAmount.getMonthAmount())
                .build();
        iRedisService.setValue(cacheKey, activityAmountEntity);
        return activityAmountEntity;
    }

    @Override
    public void doSaveOrder(CreateOrderAggregate createOrderAggregate) {
        ActivityOrderEntity activityOrderEntity = createOrderAggregate.getActivityOrderEntity();
        /** convert domain object to persistent object */
        RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
        raffleActivityOrder.setUserId(activityOrderEntity.getUserId());
        raffleActivityOrder.setSku(activityOrderEntity.getSku());
        raffleActivityOrder.setActivityId(activityOrderEntity.getActivityId());
        raffleActivityOrder.setActivityName(activityOrderEntity.getActivityName());
        raffleActivityOrder.setStrategyId(activityOrderEntity.getStrategyId());
        raffleActivityOrder.setOrderId(activityOrderEntity.getOrderId());
        raffleActivityOrder.setOrderTime(activityOrderEntity.getOrderTime());
        raffleActivityOrder.setTotalAmount(activityOrderEntity.getTotalAmount());
        raffleActivityOrder.setDayAmount(activityOrderEntity.getDayAmount());
        raffleActivityOrder.setMonthAmount(activityOrderEntity.getMonthAmount());
        raffleActivityOrder.setTotalAmount(createOrderAggregate.getTotalAmount());
        raffleActivityOrder.setDayAmount(createOrderAggregate.getDayAmount());
        raffleActivityOrder.setMonthAmount(createOrderAggregate.getMonthAmount());
        raffleActivityOrder.setStatus(activityOrderEntity.getStatus().getCode());
        raffleActivityOrder.setOutBusinessNo(activityOrderEntity.getOutBusinessNo());

        /** create RaffleActivityAccount object to store */
        RaffleActivityAccount raffleActivityAccount = new RaffleActivityAccount();
        raffleActivityAccount.setUserId(createOrderAggregate.getUserId());
        raffleActivityAccount.setActivityId(createOrderAggregate.getActivityId());
        raffleActivityAccount.setTotalAmount(createOrderAggregate.getTotalAmount());
        raffleActivityAccount.setTotalRemain(createOrderAggregate.getTotalAmount());
        raffleActivityAccount.setDayAmount(createOrderAggregate.getDayAmount());
        raffleActivityAccount.setDayRemain(createOrderAggregate.getDayAmount());
        raffleActivityAccount.setMonthAmount(createOrderAggregate.getMonthAmount());
        raffleActivityAccount.setMonthRemain(createOrderAggregate.getMonthAmount());

        try{
            /** take userId as key to set router */
            idbRouterStrategy.doRouter(createOrderAggregate.getUserId());
            transactionTemplate.execute(status -> {
               try{
                   /** insert raffleActivityOrder */
                   iRaffleActivityOrderDao.insert(raffleActivityOrder);
                   /** update account */
                   int amount = iRaffleActivityAccountDao.updateAccountQuota(raffleActivityAccount);
                   /** amount == 0, means account does not exist, create new account */
                   if (amount == 0) iRaffleActivityAccountDao.insert(raffleActivityAccount);
                   return 1;
               }catch (DuplicateKeyException e){
                   status.setRollbackOnly();
                   log.error("save order - unique key conflict userId: {} activityId: {} sku: {}", activityOrderEntity.getUserId(), activityOrderEntity.getActivityId(), activityOrderEntity.getSku(), e);
                   throw new AppException(ResponseCode.INDEX_DUPLICATE.getCode());

               }
            });

        }finally {
            idbRouterStrategy.clear();
        }

    }

}
