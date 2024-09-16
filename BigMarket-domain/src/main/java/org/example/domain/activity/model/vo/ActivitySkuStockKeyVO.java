package org.example.domain.activity.model.vo;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivitySkuStockKeyVO {
    /** use to store all sku send to queue */
    @Getter
    public static Set<Long> blockingQueueSkuSet = new HashSet<>();

    private Long sku;

    private Long activityId;

}
