package org.example.trigger.http;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.RaffleAwardEntity;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;
import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.service.IRaffleAward;
import org.example.domain.strategy.service.IRaffleStrategy;
import org.example.domain.strategy.service.armory.IStrategyArmory;
import org.example.trigger.api.IRaffleService;
import org.example.trigger.api.dto.RaffleAwardListRequestDTO;
import org.example.trigger.api.dto.RaffleAwardListResponseDTO;
import org.example.trigger.api.dto.RaffleRequestDTO;
import org.example.trigger.api.dto.RaffleResponseDTO;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.example.types.model.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * raffle service
 */

@Slf4j
@RestController()
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/")
public class IRaffleController implements IRaffleService {

    @Resource
    private IStrategyArmory iStrategyArmory;

    @Resource
    private IRaffleAward iRaffleAward;

    @Resource
    private IRaffleStrategy iRaffleStrategy;

    /**
     * armory the raffle table into cache
     * <a href="http://localhost:8091/api/v1/raffle/strategy_armory">/api/v1/raffle/strategy_armory</a>
     * @param strategyId
     * @return Response<Boolean>
     */
    @RequestMapping(value = "strategy_armory", method = RequestMethod.GET)
    @Override
    public Response<Boolean> strategyArmory(@RequestParam Long strategyId) {
        try {
            log.info("raffle strategy armory start  strategyId:{}", strategyId);
            boolean armoryStatus = iStrategyArmory.assembleRaffleStrategy(strategyId);
            Response<Boolean> response = Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(armoryStatus)
                    .build();
            log.info("raffle strategy armory complete  strategyId:{} response:{}", strategyId, JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("raffle strategy armory error  strategyId:{}", strategyId);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * query raffle award list(only store each award entity once)
     * <a href="http://localhost:8091/api/v1/raffle/query_raffle_award_list">/api/v1/raffle/query_raffle_award_list</a>
     * @param raffleAwardListRequestDTO
     * @return Response<List<RaffleAwardListResponseDTO>>
     */
    @RequestMapping(value = "query_raffle_award_list", method = RequestMethod.POST)
    @Override
    public Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(@RequestBody RaffleAwardListRequestDTO raffleAwardListRequestDTO) {
        try {
            log.info("query raffle award list start  strategyId:{}", raffleAwardListRequestDTO.getStrategyId());
            List<StrategyAwardEntity> strategyAwardEntityList = iRaffleAward.queryStrategyAwardList(raffleAwardListRequestDTO.getStrategyId());
            List<RaffleAwardListResponseDTO> raffleAwardListResponseDTOList = new ArrayList<>();
            for (StrategyAwardEntity strategyAward : strategyAwardEntityList) {
                raffleAwardListResponseDTOList.add(RaffleAwardListResponseDTO.builder()
                        .awardId(strategyAward.getAwardId())
                        .awardTitle(strategyAward.getAwardTitle())
                        .awardSubtitle(strategyAward.getAwardSubtitle())
                        .sort(strategyAward.getSort())
                        .build());
            }

            Response<List<RaffleAwardListResponseDTO>> response = Response.<List<RaffleAwardListResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(raffleAwardListResponseDTOList)
                    .build();
            log.info("query raffle award list complete  strategyId:{} response:{}", raffleAwardListRequestDTO.getStrategyId(), JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("query raffle award list error  strategyId:{}", raffleAwardListRequestDTO.getStrategyId());
            return Response.<List<RaffleAwardListResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * execute random raffle
     * <a href="http://localhost:8091/api/v1/raffle/random_raffle">/api/v1/raffle/random_raffle</a>
     * @param raffleRequestDTO
     * @return Response<RaffleResponseDTO>
     */
    @RequestMapping(value = "random_raffle", method = RequestMethod.POST)
    @Override
    public Response<RaffleResponseDTO> randomRaffle(@RequestBody RaffleRequestDTO raffleRequestDTO) {
        RaffleAwardEntity raffleAwardEntity = iRaffleStrategy.performRaffleLogicChainWithRuleTree(RaffleFactorEntity.builder()
                .userId("system")  // blacklist user: user001,user002,user003
                .strategyId(raffleRequestDTO.getStrategyId())
                .build());
        try {
            log.info("random raffle start  strategyId:{} userId:{}", raffleRequestDTO.getStrategyId(), "system");
            Response<RaffleResponseDTO> response = Response.<RaffleResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(RaffleResponseDTO.builder()
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardIndex(raffleAwardEntity.getSort())
                            .build())
                    .build();
            log.info("random raffle complete  strategyId:{} userId:{} response:{}", raffleRequestDTO.getStrategyId(), "system", JSON.toJSONString(response));
            return response;
        } catch (AppException e) {
            log.error("random raffle strategyId：{} {}", raffleRequestDTO.getStrategyId(), e.getInfo());
            return Response.<RaffleResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("random raffle strategyId：{}", raffleRequestDTO.getStrategyId(), e);
            return Response.<RaffleResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
