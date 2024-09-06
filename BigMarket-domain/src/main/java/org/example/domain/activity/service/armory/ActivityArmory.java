package org.example.domain.activity.service.armory;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.domain.activity.model.entity.ActivitySkuEntity;
import org.example.domain.activity.repository.IActivityRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
@Slf4j
public class ActivityArmory implements IActivityArmory,IActivityDispatch{

    @Resource
    private IActivityRepository iActivityRepository;

    @Override
    public boolean assembleActivitySku(Long sku) {
        ActivitySkuEntity activitySkuEntity = iActivityRepository.queryActivitySkuBySku(sku);
        /** cache data to redis */
        iActivityRepository.storeActivitySkuStockAmount(sku,activitySkuEntity.getStockAmount());
        iActivityRepository.queryActivityByActivityId(activitySkuEntity.getActivityId());
        iActivityRepository.queryActivityAmountByActivityAmountId(activitySkuEntity.getActivityAmountId());

        return true;
    }

    @Override
    public boolean subtractActivitySkuStock(Long sku, Date endDateTime) {
        return iActivityRepository.subtractActivitySkuStock(sku,endDateTime);
    }
}
