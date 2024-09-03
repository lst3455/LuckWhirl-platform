package org.example.domain.activity.repository;

import org.example.domain.activity.model.aggregate.CreateOrderAggregate;
import org.example.domain.activity.model.entity.ActivityAmountEntity;
import org.example.domain.activity.model.entity.ActivityEntity;
import org.example.domain.activity.model.entity.ActivitySkuEntity;
import org.example.domain.activity.model.vo.ActivitySkuStockKeyVO;

import java.util.Date;

public interface IActivityRepository {

    ActivitySkuEntity queryActivitySkuEntityBySku(Long sku);

    ActivityEntity queryActivityEntityByActivityId(Long activityId);

    ActivityAmountEntity queryActivityAmountEntityByActivityAmountId(Long activityAmountId);

    void doSaveOrder(CreateOrderAggregate createOrderAggregate);

    void storeActivitySkuStockAmount(Long sku, Integer stockAmount);

    boolean subtractActivitySkuStock(Long sku, Date endDateTime);

    void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO build);

    ActivitySkuStockKeyVO takeQueueValue();

    void clearQueueValue();

    void updateActivitySkuStock(Long sku);

    void clearActivitySkuStock(Long sku);
}
