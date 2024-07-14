package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class StrategyRule {
    /** auto increase id */
    private Long id;
    /** raffle strategy id */
    private Long strategyId;
    /** award id */
    private Long awardId;
    /** rule type(1. raffle rule 2. award rule) */
    private String ruleType;
    /** rule type (rule_lock) */
    private String ruleModel;
    /** proportion of rule */
    private String ruleValue;
    /** rule describe */
    private String ruleDesc;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

}
