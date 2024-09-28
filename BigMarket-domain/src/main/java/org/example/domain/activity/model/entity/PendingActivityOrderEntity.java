package org.example.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingActivityOrderEntity {
    private String userId;
    private String orderId;
    private String outBusinessNo;
    private BigDecimal pointAmount;
}
