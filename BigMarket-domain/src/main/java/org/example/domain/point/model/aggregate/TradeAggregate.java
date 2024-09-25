package org.example.domain.point.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.domain.point.model.entity.UserPointAccountEntity;
import org.example.domain.point.model.entity.UserPointOrderEntity;
import org.example.domain.point.model.vo.TradeNameVO;
import org.example.domain.point.model.vo.TradeTypeVO;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeAggregate {
    private String userId;
    private UserPointOrderEntity userPointOrderEntity;
    private UserPointAccountEntity userPointAccountEntity;

    public static UserPointAccountEntity createUserPointAccountEntity(String userId, BigDecimal updatedAmount) {
        return UserPointAccountEntity.builder()
                .userId(userId)
                .updatedAmount(updatedAmount)
                .build();
    }

    public static UserPointOrderEntity createUserPointOrderEntity(String userId,
                                                                  TradeNameVO tradeName,
                                                                  TradeTypeVO tradeType,
                                                                  BigDecimal tradeAmount,
                                                                  String outBusinessNo) {
        return UserPointOrderEntity.builder()
                .userId(userId)
                .orderId(RandomStringUtils.randomNumeric(12))
                .tradeName(tradeName)
                .tradeType(tradeType)
                .tradeAmount(tradeAmount)
                .outBusinessNo(outBusinessNo)
                .build();
    }

}
