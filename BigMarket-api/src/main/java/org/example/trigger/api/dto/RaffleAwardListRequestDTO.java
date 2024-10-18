package org.example.trigger.api.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RaffleAwardListRequestDTO implements Serializable {

    @Deprecated
    private Long strategyId;

    private Long activityId;

    private String userId;
}
