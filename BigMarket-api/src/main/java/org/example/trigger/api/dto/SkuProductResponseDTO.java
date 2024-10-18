package org.example.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuProductResponseDTO implements Serializable {
    /** sku */
    private Long sku;
    /** activity id */
    private Long activityId;
    /** activity amount id */
    private Long activityAmountId;
    /** stock amount */
    private Integer stockAmount;
    /** stock remain */
    private Integer stockRemain;
    /** point needed to redeem this sku */
    private BigDecimal pointAmount;

    private ActivityAmount activityAmount;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ActivityAmount{
        /** total amount */
        private Integer totalAmount;
        /** day amount */
        private Integer dayAmount;
        /** month amount */
        private Integer monthAmount;
    }
}
