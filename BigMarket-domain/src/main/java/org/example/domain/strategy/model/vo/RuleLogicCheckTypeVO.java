package org.example.domain.strategy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RuleLogicCheckTypeVO {

    ALLOW("0000", "pass: go through next process, avoid rule engine"),
    TAKE_OVER("0001","catch；next process will run under rule engine"),
    ;

    private final String code;
    private final String info;

}
