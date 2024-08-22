package org.example.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StrategyAwardEntity {
    /** raffle strategy id */
    private Long strategyId;
    /** award id */
    private Long awardId;
    /** award amount */
    private Long awardAmount;
    /** award remain amount */
    private Long awardRemain;
    /** award rate */
    private BigDecimal awardRate;
    /** award title */
    private String awardTitle;
    /** award subtitle */
    private String awardSubtitle;
    /** sort */
    private Integer sort;
}
