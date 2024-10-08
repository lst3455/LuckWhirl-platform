package org.example.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.activity.model.vo.OrderTradeTypeVO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySkuChargeEntity {
    /** user id */
    private String userId;
    /** sku */
    private Long sku;
    /** avoid duplicate */
    private String outBusinessNo;

    private OrderTradeTypeVO orderTradeTypeVO = OrderTradeTypeVO.non_pay_trade;
}
