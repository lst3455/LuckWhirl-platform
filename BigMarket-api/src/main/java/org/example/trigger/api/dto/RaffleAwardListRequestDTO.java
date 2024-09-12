package org.example.trigger.api.dto;

import lombok.Data;

@Data
public class RaffleAwardListRequestDTO {

    @Deprecated
    private Long strategyId;

    private Long activityId;

    private String userId;
}
