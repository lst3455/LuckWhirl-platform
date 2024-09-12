package org.example.domain.activity.service.quota.rule.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.entity.ActivityAmountEntity;
import org.example.domain.activity.model.entity.ActivityEntity;
import org.example.domain.activity.model.entity.ActivitySkuEntity;
import org.example.domain.activity.model.vo.ActivityStatusVO;
import org.example.domain.activity.service.quota.rule.AbstractActionChain;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component("activity_basic_action")
public class ActivityBasicActionChain extends AbstractActionChain {
    @Override
    public boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityAmountEntity activityAmountEntity) {
        log.info("activity rule chain start - basic, sku:{}, activityId:{}",activitySkuEntity.getSku(), activityEntity.getActivityId());
        /** first check the activity status */
        if (!activityEntity.getStatus().equals(ActivityStatusVO.open)) {
            log.info("activity rule chain take over - basic, sku:{}, activityId:{}, errorInfo:{}",activitySkuEntity.getSku(), activityEntity.getActivityId(),ResponseCode.ACTIVITY_STATE_ERROR.getInfo());
            return false;
        }
        /** then check the activity date */
        Date currentDate = new Date();
        if (activityEntity.getBeginDateTime().after(currentDate) || activityEntity.getEndDateTime().before(currentDate)) {
            log.info("activity rule chain take over - basic, sku:{}, activityId:{}, errorInfo:{}",activitySkuEntity.getSku(), activityEntity.getActivityId(),ResponseCode.ACTIVITY_DATE_ERROR.getInfo());
            return false;
        }
        /** then check the activitySku stock */
        if (activitySkuEntity.getStockRemain() <= 0) {
            log.info("activity rule chain take over - basic, sku:{}, activityId:{}, errorInfo:{}",activitySkuEntity.getSku(), activityEntity.getActivityId(),ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getInfo());
            return false;
        }
        log.info("activity rule chain pass - basic, sku:{}, activityId:{}",activitySkuEntity.getSku(), activityEntity.getActivityId());
        return next().action(activitySkuEntity, activityEntity, activityAmountEntity);
    }
}
