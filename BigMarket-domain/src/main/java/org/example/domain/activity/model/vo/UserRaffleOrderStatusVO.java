package org.example.domain.activity.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRaffleOrderStatusVO {

    create("create", "create"),
    used("used", "used"),
    cancel("cancel", "cancel"),
    ;

    private final String code;
    private final String desc;

}
