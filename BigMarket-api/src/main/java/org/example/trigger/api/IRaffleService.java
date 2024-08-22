package org.example.trigger.api;

import org.example.trigger.api.dto.RaffleAwardListRequestDTO;
import org.example.trigger.api.dto.RaffleAwardListResponseDTO;
import org.example.trigger.api.dto.RaffleRequestDTO;
import org.example.trigger.api.dto.RaffleResponseDTO;
import org.example.types.model.Response;

import java.util.List;

public interface IRaffleService {
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

    /**
     * query random raffle api
     * @param raffleRequestDTO
     * @return RaffleResponseDTO
     */
    Response<RaffleResponseDTO> randomRaffle(RaffleRequestDTO raffleRequestDTO);
}
