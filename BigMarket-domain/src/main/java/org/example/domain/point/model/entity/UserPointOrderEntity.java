package org.example.domain.point.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.point.model.vo.TradeNameVO;
import org.example.domain.point.model.vo.TradeTypeVO;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPointOrderEntity {
    /** user id */
    private String userId;
    /** order id */
    private String orderId;
    /** trade name */
    private TradeNameVO tradeName;
    /** trade type */
    private TradeTypeVO tradeType;
    /** trade amount */
    private BigDecimal tradeAmount;
    /** avoid duplicate */
    private String outBusinessNo;
}
