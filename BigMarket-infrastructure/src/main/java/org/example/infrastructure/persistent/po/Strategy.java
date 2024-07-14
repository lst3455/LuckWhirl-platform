package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class Strategy {
    /** raffle strategy */
    private Long strategyId;
    /** auto increase id */
    private Long id;
    /** describe of raffle strategy */
    private String strategyDesc;
    /** describe of raffle strategy */
    private String ruleModel;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

}
