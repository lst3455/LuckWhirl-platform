package org.example.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityShopCartEntity {

    /** user id */
    private String userId;
    /** sku */
    private Long sku;
}
