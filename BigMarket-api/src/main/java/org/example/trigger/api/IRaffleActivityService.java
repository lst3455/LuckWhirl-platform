package org.example.trigger.api;

import org.example.trigger.api.dto.*;
import org.example.types.model.Response;

import java.math.BigDecimal;
import java.util.List;

public interface IRaffleActivityService {

    Response<Boolean> activityArmory(Long activityId);

    Response<ActivityDrawResponseDTO> draw(ActivityDrawRequestDTO activityDrawRequestDTO);

    Response<Boolean> dailySignRebate(String userId);

    Response<Boolean> isDailySignRebateGet(String userId);

    Response<UserActivityAccountResponseDTO> queryRaffleActivityAccount(UserActivityAccountRequestDTO userActivityAccountRequestDTO);

    Response<Boolean> pointRedeemSku(SkuProductRequestDTO skuProductRequestDTO);

    Response<BigDecimal> queryUserPointAccount(String userId);

    Response<List<SkuProductResponseDTO>> querySkuProductListByActivityId(Long activityId);

}
