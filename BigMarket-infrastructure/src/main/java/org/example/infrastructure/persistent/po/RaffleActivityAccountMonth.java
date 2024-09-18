package org.example.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleActivityAccountMonth {

    /** auto increasing id */
    private Long id;
    /** user id */
    private String userId;
    /** activity id */
    private Long activityId;
    /** day (yyyy-mm-dd) */
    private String month;
    /** day amount */
    private Integer monthAmount;
    /** day remain */
    private Integer monthRemain;
    /** create time */
    private Date createTime;
    /** update time */
    private Date updateTime;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");

    public String currentMonth(){
        return simpleDateFormat.format(new Date());
    }
}
