package org.example.domain.activity.service;

import org.example.domain.activity.model.entity.*;

public interface IRaffleActivityAccountQuotaService {

    ActivityOrderEntity createActivityOrder(ActivityShopCartEntity activityShopCartEntity);

    PendingActivityOrderEntity createSkuChargeOrder(ActivitySkuChargeEntity activitySkuChargeEntity);

    void updateActivityOrder(DeliveryOrderEntity deliveryOrderEntity);

    Integer queryRaffleActivityAccountDayPartakeAmount(String userId, Long activityId);

    ActivityAccountEntity queryActivityAccountEntity(String userId, Long activityId);

    Integer queryRaffleActivityAccountPartakeAmount(String userId, Long activityId);
}
