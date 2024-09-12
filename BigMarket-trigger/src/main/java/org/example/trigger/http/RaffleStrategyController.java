package org.example.trigger.http;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.domain.activity.service.IRaffleActivityAccountQuotaService;
import org.example.domain.strategy.model.entity.RaffleAwardEntity;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;
import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.service.IRaffleAward;
import org.example.domain.strategy.service.IRaffleAwardRule;
import org.example.domain.strategy.service.IRaffleStrategy;
import org.example.domain.strategy.service.armory.IStrategyArmory;
import org.example.trigger.api.IRaffleStrategyService;
import org.example.trigger.api.dto.RaffleAwardListRequestDTO;
import org.example.trigger.api.dto.RaffleAwardListResponseDTO;
import org.example.trigger.api.dto.RaffleStrategyRequestDTO;
import org.example.trigger.api.dto.RaffleStrategyResponseDTO;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.example.types.model.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * raffle service
 */

@Slf4j
@RestController()
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/strategy")
public class RaffleStrategyController implements IRaffleStrategyService {

    @Resource
    private IStrategyArmory iStrategyArmory;

    @Resource
    private IRaffleAward iRaffleAward;

    @Resource
    private IRaffleStrategy iRaffleStrategy;

    @Resource
    private IRaffleAwardRule iRaffleAwardRule;

    @Resource
    private IRaffleActivityAccountQuotaService iRaffleActivityAccountQuotaService;

    /**
     * armory the raffle table into cache
     * <a href="http://localhost:8091/api/v1/raffle/strategy/strategy_armory">/api/v1/raffle/strategy/strategy_armory</a>
     *
     * @param strategyId
     * @return Response<Boolean>
     */
    @RequestMapping(value = "strategy_armory", method = RequestMethod.GET)
    @Override
    public Response<Boolean> strategyArmory(@RequestParam Long strategyId) {
        try {
            log.info("raffle strategy armory start, strategyId:{}", strategyId);
            boolean armoryStatus = iStrategyArmory.assembleRaffleStrategy(strategyId);
            Response<Boolean> response = Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(armoryStatus)
                    .build();
            log.info("raffle strategy armory complete, strategyId:{}, response:{}", strategyId, JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("raffle strategy armory error, strategyId:{}", strategyId);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * query raffle award list(only store each award entity once)
     * <a href="http://localhost:8091/api/v1/raffle/strategy/query_raffle_award_list">/api/v1/raffle/strategy/query_raffle_award_list</a>
     *
     * @param raffleAwardListRequestDTO
     * @return Response<List < RaffleAwardListResponseDTO>>
     */
    @RequestMapping(value = "query_raffle_award_list", method = RequestMethod.POST)
    @Override
    public Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(@RequestBody RaffleAwardListRequestDTO raffleAwardListRequestDTO) {
        try {
            /** check parameter */
            if (StringUtils.isBlank(raffleAwardListRequestDTO.getUserId()) || raffleAwardListRequestDTO.getActivityId() == null) {
                log.error("query raffle award list error - invalid parameter, userId:{}, activityId:{}", raffleAwardListRequestDTO.getUserId(),raffleAwardListRequestDTO.getActivityId());
                return Response.<List<RaffleAwardListResponseDTO>>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            log.info("query raffle award list start, userId:{}, activityId:{}", raffleAwardListRequestDTO.getUserId(),raffleAwardListRequestDTO.getActivityId());
            /**
             * query award list
             * for raffle_award table, award only bind the tree_id as their rule_model
             * so for one strategyId, tree_id may be different for different award
             */
            List<StrategyAwardEntity> strategyAwardEntityList = iRaffleAward.queryStrategyAwardListByActivityId(raffleAwardListRequestDTO.getActivityId());
            /** get rule model, actually is treeId */
            String[] treeIds = strategyAwardEntityList.stream()
                    .map(StrategyAwardEntity::getRuleModel)
                    .filter(Objects::nonNull)
                    .toArray(String[]::new);
            /** query ruleValue of rule_lock tree node */
            Map<String,Integer> ruleLockAmountMap = iRaffleAwardRule.queryRuleTreeLockNodeValueByTreeIds(treeIds);
            /** query user raffle amount */
            Integer dayPartakeAmount = iRaffleActivityAccountQuotaService.queryRaffleActivityAccountDayPartakeAmount(raffleAwardListRequestDTO.getUserId(),raffleAwardListRequestDTO.getActivityId());


            List<RaffleAwardListResponseDTO> raffleAwardListResponseDTOList = new ArrayList<>();
            for (StrategyAwardEntity strategyAward : strategyAwardEntityList) {
                Integer ruleLockAmount = ruleLockAmountMap.getOrDefault(strategyAward.getRuleModel(),0);
                raffleAwardListResponseDTOList.add(RaffleAwardListResponseDTO.builder()
                        .awardId(strategyAward.getAwardId())
                        .awardTitle(strategyAward.getAwardTitle())
                        .awardSubtitle(strategyAward.getAwardSubtitle())
                        .sort(strategyAward.getSort())
                        .awardUnlockAmount(ruleLockAmount)
                        .isUnlock(dayPartakeAmount >= ruleLockAmount)
                        .awardUnlockRemain(dayPartakeAmount >= ruleLockAmount? 0 : ruleLockAmount - dayPartakeAmount)
                        .build());
            }

            Response<List<RaffleAwardListResponseDTO>> response = Response.<List<RaffleAwardListResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(raffleAwardListResponseDTOList)
                    .build();
            log.info("query raffle award list complete, userId:{}, activityId:{}, response:{}", raffleAwardListRequestDTO.getUserId(), raffleAwardListRequestDTO.getActivityId(), JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("query raffle award list error, userId:{}, activityId:{}", raffleAwardListRequestDTO.getUserId(),raffleAwardListRequestDTO.getActivityId());
            return Response.<List<RaffleAwardListResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * execute random raffle
     * <a href="http://localhost:8091/api/v1/raffle/random_raffle">/api/v1/raffle/random_raffle</a>
     *
     * @param raffleStrategyRequestDTO
     * @return Response<RaffleResponseDTO>
     */
    @RequestMapping(value = "random_raffle", method = RequestMethod.POST)
    @Override
    public Response<RaffleStrategyResponseDTO> randomRaffle(@RequestBody RaffleStrategyRequestDTO raffleStrategyRequestDTO) {
        RaffleAwardEntity raffleAwardEntity = iRaffleStrategy.performRaffleLogicChainWithRuleTree(RaffleFactorEntity.builder()
                .userId("system")  // blacklist user: user001,user002,user003
                .strategyId(raffleStrategyRequestDTO.getStrategyId())
                .build());
        try {
            log.info("random raffle start, strategyId:{}, userId:{}", raffleStrategyRequestDTO.getStrategyId(), "system");
            Response<RaffleStrategyResponseDTO> response = Response.<RaffleStrategyResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(RaffleStrategyResponseDTO.builder()
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardIndex(raffleAwardEntity.getSort())
                            .build())
                    .build();
            log.info("random raffle complete, strategyId:{}, userId:{}, response:{}", raffleStrategyRequestDTO.getStrategyId(), "system", JSON.toJSONString(response));
            return response;
        } catch (AppException e) {
            log.error("random raffle error, strategyId：{} {}", raffleStrategyRequestDTO.getStrategyId(), e.getInfo());
            return Response.<RaffleStrategyResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("random raffle error, strategyId：{}", raffleStrategyRequestDTO.getStrategyId(), e);
            return Response.<RaffleStrategyResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
