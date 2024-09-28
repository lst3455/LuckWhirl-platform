package org.example.domain.activity.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusVO {

    pending("pending","pending"),
    completed("completed","completed");

    private final String code;
    private final String desc;

}
