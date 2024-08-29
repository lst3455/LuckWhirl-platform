package org.example.domain.activity.service;

import org.example.domain.activity.model.entity.ActivityOrderEntity;
import org.example.domain.activity.model.entity.ActivityShopCartEntity;

public interface IRaffleOrder {

    ActivityOrderEntity createActivityOrder(ActivityShopCartEntity activityShopCartEntity);
}
