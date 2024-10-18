package org.example.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDrawResponseDTO implements Serializable {
    /** awardId */
    private Long awardId;
    /** awardTitle*/
    private String awardTitle;
    /** awardIndex */
    private Integer awardIndex;

}
