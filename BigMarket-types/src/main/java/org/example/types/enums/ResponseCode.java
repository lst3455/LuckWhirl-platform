package org.example.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS("0000", "success"),
    UN_ERROR("0001", "fail"),
    ILLEGAL_PARAMETER("0002", "invalid parameter"),
    INDEX_DUPLICATE("0003","unique key conflict" ),
    CACHEKEY_NOT_EXIST("0004", "cache key not exist"),
    DEGRADE_SWITCH("0005","degrade switch"),
    RATE_LIMIT("0006", "access deny, reach rate limit"),
    HYSTRIX("0007", "connect timeout"),
    GATEWAY_ERROR("0008", "chatbot api call fail"),
    APP_TOKEN_ERROR("0009", "api access fail"),
    STRATEGY_RULE_WEIGHT_IS_NULL("ERR_BIZ_001","business abnormal, strategy rule exist but doesn't complete configuration"),
    UN_ASSEMBLED_STRATEGY_ARMORY("ERR_BIZ_002", "raffle strategy armory has not done yet, please use IStrategyArmory to armory"),
    ACTIVITY_STATE_ERROR("ERR_BIZ_003", "activity dose not open"),
    ACTIVITY_DATE_ERROR("ERR_BIZ_004", "out of activity date"),
    ACTIVITY_SKU_STOCK_ERROR("ERR_BIZ_005", "activity stock is not enough"),
    ACTIVITY_CHAIN_TAKE_OVER("ERR_BIZ_006", "activity chain take over, process stop"),
    ACCOUNT_QUOTA_ERROR("ERR_BIZ_006","account amount insufficient"),
    ACCOUNT_MONTH_QUOTA_ERROR("ERR_BIZ_007","account month amount insufficient"),
    ACCOUNT_DAY_QUOTA_ERROR("ERR_BIZ_008","account day amount insufficient"),
    ACTIVITY_RAFFLE_ORDER_ERROR("ERR_BIZ_009","user raffle order has been used" ),
    USER_POINT_ACCOUNT_NO_ENOUGH("ERR_BIZ_009","user point account not enough" );


    private String code;
    private String info;

}
