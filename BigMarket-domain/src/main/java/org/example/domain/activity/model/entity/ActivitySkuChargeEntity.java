package org.example.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySkuChargeEntity {
    /** user id */
    private String userId;
    /** sku */
    private Long sku;
    /** avoid duplicate */
    private String outBusinessNo;
}
