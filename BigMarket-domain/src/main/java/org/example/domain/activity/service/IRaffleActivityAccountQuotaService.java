package org.example.domain.activity.service;

import org.example.domain.activity.model.entity.ActivityAccountEntity;
import org.example.domain.activity.model.entity.ActivityOrderEntity;
import org.example.domain.activity.model.entity.ActivityShopCartEntity;
import org.example.domain.activity.model.entity.ActivitySkuChargeEntity;

public interface IRaffleActivityAccountQuotaService {

    ActivityOrderEntity createActivityOrder(ActivityShopCartEntity activityShopCartEntity);

    String createSkuChargeOrder(ActivitySkuChargeEntity activitySkuChargeEntity);

    Integer queryRaffleActivityAccountDayPartakeAmount(String userId, Long activityId);

    ActivityAccountEntity queryActivityAccountEntity(String userId, Long activityId);
}
