package org.example.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.domain.activity.event.ActivitySkuStockZeroMessageEvent;
import org.example.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import org.example.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import org.example.domain.activity.model.entity.*;
import org.example.domain.activity.model.vo.ActivitySkuStockKeyVO;
import org.example.domain.activity.model.vo.ActivityStatusVO;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.infrastructure.event.EventPublisher;
import org.example.infrastructure.persistent.dao.*;
import org.example.infrastructure.persistent.po.*;
import org.example.infrastructure.persistent.redis.IRedisService;
import org.example.types.common.Constants;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private ActivitySkuStockZeroMessageEvent activitySkuStockZeroMessageEvent;
    @Resource
    private IRaffleActivityAccountDayDao iRaffleActivityAccountDayDao;
    @Resource
    private IRaffleActivityAccountMonthDao iRaffleActivityAccountMonthDao;


    @Override
    public ActivitySkuEntity queryActivitySkuBySku(Long sku) {
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
    public ActivityEntity queryActivityByActivityId(Long activityId) {
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
    public ActivityAmountEntity queryActivityAmountByActivityAmountId(Long activityAmountId) {
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
    public void doSaveQuotaOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        ActivityOrderEntity activityOrderEntity = createQuotaOrderAggregate.getActivityOrderEntity();
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
        raffleActivityOrder.setTotalAmount(createQuotaOrderAggregate.getTotalAmount());
        raffleActivityOrder.setDayAmount(createQuotaOrderAggregate.getDayAmount());
        raffleActivityOrder.setMonthAmount(createQuotaOrderAggregate.getMonthAmount());
        raffleActivityOrder.setStatus(activityOrderEntity.getStatus().getCode());
        raffleActivityOrder.setOutBusinessNo(activityOrderEntity.getOutBusinessNo());

        /** create RaffleActivityAccount object to store */
        RaffleActivityAccount raffleActivityAccount = new RaffleActivityAccount();
        raffleActivityAccount.setUserId(createQuotaOrderAggregate.getUserId());
        raffleActivityAccount.setActivityId(createQuotaOrderAggregate.getActivityId());
        raffleActivityAccount.setTotalAmount(createQuotaOrderAggregate.getTotalAmount());
        raffleActivityAccount.setTotalRemain(createQuotaOrderAggregate.getTotalAmount());
        raffleActivityAccount.setDayAmount(createQuotaOrderAggregate.getDayAmount());
        raffleActivityAccount.setDayRemain(createQuotaOrderAggregate.getDayAmount());
        raffleActivityAccount.setMonthAmount(createQuotaOrderAggregate.getMonthAmount());
        raffleActivityAccount.setMonthRemain(createQuotaOrderAggregate.getMonthAmount());

        try{
            /** take userId as key to set router */
            idbRouterStrategy.doRouter(createQuotaOrderAggregate.getUserId());
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
                   log.error("save quota order - unique key conflict userId: {} activityId: {} sku: {}", activityOrderEntity.getUserId(), activityOrderEntity.getActivityId(), activityOrderEntity.getSku(), e);
                   throw new AppException(ResponseCode.INDEX_DUPLICATE.getCode(),e);

               }
            });

        }finally {
            idbRouterStrategy.clear();
        }

    }

    @Override
    public void storeActivitySkuStockAmount(Long sku, Integer stockAmount) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_AMOUNT_KEY + sku;
        if (iRedisService.isExists(cacheKey)) return;
        iRedisService.setAtomicLong(cacheKey, Long.valueOf(stockAmount));
    }

    @Override
    public boolean subtractActivitySkuStock(Long sku, Date endDateTime) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_AMOUNT_KEY + sku;
        Long remain = iRedisService.decr(cacheKey);
        if (remain == 0){
            /** send MQ when remain is 0 */
            eventPublisher.publish(activitySkuStockZeroMessageEvent.topic(),activitySkuStockZeroMessageEvent.buildEventMessage(sku));
            /*return false;*/
        } else if (remain < 0) {
            iRedisService.setAtomicLong(cacheKey,0L);
            return false;
        }

        String lockKey = cacheKey + "_" + remain;
        long expireMillis = endDateTime.getTime() - System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
        boolean lock = iRedisService.setNX(lockKey,expireMillis,TimeUnit.MILLISECONDS);
        if (!lock) log.info("lock activity sku stock fail, lockKey: {}",lockKey);
        return lock;
    }

    @Override
    public void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO activitySkuStockKeyVO) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_QUERY_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = iRedisService.getBlockingQueue(cacheKey);
        /** create a delay queue */
        RDelayedQueue<ActivitySkuStockKeyVO> delayedQueue = iRedisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(activitySkuStockKeyVO,3, TimeUnit.SECONDS);
    }

    @Override
    public ActivitySkuStockKeyVO takeQueueValue() {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_QUERY_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> destinationQueue = iRedisService.getBlockingQueue(cacheKey);
        return destinationQueue.poll();
    }

    @Override
    public void clearQueueValue() {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_QUERY_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = iRedisService.getBlockingQueue(cacheKey);
        RDelayedQueue<ActivitySkuStockKeyVO> delayedQueue = iRedisService.getDelayedQueue(blockingQueue);
        delayedQueue.clear();
        blockingQueue.clear();
    }

    @Override
    public void updateActivitySkuStock(Long sku) {
        iRaffleActivitySkuDao.updateActivitySkuStock(sku);
    }

    @Override
    public void clearActivitySkuStock(Long sku) {
        iRaffleActivitySkuDao.clearActivitySkuStock(sku);
    }

    @Override
    public ActivityAccountEntity queryActivityAccountByUserId(String userId, Long activityId) {
        RaffleActivityAccount raffleActivityAccount = new RaffleActivityAccount();
        raffleActivityAccount.setUserId(userId);
        raffleActivityAccount.setActivityId(activityId);
        raffleActivityAccount = iRaffleActivityAccountDao.queryActivityAccountByUserId(raffleActivityAccount);
        if (raffleActivityAccount == null) return null;

        return ActivityAccountEntity.builder()
                .userId(raffleActivityAccount.getUserId())
                .activityId(raffleActivityAccount.getActivityId())
                .totalAmount(raffleActivityAccount.getTotalAmount())
                .totalRemain(raffleActivityAccount.getTotalRemain())
                .dayAmount(raffleActivityAccount.getDayAmount())
                .dayRemain(raffleActivityAccount.getDayRemain())
                .monthAmount(raffleActivityAccount.getMonthAmount())
                .monthRemain(raffleActivityAccount.getMonthRemain())
                .build();

    }

    @Override
    public void doSaveRaffleOrder(CreatePartakeOrderAggregate createPartakeOrderAggregate) {
        try{
            /** get necessary data */
            String userId = createPartakeOrderAggregate.getUserId();
            Long activityId = createPartakeOrderAggregate.getActivityId();
            ActivityAccountEntity activityAccountEntity = createPartakeOrderAggregate.getActivityAccountEntity();
            ActivityAccountMonthEntity activityAccountMonthEntity = createPartakeOrderAggregate.getActivityAccountMonthEntity();
            ActivityAccountDayEntity activityAccountDayEntity = createPartakeOrderAggregate.getActivityAccountDayEntity();
            UserRaffleOrderEntity userRaffleOrderEntity = createPartakeOrderAggregate.getUserRaffleOrderEntity();

            idbRouterStrategy.doRouter(userId);
            transactionTemplate.execute(status -> {
                try{
                    /** first manipulate activity account */
                    int updateTotalRemain = iRaffleActivityAccountDao.updateActivityAccountRemain(
                            RaffleActivityAccount.builder()
                                    .userId(userId)
                                    .activityId(activityId)
                                    .build());
                    if (updateTotalRemain != 1){
                        status.setRollbackOnly();
                        log.error("save raffle order - totalRemain insufficient userId: {} activityId: {}", userId, activityId);
                        throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(),ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
                    }
                    /** then manipulate activity account month*/
                    if (createPartakeOrderAggregate.isExistActivityAccountMonth()) {
                        int updateMonthRemain = iRaffleActivityAccountMonthDao.updateActivityAccountMonthRemain(
                                RaffleActivityAccountMonth.builder()
                                        .userId(userId)
                                        .activityId(activityId)
                                        .month(activityAccountMonthEntity.getMonth())
                                        .build());
                        if (updateMonthRemain != 1) {
                            status.setRollbackOnly();
                            log.warn("save raffle order - monthRemain insufficient userId: {} activityId: {} month: {}", userId, activityId, activityAccountMonthEntity.getMonth());
                            throw new AppException(ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getInfo());
                        }
                    } else {
                        iRaffleActivityAccountMonthDao.insertActivityAccountMonth(RaffleActivityAccountMonth.builder()
                                .userId(activityAccountMonthEntity.getUserId())
                                .activityId(activityAccountMonthEntity.getActivityId())
                                .month(activityAccountMonthEntity.getMonth())
                                .monthAmount(activityAccountMonthEntity.getMonthAmount())
                                .monthRemain(activityAccountMonthEntity.getMonthRemain() - 1)
                                .build());
                        /** if create new RaffleActivityAccountMonth, update monthRemain in RaffleActivityAccount */
                        iRaffleActivityAccountDao.updateActivityAccountMonthRemain(RaffleActivityAccount.builder()
                                .userId(userId)
                                .activityId(activityId)
                                .monthRemain(activityAccountEntity.getMonthRemain())
                                .build());
                    }
                    /** then manipulate activity day */
                    if (createPartakeOrderAggregate.isExistActivityAccountDay()) {
                        int updateDayAmount = iRaffleActivityAccountDayDao.updateActivityAccountDayRemain(RaffleActivityAccountDay.builder()
                                .userId(userId)
                                .activityId(activityId)
                                .day(activityAccountDayEntity.getDay())
                                .build());
                        if (updateDayAmount != 1) {
                            status.setRollbackOnly();
                            log.warn("save raffle order - dayRemain insufficient userId: {} activityId: {} day: {}", userId, activityId, activityAccountDayEntity.getDay());
                            throw new AppException(ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getInfo());
                        }
                    } else {
                        iRaffleActivityAccountDayDao.insertActivityAccountDay(RaffleActivityAccountDay.builder()
                                .userId(activityAccountDayEntity.getUserId())
                                .activityId(activityAccountDayEntity.getActivityId())
                                .day(activityAccountDayEntity.getDay())
                                .dayAmount(activityAccountDayEntity.getDayAmount())
                                .dayRemain(activityAccountDayEntity.getDayRemain() - 1)
                                .build());
                        /** if create new RaffleActivityAccountDay, update dayRemain in RaffleActivityAccount */
                        iRaffleActivityAccountDao.updateActivityAccountDayRemain(RaffleActivityAccount.builder()
                                .userId(userId)
                                .activityId(activityId)
                                .dayRemain(activityAccountEntity.getDayRemain())
                                .build());
                    }



                }catch (DuplicateKeyException e){
                    status.setRollbackOnly();
                    log.error("save raffle order - unique key conflict userId: {} activityId: {}", userId, activityId, e);
                    throw new AppException(ResponseCode.INDEX_DUPLICATE.getCode(),e);
                }
                return 1;
            });
        }finally {
            idbRouterStrategy.clear();
        }
    }

    @Override
    public ActivityAccountMonthEntity queryActivityAccountMonthByUserId(String userId, Long activityId, String month) {
        return null;
    }

    @Override
    public ActivityAccountDayEntity queryActivityAccountDayByUserId(String userId, Long activityId, String day) {
        return null;
    }


}
