package org.example.domain.rebate.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorRebateOrderEntity {
    /** user id */
    private String userId;
    /** order id */
    private String orderId;
    /** behavior type（sign, or others） */
    private String behaviorType;
    /** describe */
    private String rebateDesc;
    /** rebate type（sku, integral） */
    private String rebateType;
    /** configuration */
    private String rebateConfig;
    /** business id */
    private String bizId;
}
