package org.example.domain.activity.service.partake;

import org.apache.commons.lang3.RandomStringUtils;
import org.example.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import org.example.domain.activity.model.entity.*;
import org.example.domain.activity.model.vo.UserRaffleOrderStatusVO;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Service
public class RaffleActivityPartakeService extends AbstractRaffleActivityPartake{

    private final SimpleDateFormat dateFormatMonth = new SimpleDateFormat("yyyy-MM");
    private final SimpleDateFormat dateFormatDay = new SimpleDateFormat("yyyy-MM-dd");

    protected RaffleActivityPartakeService(IActivityRepository iActivityRepository) {
        super(iActivityRepository);
    }

    @Override
    protected void doSaveRaffleOrder(CreatePartakeOrderAggregate createPartakeOrderAggregate) {
        iActivityRepository.doSaveRaffleOrder(createPartakeOrderAggregate);
    }

    @Override
    protected UserRaffleOrderEntity buildUserRaffleOrder(String userId, Long activityId, Date currentDate) {
        ActivityEntity activityEntity = iActivityRepository.queryActivityByActivityId(activityId);
        return UserRaffleOrderEntity.builder()
                .userId(userId)
                .activityId(activityId)
                .activityName(activityEntity.getActivityName())
                .strategyId(activityEntity.getStrategyId())
                .orderId(RandomStringUtils.randomNumeric(12))
                .orderTime(currentDate)
                .orderStatus(UserRaffleOrderStatusVO.create)
                .build();
    }

    @Override
    protected CreatePartakeOrderAggregate doFilterAccount(String userId, Long activityId, Date currentDate) {

        ActivityAccountEntity activityAccountEntity = iActivityRepository.queryActivityAccountByUserId(userId,activityId);
        /** check total amount */
        if (activityAccountEntity == null || activityAccountEntity.getTotalAmount() <= 0){
            throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(),ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
        }

        String month = dateFormatMonth.format(currentDate);
        String day = dateFormatDay.format(currentDate);

        /** check month amount */
        ActivityAccountMonthEntity activityAccountMonthEntity = iActivityRepository.queryActivityAccountMonthByUserId(userId, activityId, month);
        if (activityAccountMonthEntity != null && activityAccountMonthEntity.getMonthRemain() <= 0) {
            throw new AppException(ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getInfo());
        }

        /** create ActivityAccountMonthEntity if database doesn't have */
        boolean isExistActivityAccountMonth = activityAccountMonthEntity != null;
        if (activityAccountMonthEntity == null) {
            activityAccountMonthEntity = new ActivityAccountMonthEntity();
            activityAccountMonthEntity.setUserId(userId);
            activityAccountMonthEntity.setActivityId(activityId);
            activityAccountMonthEntity.setMonth(month);
            activityAccountMonthEntity.setMonthAmount(activityAccountEntity.getMonthAmount());
            activityAccountMonthEntity.setMonthRemain(activityAccountEntity.getMonthRemain());
        }

        /** check day amount */
        ActivityAccountDayEntity activityAccountDayEntity = iActivityRepository.queryActivityAccountDayByUserId(userId, activityId, day);
        if (activityAccountDayEntity != null && activityAccountDayEntity.getDayRemain() <= 0) {
            throw new AppException(ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getInfo());
        }

        /** create ActivityAccountDayEntity if database doesn't have */
        boolean isExistActivityAccountDay = activityAccountDayEntity != null;
        if (activityAccountDayEntity == null) {
            activityAccountDayEntity = new ActivityAccountDayEntity();
            activityAccountDayEntity.setUserId(userId);
            activityAccountDayEntity.setActivityId(activityId);
            activityAccountDayEntity.setDay(day);
            activityAccountDayEntity.setDayAmount(activityAccountEntity.getDayAmount());
            activityAccountDayEntity.setDayRemain(activityAccountEntity.getDayRemain());
        }

        /** create PartakeOrderAggregate */
        CreatePartakeOrderAggregate createPartakeOrderAggregate = new CreatePartakeOrderAggregate();
        createPartakeOrderAggregate.setUserId(userId);
        createPartakeOrderAggregate.setActivityId(activityId);
        createPartakeOrderAggregate.setActivityAccountEntity(activityAccountEntity);
        createPartakeOrderAggregate.setExistActivityAccountMonth(isExistActivityAccountMonth);
        createPartakeOrderAggregate.setActivityAccountMonthEntity(activityAccountMonthEntity);
        createPartakeOrderAggregate.setExistActivityAccountDay(isExistActivityAccountDay);
        createPartakeOrderAggregate.setActivityAccountDayEntity(activityAccountDayEntity);

        return createPartakeOrderAggregate;

    }
}
