package org.example.domain.strategy.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleAwardEntity {

    /** award id */
    private Long awardId;
    /** raffle strategy id */
    private Long strategyId;
    /** award key */
    private String awardKey;
    /** award config */
    private String awardConfig;
    /** award describe */
    private String awardDesc;
}
