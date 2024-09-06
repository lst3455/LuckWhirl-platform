package org.example.domain.activity.service.quota.rule.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.entity.ActivityAmountEntity;
import org.example.domain.activity.model.entity.ActivityEntity;
import org.example.domain.activity.model.entity.ActivitySkuEntity;
import org.example.domain.activity.model.vo.ActivitySkuStockKeyVO;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.domain.activity.service.armory.IActivityDispatch;
import org.example.domain.activity.service.quota.rule.AbstractActionChain;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component("activity_sku_stock_action")
public class ActivitySkuStockActionChain extends AbstractActionChain {

    @Resource
    private IActivityDispatch iActivityDispatch;
    @Resource
    private IActivityRepository iActivityRepository;

    @Override
    public boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityAmountEntity activityAmountEntity) {
        log.info("activity rule chain start - skuStock sku:{} activityId:{}",activitySkuEntity.getSku(), activityEntity.getActivityId());

        /** subtract the corresponding activity sku stock amount, return true if success */
        boolean status = iActivityRepository.subtractActivitySkuStock(activitySkuEntity.getSku(),activityEntity.getEndDateTime());
        if (status) {
            /** add sku and activityId to delay queue, in order to delayed consume the database stock */
            iActivityRepository.activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO.builder()
                    .sku(activitySkuEntity.getSku())
                    .activityId(activitySkuEntity.getActivityId())
                    .build());
            log.info("activity rule chain pass - skuStock sku:{} activityId:{}",activitySkuEntity.getSku(), activityEntity.getActivityId());
            return true;
        }

        log.info("activity rule chain take over - skuStock sku:{} activityId:{} errorInfo:{}",activitySkuEntity.getSku(), activityEntity.getActivityId(),ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getInfo());
        return false;
    }
}
