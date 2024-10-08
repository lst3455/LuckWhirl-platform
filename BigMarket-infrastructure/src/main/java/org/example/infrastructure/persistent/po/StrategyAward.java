package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StrategyAward {
    /** auto increase key */
    private Long id;
    /** raffle strategy id */
    private Long strategyId;
    /** award id */
    private Long awardId;
    /** award title */
    private String awardTitle;
    /** award subtitle */
    private String awardSubtitle;
    /** award amount */
    private Long awardAmount;
    /** award remain amount */
    private Long awardRemain;
    /** award rate */
    private BigDecimal awardRate;
    /** sort */
    private Integer sort;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;
    /** rule model */
    private String ruleModel;

}
