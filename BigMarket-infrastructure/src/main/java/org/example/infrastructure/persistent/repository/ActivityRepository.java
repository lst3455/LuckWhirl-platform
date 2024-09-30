package org.example.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.event.ActivitySkuStockZeroMessageEvent;
import org.example.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import org.example.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import org.example.domain.activity.model.entity.*;
import org.example.domain.activity.model.vo.ActivitySkuStockKeyVO;
import org.example.domain.activity.model.vo.ActivityStatusVO;
import org.example.domain.activity.model.vo.UserRaffleOrderStatusVO;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private IUserRaffleOrderDao iUserRaffleOrderDao;
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
        /** first get data from cache */
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_KEY + sku;
        ActivitySkuEntity activitySkuEntity = iRedisService.getValue(cacheKey);
        if (activitySkuEntity != null) return activitySkuEntity;
        /** then get data from database */
        RaffleActivitySku raffleActivitySku = iRaffleActivitySkuDao.queryRaffleActivitySkuBySku(sku);
        activitySkuEntity = ActivitySkuEntity.builder()
                .sku(raffleActivitySku.getSku())
                .activityId(raffleActivitySku.getActivityId())
                .activityAmountId(raffleActivitySku.getActivityAmountId())
                .stockAmount(raffleActivitySku.getStockAmount())
                .stockRemain(raffleActivitySku.getStockRemain())
                .pointAmount(raffleActivitySku.getPointAmount())
                .build();
        iRedisService.setValue(cacheKey, activitySkuEntity);
        return activitySkuEntity;
    }

    @Override
    public ActivityEntity queryRaffleActivityByActivityId(Long activityId) {
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
    public void doSaveNonPayTypeQuotaOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
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
        raffleActivityOrder.setStatus(activityOrderEntity.getStatus().getCode());
        raffleActivityOrder.setPointAmount(activityOrderEntity.getPointAmount());
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

        /** create RaffleActivityAccountMonth object to store */
        RaffleActivityAccountMonth raffleActivityAccountMonth = new RaffleActivityAccountMonth();
        raffleActivityAccountMonth.setUserId(createQuotaOrderAggregate.getUserId());
        raffleActivityAccountMonth.setActivityId(createQuotaOrderAggregate.getActivityId());
        raffleActivityAccountMonth.setMonth(raffleActivityAccountMonth.currentMonth());
        raffleActivityAccountMonth.setMonthAmount(createQuotaOrderAggregate.getMonthAmount());
        raffleActivityAccountMonth.setMonthRemain(createQuotaOrderAggregate.getMonthAmount());

        /** create RaffleActivityAccountDay object to store */
        RaffleActivityAccountDay raffleActivityAccountDay = new RaffleActivityAccountDay();
        raffleActivityAccountDay.setUserId(createQuotaOrderAggregate.getUserId());
        raffleActivityAccountDay.setActivityId(createQuotaOrderAggregate.getActivityId());
        raffleActivityAccountDay.setDay(raffleActivityAccountDay.currentDay());
        raffleActivityAccountDay.setDayAmount(createQuotaOrderAggregate.getDayAmount());
        raffleActivityAccountDay.setDayRemain(createQuotaOrderAggregate.getDayAmount());

        try {
            /** take userId as key to set router */
            idbRouterStrategy.doRouter(createQuotaOrderAggregate.getUserId());
            transactionTemplate.execute(status -> {
                try {
                    /** insert raffleActivityOrder */
                    iRaffleActivityOrderDao.insert(raffleActivityOrder);
                    /** update account */
                    int amount = iRaffleActivityAccountDao.updateAccountQuota(raffleActivityAccount);
                    /** amount == 0, means account does not exist, create new account */
                    if (amount == 0) iRaffleActivityAccountDao.insertActivityAccount(raffleActivityAccount);

                    /** update day account */
                    int dayAmount = iRaffleActivityAccountDayDao.updateAccountDayQuota(raffleActivityAccountDay);
                    /** dayAmount == 0, means day account does not exist, create new account */
                    if (dayAmount == 0) iRaffleActivityAccountDayDao.insertActivityAccountDay(raffleActivityAccountDay);

                    /** update month account */
                    int monthAmount = iRaffleActivityAccountMonthDao.updateAccountMonthQuota(raffleActivityAccountMonth);
                    /** monthAmount == 0, means month account does not exist, create new account */
                    if (monthAmount == 0)
                        iRaffleActivityAccountMonthDao.insertActivityAccountMonth(raffleActivityAccountMonth);

                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("save quota order - unique key conflict userId: {} activityId: {} sku: {}", activityOrderEntity.getUserId(), activityOrderEntity.getActivityId(), activityOrderEntity.getSku(), e);
                    throw new AppException(ResponseCode.INDEX_DUPLICATE.getCode(), e);

                }
            });
        } finally {
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
        if (!iRedisService.isExists(cacheKey)) {
            throw new AppException(ResponseCode.CACHEKEY_NOT_EXIST.getCode(), ResponseCode.CACHEKEY_NOT_EXIST.getInfo());
        }
        Long remain = iRedisService.decr(cacheKey);
        if (remain == 0) {
            /** send MQ when remain is 0 */
            eventPublisher.publish(activitySkuStockZeroMessageEvent.topic(), activitySkuStockZeroMessageEvent.buildEventMessage(sku));
            /*return false;*/
        } else if (remain < 0) {
            iRedisService.setAtomicLong(cacheKey, 0L);
            return false;
        }

        String lockKey = cacheKey + "_" + remain;
        long expireMillis = endDateTime.getTime() - System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
        boolean lock = iRedisService.setNX(lockKey, expireMillis, TimeUnit.MILLISECONDS);
        if (!lock) log.info("lock activity sku stock fail, lockKey: {}", lockKey);
        return lock;
    }

    @Override
    public void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO activitySkuStockKeyVO) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_QUERY_KEY + activitySkuStockKeyVO.getSku();
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = iRedisService.getBlockingQueue(cacheKey);
        /** create a delay queue */
        RDelayedQueue<ActivitySkuStockKeyVO> delayedQueue = iRedisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(activitySkuStockKeyVO, 3, TimeUnit.SECONDS);
        /** add to queueList to store all used sku */
        ActivitySkuStockKeyVO.blockingQueueSkuSet.add(activitySkuStockKeyVO.getSku());
    }

    @Override
    public ActivitySkuStockKeyVO takeQueueValue(Long sku) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_QUERY_KEY + sku;
        RBlockingQueue<ActivitySkuStockKeyVO> destinationQueue = iRedisService.getBlockingQueue(cacheKey);
        return destinationQueue.poll();
    }

    @Override
    public void clearQueueValue(Long sku) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_QUERY_KEY + sku;
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
        /** check activity account exist */
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
        try {
            /** get necessary data */
            String userId = createPartakeOrderAggregate.getUserId();
            Long activityId = createPartakeOrderAggregate.getActivityId();
            ActivityAccountEntity activityAccountEntity = createPartakeOrderAggregate.getActivityAccountEntity();
            ActivityAccountMonthEntity activityAccountMonthEntity = createPartakeOrderAggregate.getActivityAccountMonthEntity();
            ActivityAccountDayEntity activityAccountDayEntity = createPartakeOrderAggregate.getActivityAccountDayEntity();
            UserRaffleOrderEntity userRaffleOrderEntity = createPartakeOrderAggregate.getUserRaffleOrderEntity();

            idbRouterStrategy.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    /** first manipulate activity account */
                    int updateTotalRemain = iRaffleActivityAccountDao.updateActivityAccountRemain(
                            RaffleActivityAccount.builder()
                                    .userId(userId)
                                    .activityId(activityId)
                                    .build());
                    if (updateTotalRemain != 1) {
                        status.setRollbackOnly();
                        log.error("save raffle order - totalRemain insufficient userId: {} activityId: {}", userId, activityId);
                        throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
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
                    /** finally insert the userRaffleOrder into database */
                    iUserRaffleOrderDao.insertUserRaffleOrder(UserRaffleOrder.builder()
                            .userId(userRaffleOrderEntity.getUserId())
                            .activityId(userRaffleOrderEntity.getActivityId())
                            .activityName(userRaffleOrderEntity.getActivityName())
                            .strategyId(userRaffleOrderEntity.getStrategyId())
                            .orderId(userRaffleOrderEntity.getOrderId())
                            .orderTime(userRaffleOrderEntity.getOrderTime())
                            .orderStatus(userRaffleOrderEntity.getOrderStatus().getCode())
                            .build());

                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("save raffle order - unique key conflict userId: {} activityId: {}", userId, activityId, e);
                    throw new AppException(ResponseCode.INDEX_DUPLICATE.getCode(), e);
                }
            });
        } finally {
            idbRouterStrategy.clear();
        }
    }

    @Override
    public ActivityAccountMonthEntity queryActivityAccountMonthByUserId(String userId, Long activityId, String month) {
        /** check activity account month exist */
        RaffleActivityAccountMonth raffleActivityAccountMonth = RaffleActivityAccountMonth.builder()
                .userId(userId)
                .activityId(activityId)
                .month(month)
                .build();
        raffleActivityAccountMonth = iRaffleActivityAccountMonthDao.queryActivityAccountMonth(raffleActivityAccountMonth);
        if (raffleActivityAccountMonth == null) return null;

        return ActivityAccountMonthEntity.builder()
                .userId(raffleActivityAccountMonth.getUserId())
                .activityId(raffleActivityAccountMonth.getActivityId())
                .month(raffleActivityAccountMonth.getMonth())
                .monthAmount(raffleActivityAccountMonth.getMonthAmount())
                .monthRemain(raffleActivityAccountMonth.getMonthRemain())
                .build();
    }

    @Override
    public ActivityAccountDayEntity queryActivityAccountDayByUserId(String userId, Long activityId, String day) {
        /** check activity account day exist */
        RaffleActivityAccountDay raffleActivityAccountDay = RaffleActivityAccountDay.builder()
                .userId(userId)
                .activityId(activityId)
                .day(day)
                .build();
        raffleActivityAccountDay = iRaffleActivityAccountDayDao.queryActivityAccountDay(raffleActivityAccountDay);
        if (raffleActivityAccountDay == null) return null;

        return ActivityAccountDayEntity.builder()
                .userId(raffleActivityAccountDay.getUserId())
                .activityId(raffleActivityAccountDay.getActivityId())
                .day(raffleActivityAccountDay.getDay())
                .dayAmount(raffleActivityAccountDay.getDayAmount())
                .dayRemain(raffleActivityAccountDay.getDayRemain())
                .build();
    }

    @Override
    public UserRaffleOrderEntity queryNoUsedRaffleOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity) {
        /** check if unused raffle order exist */
        UserRaffleOrder userRaffleOrder = UserRaffleOrder.builder()
                .userId(partakeRaffleActivityEntity.getUserId())
                .activityId(partakeRaffleActivityEntity.getActivityId())
                .build();

        userRaffleOrder = iUserRaffleOrderDao.queryNoUsedRaffleOrder(userRaffleOrder);
        if (userRaffleOrder == null) return null;

        return UserRaffleOrderEntity.builder()
                .userId(userRaffleOrder.getUserId())
                .activityId(userRaffleOrder.getActivityId())
                .activityName(userRaffleOrder.getActivityName())
                .strategyId(userRaffleOrder.getStrategyId())
                .orderId(userRaffleOrder.getOrderId())
                .orderTime(userRaffleOrder.getOrderTime())
                .orderStatus(UserRaffleOrderStatusVO.valueOf(userRaffleOrder.getOrderStatus()))
                .build();
    }

    @Override
    public List<ActivitySkuEntity> queryActivitySkuByActivityId(Long activityId) {
        List<RaffleActivitySku> raffleActivitySkuList = iRaffleActivitySkuDao.queryRaffleActivitySkuByActivityId(activityId);
        List<ActivitySkuEntity> activitySkuEntityList = new ArrayList<>();
        for (RaffleActivitySku raffleActivitySku : raffleActivitySkuList) {
            ActivitySkuEntity activitySkuEntity = ActivitySkuEntity.builder()
                    .sku(raffleActivitySku.getSku())
                    .activityId(raffleActivitySku.getActivityId())
                    .activityAmountId(raffleActivitySku.getActivityAmountId())
                    .stockAmount(raffleActivitySku.getStockAmount())
                    .stockRemain(raffleActivitySku.getStockRemain())
                    .build();
            activitySkuEntityList.add(activitySkuEntity);
        }
        return activitySkuEntityList;
    }

    @Override
    public Integer queryRaffleActivityAccountDayPartakeAmount(String userId, Long activityId) {
        RaffleActivityAccountDay raffleActivityAccountDay = new RaffleActivityAccountDay();
        raffleActivityAccountDay.setUserId(userId);
        raffleActivityAccountDay.setActivityId(activityId);
        raffleActivityAccountDay.setDay(raffleActivityAccountDay.currentDay());
        raffleActivityAccountDay = iRaffleActivityAccountDayDao.queryActivityAccountDay(raffleActivityAccountDay);
        if (raffleActivityAccountDay == null) return 0;
        return raffleActivityAccountDay.getDayAmount() - raffleActivityAccountDay.getDayRemain();


    }

    @Override
    public ActivityAccountEntity queryActivityAccountEntity(String userId, Long activityId) {
        /** get activity account first */
        RaffleActivityAccount raffleActivityAccount = iRaffleActivityAccountDao.queryActivityAccountByUserId(RaffleActivityAccount.builder()
                .activityId(activityId)
                .userId(userId)
                .build());

        if (null == raffleActivityAccount) {
            /** means do not have account, should get total amount first */
            return ActivityAccountEntity.builder()
                    .activityId(activityId)
                    .userId(userId)
                    .totalAmount(0)
                    .totalRemain(0)
                    .monthAmount(0)
                    .monthRemain(0)
                    .dayAmount(0)
                    .dayRemain(0)
                    .build();
        }

        SimpleDateFormat simpleDateFormatMonth = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
        /** get activity account month */
        RaffleActivityAccountMonth raffleActivityAccountMonth = iRaffleActivityAccountMonthDao.queryActivityAccountMonth(RaffleActivityAccountMonth.builder()
                .activityId(activityId)
                .userId(userId)
                .month(simpleDateFormatMonth.format(new Date()))
                .build());

        /** get activity account day */
        RaffleActivityAccountDay raffleActivityAccountDay = iRaffleActivityAccountDayDao.queryActivityAccountDay(RaffleActivityAccountDay.builder()
                .activityId(activityId)
                .userId(userId)
                .day(simpleDateFormatDay.format(new Date()))
                .build());

        ActivityAccountEntity activityAccountEntity = new ActivityAccountEntity();
        activityAccountEntity.setUserId(userId);
        activityAccountEntity.setActivityId(activityId);
        activityAccountEntity.setTotalAmount(raffleActivityAccount.getTotalAmount());
        activityAccountEntity.setTotalRemain(raffleActivityAccount.getTotalRemain());

        if (raffleActivityAccountDay == null) {
            /** means do not have day account, just use total amount, day amount can pass to next day */
            activityAccountEntity.setDayAmount(raffleActivityAccount.getDayAmount());
            activityAccountEntity.setDayRemain(raffleActivityAccount.getDayRemain()); // todo need to check if should be getDayAmount()
        } else {
            activityAccountEntity.setDayAmount(raffleActivityAccountDay.getDayAmount());
            activityAccountEntity.setDayRemain(raffleActivityAccountDay.getDayRemain());
        }

        if (null == raffleActivityAccountMonth) {
            /** means do not have month account, just use total amount, month amount can pass to next month */
            activityAccountEntity.setMonthAmount(raffleActivityAccount.getMonthAmount());
            activityAccountEntity.setMonthRemain(raffleActivityAccount.getMonthRemain()); // todo need to check if should be getMonthAmount()
        } else {
            activityAccountEntity.setMonthAmount(raffleActivityAccountMonth.getMonthAmount());
            activityAccountEntity.setMonthRemain(raffleActivityAccountMonth.getMonthRemain());
        }

        return activityAccountEntity;
    }

    @Override
    public Integer queryRaffleActivityAccountPartakeAmount(String userId, Long activityId) {
        ActivityAccountEntity activityAccountEntity = queryActivityAccountByUserId(userId, activityId);
        if (activityAccountEntity == null) return 0;
        return activityAccountEntity.getTotalAmount() - activityAccountEntity.getTotalRemain();
    }

    @Override
    public void doSavePayTypeQuotaOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
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
        raffleActivityOrder.setStatus(activityOrderEntity.getStatus().getCode());
        raffleActivityOrder.setOutBusinessNo(activityOrderEntity.getOutBusinessNo());
        raffleActivityOrder.setPointAmount(activityOrderEntity.getPointAmount());

        try {
            /** take userId as key to set router */
            idbRouterStrategy.doRouter(createQuotaOrderAggregate.getUserId());
            transactionTemplate.execute(status -> {
                try {
                    /** insert raffleActivityOrder */
                    iRaffleActivityOrderDao.insert(raffleActivityOrder);
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("save quota order - unique key conflict userId: {} activityId: {} sku: {}", activityOrderEntity.getUserId(), activityOrderEntity.getActivityId(), activityOrderEntity.getSku(), e);
                    throw new AppException(ResponseCode.INDEX_DUPLICATE.getCode(), e);

                }
            });
        } finally {
            idbRouterStrategy.clear();
        }
    }

    @Override
    public void updateActivityOrder(DeliveryOrderEntity deliveryOrderEntity) {

        RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
        raffleActivityOrder.setUserId(deliveryOrderEntity.getUserId());
        raffleActivityOrder.setOutBusinessNo(deliveryOrderEntity.getOutBusinessNo());
        raffleActivityOrder = iRaffleActivityOrderDao.queryRaffleActivityOrder(raffleActivityOrder);

        if (raffleActivityOrder == null) return;

        /** create RaffleActivityAccount object to store */
        RaffleActivityAccount raffleActivityAccount = new RaffleActivityAccount();
        raffleActivityAccount.setUserId(raffleActivityOrder.getUserId());
        raffleActivityAccount.setActivityId(raffleActivityOrder.getActivityId());
        raffleActivityAccount.setTotalAmount(raffleActivityOrder.getTotalAmount());
        raffleActivityAccount.setTotalRemain(raffleActivityOrder.getTotalAmount());
        raffleActivityAccount.setDayAmount(raffleActivityOrder.getDayAmount());
        raffleActivityAccount.setDayRemain(raffleActivityOrder.getDayAmount());
        raffleActivityAccount.setMonthAmount(raffleActivityOrder.getMonthAmount());
        raffleActivityAccount.setMonthRemain(raffleActivityOrder.getMonthAmount());

        /** create RaffleActivityAccountMonth object to store */
        RaffleActivityAccountMonth raffleActivityAccountMonth = new RaffleActivityAccountMonth();
        raffleActivityAccountMonth.setUserId(raffleActivityOrder.getUserId());
        raffleActivityAccountMonth.setActivityId(raffleActivityOrder.getActivityId());
        raffleActivityAccountMonth.setMonth(raffleActivityAccountMonth.currentMonth());
        raffleActivityAccountMonth.setMonthAmount(raffleActivityOrder.getMonthAmount());
        raffleActivityAccountMonth.setMonthRemain(raffleActivityOrder.getMonthAmount());

        /** create RaffleActivityAccountDay object to store */
        RaffleActivityAccountDay raffleActivityAccountDay = new RaffleActivityAccountDay();
        raffleActivityAccountDay.setUserId(raffleActivityOrder.getUserId());
        raffleActivityAccountDay.setActivityId(raffleActivityOrder.getActivityId());
        raffleActivityAccountDay.setDay(raffleActivityAccountDay.currentDay());
        raffleActivityAccountDay.setDayAmount(raffleActivityOrder.getDayAmount());
        raffleActivityAccountDay.setDayRemain(raffleActivityOrder.getDayAmount());

        try {
            /** take userId as key to set router */
            idbRouterStrategy.doRouter(raffleActivityOrder.getUserId());
            RaffleActivityOrder finalRaffleActivityOrder = raffleActivityOrder;
            transactionTemplate.execute(status -> {
                try {
                    /** update raffleActivityOrder */
                    int update = iRaffleActivityOrderDao.updateActivityOrderCompleted(finalRaffleActivityOrder);
                    if (update != 1) {
                        status.setRollbackOnly();
                        return 1;
                    }

                    /** update account */
                    int amount = iRaffleActivityAccountDao.updateAccountQuota(raffleActivityAccount);
                    /** amount == 0, means account does not exist, create new account */
                    if (amount == 0) iRaffleActivityAccountDao.insertActivityAccount(raffleActivityAccount);

                    /** update day account */
                    int dayAmount = iRaffleActivityAccountDayDao.updateAccountDayQuota(raffleActivityAccountDay);
                    /** dayAmount == 0, means day account does not exist, create new account */
                    if (dayAmount == 0) iRaffleActivityAccountDayDao.insertActivityAccountDay(raffleActivityAccountDay);

                    /** update month account */
                    int monthAmount = iRaffleActivityAccountMonthDao.updateAccountMonthQuota(raffleActivityAccountMonth);
                    /** monthAmount == 0, means month account does not exist, create new account */
                    if (monthAmount == 0) iRaffleActivityAccountMonthDao.insertActivityAccountMonth(raffleActivityAccountMonth);

                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("save quota order - unique key conflict userId: {} activityId: {} sku: {}", finalRaffleActivityOrder.getUserId(), finalRaffleActivityOrder.getActivityId(), finalRaffleActivityOrder.getSku(), e);
                    throw new AppException(ResponseCode.INDEX_DUPLICATE.getCode(), e);

                }
            });
        } finally {
            idbRouterStrategy.clear();
        }
    }

    @Override
    public PendingActivityOrderEntity queryPendingActivityOrder(ActivitySkuChargeEntity activitySkuChargeEntity) {
        RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
        raffleActivityOrder.setUserId(activitySkuChargeEntity.getUserId());
        raffleActivityOrder.setSku(activitySkuChargeEntity.getSku());
        raffleActivityOrder = iRaffleActivityOrderDao.queryPendingActivityOrder(raffleActivityOrder);

        if(raffleActivityOrder == null) return null;
        return PendingActivityOrderEntity.builder()
                .userId(raffleActivityOrder.getUserId())
                .orderId(raffleActivityOrder.getOrderId())
                .outBusinessNo(raffleActivityOrder.getOutBusinessNo())
                .pointAmount(raffleActivityOrder.getPointAmount())
                .build();
    }

    @Override
    public List<SkuProductEntity> querySkuProductEntityListByActivityId(Long activityId) {
        List<RaffleActivitySku> raffleActivitySkuList = iRaffleActivitySkuDao.queryRaffleActivitySkuByActivityId(activityId);
        List<SkuProductEntity> skuProductEntityList = new ArrayList<>(raffleActivitySkuList.size());
        for (RaffleActivitySku raffleActivitySku : raffleActivitySkuList) {
            RaffleActivityAmount raffleActivityAmount = iRaffleActivityAmountDao.queryRaffleActivityAmountByActivityAmountId(raffleActivitySku.getActivityAmountId());
            SkuProductEntity.ActivityAmount activityAmount = new SkuProductEntity.ActivityAmount();
            activityAmount.setTotalAmount(raffleActivityAmount.getTotalAmount());
            activityAmount.setMonthAmount(raffleActivityAmount.getMonthAmount());
            activityAmount.setDayAmount(raffleActivityAmount.getDayAmount());

            skuProductEntityList.add(SkuProductEntity.builder()
                    .sku(raffleActivitySku.getSku())
                    .activityId(raffleActivitySku.getActivityId())
                    .activityAmountId(raffleActivitySku.getActivityAmountId())
                    .stockAmount(raffleActivitySku.getStockAmount())
                    .stockRemain(raffleActivitySku.getStockRemain())
                    .pointAmount(raffleActivitySku.getPointAmount())
                    .activityAmount(activityAmount)
                    .build());
        }
        return skuProductEntityList;
    }
}
