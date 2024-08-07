package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class RuleTree {

    /** auto increase id */
    private Long id;
    /** rule tree ID */
    private String treeId;
    /** rule tree name */
    private String treeName;
    /** rule tree describe */
    private String treeDesc;
    /** rule tree root node */
    private String treeRootNodeKey;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

}

