package org.example.domain.point.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradeNameVO {
    REBATE("rebate"),
    REDEEM_SKU("redeem sku"),
    ;

    private final String name;
}
