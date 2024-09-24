package org.example.domain.award.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountStatusVO {
    open("open","open"),
    block("block","block"),
    ;

    private final String code;
    private final String desc;

}
