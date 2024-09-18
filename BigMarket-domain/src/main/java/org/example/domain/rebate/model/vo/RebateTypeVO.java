package org.example.domain.rebate.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RebateTypeVO {
    SKU("sku", "sku charge"),
    POINT("point", "point charge"),
    ;

    private final String code;
    private final String info;
}
