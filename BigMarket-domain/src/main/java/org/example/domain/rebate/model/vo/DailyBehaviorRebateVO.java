package org.example.domain.rebate.model.vo;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyBehaviorRebateVO {

    /** behavior type（sign, or others） */
    private String behaviorType;
    /** describe */
    private String rebateDesc;
    /** rebate type（sku, integral） */
    private String rebateType;
    /** configuration */
    private String rebateConfig;

}
