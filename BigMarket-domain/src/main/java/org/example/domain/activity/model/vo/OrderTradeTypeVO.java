package org.example.domain.activity.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderTradeTypeVO {
    pay_trade("pay_trade","pay point to redeem"),
    non_pay_trade("non_pay_trade","no need pay point to redeem");

    private final String code;
    private final String info;
}
