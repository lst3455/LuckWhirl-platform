package org.example.domain.activity.repository;

import org.example.domain.activity.model.entity.ActivityAmountEntity;
import org.example.domain.activity.model.entity.ActivityEntity;
import org.example.domain.activity.model.entity.ActivitySkuEntity;

public interface IActivityRepository {

    ActivitySkuEntity queryActivitySkuEntity(Long sku);

    ActivityEntity queryActivityByActivityEntityById(Long activityId);

    ActivityAmountEntity queryActivityAmountEntityByActivityAmountId(Long activityAmountId);
}
