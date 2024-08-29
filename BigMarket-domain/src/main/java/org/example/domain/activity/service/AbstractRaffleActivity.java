package org.example.domain.activity.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.domain.activity.model.entity.*;
import org.example.domain.activity.repository.IActivityRepository;

@Slf4j
public class AbstractRaffleActivity implements IRaffleOrder{

    protected IActivityRepository iActivityRepository;

    public AbstractRaffleActivity(IActivityRepository iActivityRepository) {
        this.iActivityRepository = iActivityRepository;
    }

    @Override
    public ActivityOrderEntity createActivityOrder(ActivityShopCartEntity activityShopCartEntity) {

        ActivitySkuEntity activitySkuEntity = iActivityRepository.queryActivitySkuEntity(activityShopCartEntity.getSku());
        ActivityEntity activityEntity = iActivityRepository.queryActivityByActivityEntityById(activitySkuEntity.getActivityId());
        ActivityAmountEntity activityAmountEntity = iActivityRepository.queryActivityAmountEntityByActivityAmountId(activitySkuEntity.getActivityAmountId());
        log.info("query result: {} {} {}", JSON.toJSONString(activitySkuEntity),JSON.toJSONString(activityEntity),JSON.toJSONString(activityAmountEntity));

        return ActivityOrderEntity.builder().build();
    }
}
