package org.example.domain.activity.service.armory;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.entity.ActivitySkuEntity;
import org.example.domain.activity.repository.IActivityRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

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
        /** cache data to redis when query*/
        iActivityRepository.queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        /** cache data to redis when query*/
        iActivityRepository.queryActivityAmountByActivityAmountId(activitySkuEntity.getActivityAmountId());

        return true;
    }

    @Override
    public boolean assembleActivitySkuByActivityId(Long activityId) {
        List<ActivitySkuEntity> activitySkuEntityList = iActivityRepository.queryActivitySkuByActivityId(activityId);
        for (ActivitySkuEntity activitySkuEntity : activitySkuEntityList){
            iActivityRepository.storeActivitySkuStockAmount(activitySkuEntity.getSku(),activitySkuEntity.getStockRemain());
            /** cache data to redis when query*/
            iActivityRepository.queryActivityAmountByActivityAmountId(activitySkuEntity.getActivityAmountId());
        }
        /** cache data to redis when query*/
        iActivityRepository.queryRaffleActivityByActivityId(activityId);
        return true;
    }

    @Override
    public boolean subtractActivitySkuStock(Long sku, Date endDateTime) {
        return iActivityRepository.subtractActivitySkuStock(sku,endDateTime);
    }
}
