package org.example.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySkuEntity {

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
}