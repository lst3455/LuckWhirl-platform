package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class RuleTreeNode {
    /** auto increase id */
    private Long id;
    /** rule tree id */
    private String treeId;
    /** rule key */
    private String ruleKey;
    /** rule describe */
    private String ruleDesc;
    /** rule value */
    private String ruleValue;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

}
