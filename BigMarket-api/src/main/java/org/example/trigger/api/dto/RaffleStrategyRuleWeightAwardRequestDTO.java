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
public class RaffleStrategyRuleWeightAwardRequestDTO implements Serializable {

    private Long activityId;

    private String userId;
}
