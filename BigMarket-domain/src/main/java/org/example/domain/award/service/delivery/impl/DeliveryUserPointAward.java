package org.example.domain.award.service.delivery.impl;

import org.apache.commons.lang3.StringUtils;
import org.example.domain.award.model.aggregate.DeliveryAwardAggregate;
import org.example.domain.award.model.entity.DeliveryAwardEntity;
import org.example.domain.award.model.entity.UserAwardRecordEntity;
import org.example.domain.award.model.entity.UserPointAwardEntity;
import org.example.domain.award.model.vo.AwardStatusVO;
import org.example.domain.award.adapter.repository.IAwardRepository;
import org.example.domain.award.service.delivery.IDeliveryAward;
import org.example.types.common.Constants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;

@Component("user_point_random")
public class DeliveryUserPointAward implements IDeliveryAward {

    @Resource
    private IAwardRepository iAwardRepository;

    @Override
    public void deliveryAward(DeliveryAwardEntity deliveryAwardEntity) {
        /** get award id */
        Long awardId = deliveryAwardEntity.getAwardId();
        /** get range */
        String awardConfig = deliveryAwardEntity.getAwardConfig();
        if (StringUtils.isBlank(awardConfig)) {
            awardConfig = iAwardRepository.queryAwardConfigByAwardId(awardId);
        }

        String[] creditRange = awardConfig.split(Constants.SPLIT_COMMA);
        if (creditRange.length != 2) {
            throw new RuntimeException("award config:" + awardConfig + "is not a valid range, example:1,100");
        }

        /** create random point base on the range */
        BigDecimal pointAmount = generateRandom(new BigDecimal(creditRange[0]), new BigDecimal(creditRange[1]));

        UserAwardRecordEntity userAwardRecordEntity = DeliveryAwardAggregate.buildDeliveryUserAwardRecordEntity(
                deliveryAwardEntity.getUserId(),
                deliveryAwardEntity.getOrderId(),
                deliveryAwardEntity.getAwardId(),
                AwardStatusVO.complete
        );

        UserPointAwardEntity userPointAwardEntity = DeliveryAwardAggregate.buildUserPointAwardEntity(deliveryAwardEntity.getUserId(), pointAmount);

        DeliveryAwardAggregate deliveryAwardAggregate = new DeliveryAwardAggregate();
        deliveryAwardAggregate.setUserId(deliveryAwardEntity.getUserId());
        deliveryAwardAggregate.setUserAwardRecordEntity(userAwardRecordEntity);
        deliveryAwardAggregate.setUserPointAwardEntity(userPointAwardEntity);

        /** save deliveryAwardAggregate */
        doSaveDeliveryAwardAggregate(deliveryAwardAggregate);
    }

    private void doSaveDeliveryAwardAggregate(DeliveryAwardAggregate deliveryAwardAggregate) {
        iAwardRepository.doSaveDeliveryAwardAggregate(deliveryAwardAggregate);
    }

    private BigDecimal generateRandom(BigDecimal min, BigDecimal max) {
        if (min.equals(max)) return min;
        BigDecimal randomBigDecimal = min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));
        return randomBigDecimal.round(new MathContext(3));
    }


}
