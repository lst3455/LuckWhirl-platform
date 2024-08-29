package org.example.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityAmountEntity {

    /** times of attending activity */
    private Long activityAmountId;
    /** total amount */
    private Integer totalAmount;
    /** day amount */
    private Integer dayAmount;
    /** month amount */
    private Integer monthAmount;

}


