package org.example.domain.activity.repository;

import org.example.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import org.example.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import org.example.domain.activity.model.entity.*;
import org.example.domain.activity.model.vo.ActivitySkuStockKeyVO;

import java.util.Date;
import java.util.List;

public interface IActivityRepository {

    ActivitySkuEntity queryActivitySkuBySku(Long sku);

    ActivityEntity queryRaffleActivityByActivityId(Long activityId);

    ActivityAmountEntity queryActivityAmountByActivityAmountId(Long activityAmountId);

    void doSaveQuotaOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    void storeActivitySkuStockAmount(Long sku, Integer stockAmount);

    boolean subtractActivitySkuStock(Long sku, Date endDateTime);

    void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO build);

    ActivitySkuStockKeyVO takeQueueValue(Long sku);

    void clearQueueValue(Long sku);

    void updateActivitySkuStock(Long sku);

    void clearActivitySkuStock(Long sku);

    ActivityAccountEntity queryActivityAccountByUserId(String userId, Long activityId);

    void doSaveRaffleOrder(CreatePartakeOrderAggregate createPartakeOrderAggregate);

    ActivityAccountMonthEntity queryActivityAccountMonthByUserId(String userId, Long activityId, String month);

    ActivityAccountDayEntity queryActivityAccountDayByUserId(String userId, Long activityId, String day);

    UserRaffleOrderEntity queryNoUsedRaffleOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity);

    List<ActivitySkuEntity> queryActivitySkuByActivityId(Long activityId);

    Integer queryRaffleActivityAccountDayPartakeAmount(String userId, Long activityId);

    ActivityAccountEntity queryActivityAccountEntity(String userId, Long activityId);
}
