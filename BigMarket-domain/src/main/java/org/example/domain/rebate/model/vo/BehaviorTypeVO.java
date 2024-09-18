package org.example.domain.rebate.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BehaviorTypeVO {

    SIGN("sign", "daily sign in"),
//    OPENAI_PAY("openai_pay", "openai pay"),
    ;

    private final String code;
    private final String info;

}
