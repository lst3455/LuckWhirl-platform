package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class Task {
    /** auto increasing id */
    private String id;
    /** message topic */
    private String topic;
    /** message */
    private String message;
    /** task status；create,completed,fail */
    private String status;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

}
