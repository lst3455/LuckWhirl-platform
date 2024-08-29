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
public class ActivityAccountEntity {

    /** user id */
    private String userId;
    /** activity id */
    private Long activityId;
    /** total amount */
    private Integer totalAmount;
    /** total remain */
    private Integer totalRemain;
    /** day amount */
    private Integer dayAmount;
    /** day remain */
    private Integer dayRemain;
    /** month amount */
    private Integer monthAmount;
    /** month remain */
    private Integer monthRemain;

}
