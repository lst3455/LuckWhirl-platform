package org.example.domain.activity.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.checkerframework.checker.units.qual.A;

@Getter
@AllArgsConstructor
public enum OrderStatusVO {

    create("completed","completed");

    private final String code;
    private final String desc;

}
