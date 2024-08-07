package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class RuleTreeNodeLine {
    /** auto increase id */
    private Long id;
    /** rule tree id */
    private String treeId;
    /** rule key (From) */
    private String ruleNodeFrom;
    /** rule key (To) */
    private String ruleNodeTo;
    /** limit type；1:=;2:>;3:<;4:>=;5<=;6:enum[enumerate range] */
    private String ruleLimitType;
    /** limit value (to next node) */
    private String ruleLimitValue;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

}
