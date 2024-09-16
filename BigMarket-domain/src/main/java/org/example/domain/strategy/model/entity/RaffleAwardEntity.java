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
    /** award title */
    private String awardTitle;
    /** award config */
    private String awardConfig;
    /** sort */
    private Integer sort;
    /** award describe */
    private String awardDesc;
}
