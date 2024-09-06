package org.example.domain.activity.repository;

import org.example.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import org.example.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import org.example.domain.activity.model.entity.*;
import org.example.domain.activity.model.vo.ActivitySkuStockKeyVO;

import java.util.Date;

public interface IActivityRepository {

    ActivitySkuEntity queryActivitySkuBySku(Long sku);

    ActivityEntity queryActivityByActivityId(Long activityId);

    ActivityAmountEntity queryActivityAmountByActivityAmountId(Long activityAmountId);

    void doSaveQuotaOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    void storeActivitySkuStockAmount(Long sku, Integer stockAmount);

    boolean subtractActivitySkuStock(Long sku, Date endDateTime);

    void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO build);

    ActivitySkuStockKeyVO takeQueueValue();

    void clearQueueValue();

    void updateActivitySkuStock(Long sku);

    void clearActivitySkuStock(Long sku);

    ActivityAccountEntity queryActivityAccountByUserId(String userId, Long activityId);

    void doSaveRaffleOrder(CreatePartakeOrderAggregate createPartakeOrderAggregate);

    ActivityAccountMonthEntity queryActivityAccountMonthByUserId(String userId, Long activityId, String month);

    ActivityAccountDayEntity queryActivityAccountDayByUserId(String userId, Long activityId, String day);
}
