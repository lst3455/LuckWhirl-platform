package org.example.domain.rebate.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BehaviorTypeVO {

    SIGN("sign", "daily sign in"),
    CHATBOT("chatbot", "complete rebate task"),
    ;

    private final String code;
    private final String info;

}
