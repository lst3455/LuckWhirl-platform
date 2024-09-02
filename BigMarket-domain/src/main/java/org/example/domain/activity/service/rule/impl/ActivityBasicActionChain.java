package org.example.domain.activity.service.rule.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.entity.ActivityAmountEntity;
import org.example.domain.activity.model.entity.ActivityEntity;
import org.example.domain.activity.model.entity.ActivitySkuEntity;
import org.example.domain.activity.service.rule.AbstractActionChain;
import org.springframework.stereotype.Component;

@Slf4j
@Component("activity_basic_action")
public class ActivityBasicActionChain extends AbstractActionChain {
    @Override
    public boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activity, ActivityAmountEntity activityAmountEntity) {
        log.info("activity rule chain start");
        return next().action(activitySkuEntity, activity, activityAmountEntity);
    }
}
