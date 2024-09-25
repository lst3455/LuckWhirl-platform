package org.example.domain.point.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradeTypeVO {
    ADDITION("addition","add point"),
    SUBTRACTION("subtraction","subtract point"),
    ;

    private final String code;
    private final String info;
}
