package org.example.domain.award.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.award.model.entity.UserAwardRecordEntity;
import org.example.domain.award.model.entity.UserPointAwardEntity;
import org.example.domain.award.model.vo.AwardStatusVO;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAwardAggregate {


    private String userId;

    private UserAwardRecordEntity userAwardRecordEntity;

    private UserPointAwardEntity userPointAwardEntity;

    public static UserAwardRecordEntity buildDeliveryUserAwardRecordEntity(String userId, String orderId, Long awardId, AwardStatusVO awardStatus) {
        UserAwardRecordEntity userAwardRecord = new UserAwardRecordEntity();
        userAwardRecord.setUserId(userId);
        userAwardRecord.setOrderId(orderId);
        userAwardRecord.setAwardId(awardId);
        userAwardRecord.setAwardStatus(awardStatus);
        return userAwardRecord;
    }

    public static UserPointAwardEntity buildUserPointAwardEntity(String userId, BigDecimal creditAmount) {
        return UserPointAwardEntity.builder().userId(userId).pointAmount(creditAmount).build();
    }


}
