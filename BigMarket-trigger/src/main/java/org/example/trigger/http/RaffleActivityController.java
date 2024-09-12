package org.example.trigger.http;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.domain.activity.model.entity.UserRaffleOrderEntity;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.domain.activity.service.IRaffleActivityPartakeService;
import org.example.domain.activity.service.armory.IActivityArmory;
import org.example.domain.award.model.entity.UserAwardRecordEntity;
import org.example.domain.award.model.vo.AwardStatusVO;
import org.example.domain.award.service.IAwardService;
import org.example.domain.strategy.model.entity.RaffleAwardEntity;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.domain.strategy.service.IRaffleAward;
import org.example.domain.strategy.service.IRaffleStrategy;
import org.example.domain.strategy.service.armory.IStrategyArmory;
import org.example.trigger.api.IRaffleActivityService;
import org.example.trigger.api.dto.ActivityDrawRequestDTO;
import org.example.trigger.api.dto.ActivityDrawResponseDTO;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.example.types.model.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@RestController()
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/activity")
public class RaffleActivityController implements IRaffleActivityService {

    @Resource
    private IRaffleActivityPartakeService iRaffleActivityPartakeService;

    @Resource
    private IRaffleStrategy iRaffleStrategy;

    @Resource
    private IAwardService iAwardService;

    @Resource
    private IActivityArmory iActivityArmory;

    @Resource
    private IStrategyArmory iStrategyArmory;

    /**
     * armory the raffle activity sku and strategy into cache
     * <a href="http://localhost:8091/api/v1/raffle/activity/activity_armory">/api/v1/raffle/activity/activity_armory</a>
     *
     * @param activityId
     * @return Response<Boolean>
     */
    @Override
    @RequestMapping(value = "activity_armory", method = RequestMethod.GET)
    public Response<Boolean> activityArmory(@RequestParam Long activityId) {
        try {
            log.info("raffle activity armory start, activityId:{}", activityId);
            /** assemble activity sku */
            boolean armoryStatus = iActivityArmory.assembleActivitySkuByActivityId(activityId);
            /** assemble strategy */
            armoryStatus = armoryStatus && iStrategyArmory.assembleRaffleStrategyByActivityId(activityId);

            Response<Boolean> response = Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(armoryStatus)
                    .build();
            log.info("raffle activity armory success, activityId:{}", activityId);
            return response;
        } catch (Exception e) {
            log.error("raffle activity armory error, activityId:{}", activityId);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * do the lucky draw
     * <a href="http://localhost:8091/api/v1/raffle/activity/draw">/api/v1/raffle/activity/draw</a>
     *
     * @param activityDrawRequestDTO
     * @return Response<ActivityDrawResponseDTO>
     */
    @Override
    @RequestMapping(value = "draw", method = RequestMethod.POST)
    public Response<ActivityDrawResponseDTO> draw(@RequestBody ActivityDrawRequestDTO activityDrawRequestDTO) {
        try {
            log.info("lucky draw - start, userId:{}, activityId:{}", activityDrawRequestDTO.getUserId(), activityDrawRequestDTO.getActivityId());
            /** first check parameter */
            if (StringUtils.isBlank(activityDrawRequestDTO.getUserId()) || activityDrawRequestDTO.getActivityId() == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            /** create activity order and save */
            UserRaffleOrderEntity userRaffleOrderEntity = iRaffleActivityPartakeService.createRaffleOrder(activityDrawRequestDTO.getUserId(), activityDrawRequestDTO.getActivityId());
            log.info("lucky draw - create RaffleOrder, orderId:{}", userRaffleOrderEntity.getOrderId());
            /** start random raffle */
            RaffleAwardEntity raffleAwardEntity = iRaffleStrategy.performRaffleLogicChainWithRuleTree(RaffleFactorEntity.builder()
                    .userId(userRaffleOrderEntity.getUserId())  // blacklist user: user001,user002,user003
                    .strategyId(userRaffleOrderEntity.getStrategyId())
                    .build());
            /** create use award record and save */
            UserAwardRecordEntity userAwardRecordEntity = new UserAwardRecordEntity();
            userAwardRecordEntity.setUserId(userRaffleOrderEntity.getUserId());
            userAwardRecordEntity.setActivityId(userRaffleOrderEntity.getActivityId());
            userAwardRecordEntity.setStrategyId(userRaffleOrderEntity.getStrategyId());
            userAwardRecordEntity.setOrderId(userRaffleOrderEntity.getOrderId());
            userAwardRecordEntity.setAwardId(raffleAwardEntity.getAwardId());
            userAwardRecordEntity.setAwardTitle(raffleAwardEntity.getAwardTitle());
            userAwardRecordEntity.setAwardTime(new Date());
            userAwardRecordEntity.setAwardStatus(AwardStatusVO.create);
            iAwardService.saveUserAwardRecord(userAwardRecordEntity);

            return Response.<ActivityDrawResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(ActivityDrawResponseDTO.builder()
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardTitle(raffleAwardEntity.getAwardTitle())
                            .awardIndex(raffleAwardEntity.getSort())
                            .build())
                    .build();
        } catch (AppException e) {
            log.error("raffle activity error, userId:{}, activityId:{}", activityDrawRequestDTO.getUserId(), activityDrawRequestDTO.getActivityId(),e);
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e){
            log.error("raffle activity error, userId:{}, activityId:{}", activityDrawRequestDTO.getUserId(), activityDrawRequestDTO.getActivityId(),e);
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
