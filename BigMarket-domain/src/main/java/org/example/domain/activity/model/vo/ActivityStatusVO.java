package org.example.domain.activity.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActivityStatusVO {

    create("create","create"),
    open("open","open"),
    close("close","close"),
    ;
    
    private final String code;
    private final String desc;
}
