package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class Award {
    /** auto increasing id */
    private Long id;
    /** award id */
    private Long awardId;
    /** award key */
    private String awardKey;
    /** award config */
    private String awardConfig;
    /** award describe */
    private String awardDesc;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

}
