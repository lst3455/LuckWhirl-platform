package org.example.domain.activity.service.partake;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import org.example.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import org.example.domain.activity.model.entity.ActivityEntity;
import org.example.domain.activity.model.entity.PartakeRaffleActivityEntity;
import org.example.domain.activity.model.entity.UserRaffleOrderEntity;
import org.example.domain.activity.model.vo.ActivityStatusVO;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.domain.activity.service.IRaffleActivityPartakeService;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
public abstract class AbstractRaffleActivityPartake implements IRaffleActivityPartakeService {

    @Resource
    protected final IActivityRepository iActivityRepository;

    protected AbstractRaffleActivityPartake(IActivityRepository iActivityRepository) {
        this.iActivityRepository = iActivityRepository;
    }

    @Override
    public UserRaffleOrderEntity createRaffleOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity) {
        /** basic info */
        String userId = partakeRaffleActivityEntity.getUserId();
        Long activityId = partakeRaffleActivityEntity.getActivityId();
        Date currentDate = new Date();

        ActivityEntity activityEntity = iActivityRepository.queryActivityByActivityId(activityId);
        /** check activity status */
        if (!ActivityStatusVO.open.equals(activityEntity.getStatus())) {
            throw new AppException(ResponseCode.ACTIVITY_STATE_ERROR.getCode(), ResponseCode.ACTIVITY_STATE_ERROR.getInfo());
        }
        /** check activity valid date */
        if (activityEntity.getBeginDateTime().after(currentDate) || activityEntity.getEndDateTime().before(currentDate)) {
            throw new AppException(ResponseCode.ACTIVITY_DATE_ERROR.getCode(), ResponseCode.ACTIVITY_DATE_ERROR.getInfo());
        }

        /** check if unused RaffleOrder exist */
        UserRaffleOrderEntity userRaffleOrderEntity = iActivityRepository.queryNoUsedRaffleOrder(partakeRaffleActivityEntity);
        if (userRaffleOrderEntity != null) {
            log.info("create raffle order[unused order exist] userId:{} activityId:{} userRaffleOrderEntity:{}", userId, activityId, JSON.toJSONString(userRaffleOrderEntity));
            return userRaffleOrderEntity;
        }

        CreatePartakeOrderAggregate createPartakeOrderAggregate = doFilterAccount(userId, activityId, currentDate);

        userRaffleOrderEntity = buildUserRaffleOrder(userId, activityId, currentDate);
        createPartakeOrderAggregate.setUserRaffleOrderEntity(userRaffleOrderEntity);
        /** save user raffle order in database */
        doSaveRaffleOrder(createPartakeOrderAggregate);

        return userRaffleOrderEntity;
    }

    protected abstract void doSaveRaffleOrder(CreatePartakeOrderAggregate createPartakeOrderAggregate);

    protected abstract UserRaffleOrderEntity buildUserRaffleOrder(String userId, Long activityId, Date currentDate);

    protected abstract CreatePartakeOrderAggregate doFilterAccount(String userId, Long activityId, Date currentDate);
}
