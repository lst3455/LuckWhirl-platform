package org.example.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleActivityAccountDay {

    /** auto increasing id */
    private Long id;
    /** user id */
    private String userId;
    /** activity id */
    private Long activityId;
    /** day (yyyy-mm-dd) */
    private String day;
    /** day amount */
    private Integer dayAmount;
    /** day remain */
    private Integer dayRemain;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public String currentDay() {
        return dateFormat.format(new Date());
    }
}
