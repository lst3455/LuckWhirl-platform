package org.example.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS("0000", "成功"),
    UN_ERROR("0001", "未知失败"),
    ILLEGAL_PARAMETER("0002", "非法参数"),
    STRATEGY_RULE_WEIGHT_IS_NULL("ERR_BIZ_001","business abnormal, strategy rule exist but doesn't complete configuration"),
    UN_ASSEMBLED_STRATEGY_ARMORY("ERR_BIZ_002", "raffle strategy armory has not done yet, please use IStrategyArmory to armory");

    private String code;
    private String info;

}
