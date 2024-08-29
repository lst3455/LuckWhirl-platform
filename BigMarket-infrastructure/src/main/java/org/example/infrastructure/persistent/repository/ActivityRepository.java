package org.example.infrastructure.persistent.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.entity.ActivityAmountEntity;
import org.example.domain.activity.model.entity.ActivityEntity;
import org.example.domain.activity.model.entity.ActivitySkuEntity;
import org.example.domain.activity.model.vo.ActivityStatusVO;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.infrastructure.persistent.dao.IRaffleActivityAmountDao;
import org.example.infrastructure.persistent.dao.IRaffleActivityDao;
import org.example.infrastructure.persistent.dao.IRaffleActivitySkuDao;
import org.example.infrastructure.persistent.po.RaffleActivity;
import org.example.infrastructure.persistent.po.RaffleActivityAmount;
import org.example.infrastructure.persistent.po.RaffleActivitySku;
import org.example.infrastructure.persistent.redis.IRedisService;
import org.example.types.common.Constants;
import org.springframework.stereotype.Repository;

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

}
