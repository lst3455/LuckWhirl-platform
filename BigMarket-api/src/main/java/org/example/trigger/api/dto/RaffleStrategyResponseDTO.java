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
public class RaffleStrategyResponseDTO implements Serializable {
    /** award id */
    private Long awardId;
    /** award index */
    private Integer awardIndex;
}
