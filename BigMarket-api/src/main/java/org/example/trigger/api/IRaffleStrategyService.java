package org.example.trigger.api;

import org.example.trigger.api.dto.*;
import org.example.types.model.Response;

import java.util.List;

public interface IRaffleStrategyService {
    /**
     * strategy armory into cache api
     * @param strategyId
     * @return armory result
     */
    Response<Boolean> strategyArmory(Long strategyId);

    /**
     * query award list api
     * @param raffleAwardListRequestDTO
     * @return List<RaffleAwardListResponseDTO>
     */
    Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(RaffleAwardListRequestDTO raffleAwardListRequestDTO);

    Response<List<RaffleStrategyRuleWeightAwardResponseDTO>> queryRaffleStrategyRuleWeightAwardList(RaffleStrategyRuleWeightAwardRequestDTO raffleStrategyRuleWeightAwardRequestDTO);

    /**
     * query random raffle api
     * @param raffleStrategyRequestDTO
     * @return RaffleResponseDTO
     */
    Response<RaffleStrategyResponseDTO> randomRaffle(RaffleStrategyRequestDTO raffleStrategyRequestDTO);


}
