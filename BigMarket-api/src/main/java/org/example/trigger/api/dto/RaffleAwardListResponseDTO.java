package org.example.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleAwardListResponseDTO {
    /** award id */
    private Long awardId;
    /** award award title */
    private String awardTitle;
    /** award award sub-title */
    private String awardSubtitle;
    /** award sort */
    private Integer sort;
    /** award unlock count */
    private Integer awardUnlockAmount;
    /** if unlock */
    private Boolean isUnlock;
    /** award unlock remain */
    private Integer awardUnlockRemain;
}
