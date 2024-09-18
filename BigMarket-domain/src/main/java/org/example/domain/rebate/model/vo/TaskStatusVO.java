package org.example.domain.rebate.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskStatusVO {
    create("create","create"),
    complete("complete","complete"),
    fail("fail","fail"),
    ;

    private final String code;
    private final String desc;
}
