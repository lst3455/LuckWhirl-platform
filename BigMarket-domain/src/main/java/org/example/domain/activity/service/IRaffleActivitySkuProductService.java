package org.example.domain.activity.service;

import org.example.domain.activity.model.entity.SkuProductEntity;

import java.util.List;

public interface IRaffleActivitySkuProductService {

    List<SkuProductEntity> querySkuProductEntityListByActivityId(Long activityId);
}
