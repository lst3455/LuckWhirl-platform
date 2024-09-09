package org.example.domain.award.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.checkerframework.checker.units.qual.A;

@Getter
@AllArgsConstructor
public enum AwardStatusVO {
    create("create","create"),
    complete("complete","complete"),
    fail("fail","fail"),
    ;

    private final String code;
    private final String desc;
}
