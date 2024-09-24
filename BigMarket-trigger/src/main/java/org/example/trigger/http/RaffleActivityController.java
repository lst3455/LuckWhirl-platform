package org.example.trigger.http;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.domain.activity.model.entity.ActivityAccountEntity;
import org.example.domain.activity.model.entity.UserRaffleOrderEntity;
import org.example.domain.activity.service.IRaffleActivityAccountQuotaService;
import org.example.domain.activity.service.IRaffleActivityPartakeService;
import org.example.domain.activity.service.armory.IActivityArmory;
import org.example.domain.award.model.entity.UserAwardRecordEntity;
import org.example.domain.award.model.vo.AwardStatusVO;
import org.example.domain.award.service.IAwardService;
import org.example.domain.rebate.model.entity.BehaviorEntity;
import org.example.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import org.example.domain.rebate.model.vo.BehaviorTypeVO;
import org.example.domain.rebate.service.IBehaviorRebateService;
import org.example.domain.strategy.model.entity.RaffleAwardEntity;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;
import org.example.domain.strategy.service.IRaffleStrategy;
import org.example.domain.strategy.service.armory.IStrategyArmory;
import org.example.trigger.api.IRaffleActivityService;
import org.example.trigger.api.dto.ActivityDrawRequestDTO;
import org.example.trigger.api.dto.ActivityDrawResponseDTO;
import org.example.trigger.api.dto.UserActivityAccountRequestDTO;
import org.example.trigger.api.dto.UserActivityAccountResponseDTO;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.example.types.model.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController()
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/activity")
public class RaffleActivityController implements IRaffleActivityService {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    @Resource
    private IRaffleActivityPartakeService iRaffleActivityPartakeService;

    @Resource
    private IRaffleActivityAccountQuotaService iRaffleActivityAccountQuotaService;

    @Resource
    private IRaffleStrategy iRaffleStrategy;

    @Resource
    private IAwardService iAwardService;

    @Resource
    private IActivityArmory iActivityArmory;

    @Resource
    private IStrategyArmory iStrategyArmory;

    @Resource
    private IBehaviorRebateService iBehaviorRebateService;

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
            log.info("raffle activity armory complete, activityId:{}", activityId);
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
                    .endDateTime(userRaffleOrderEntity.getEndDateTime())
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
            userAwardRecordEntity.setAwardConfig(raffleAwardEntity.getAwardConfig());
            iAwardService.saveUserAwardRecord(userAwardRecordEntity);
            log.info("lucky draw - complete, userId:{}, activityId:{}", activityDrawRequestDTO.getUserId(), activityDrawRequestDTO.getActivityId());
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
            log.error("raffle activity error, userId:{}, activityId:{}", activityDrawRequestDTO.getUserId(), activityDrawRequestDTO.getActivityId(), e);
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("raffle activity error, userId:{}, activityId:{}", activityDrawRequestDTO.getUserId(), activityDrawRequestDTO.getActivityId(), e);
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * do daily sign in, get rebate
     * <a href="http://localhost:8091/api/v1/raffle/activity/daily_sign_rebate">/api/v1/raffle/activity/daily_sign_rebate</a>
     *
     * @param userId
     * @return Response<Boolean>
     */
    @Override
    @RequestMapping(value = "daily_sign_rebate", method = RequestMethod.POST)
    public Response<Boolean> dailySignRebate(@RequestParam String userId) {
        try {
            log.info("daily sign rebate start, userId:{}", userId);
            BehaviorEntity behaviorEntity = BehaviorEntity.builder()
                    .userId(userId)
                    .behaviorTypeVO(BehaviorTypeVO.SIGN)
                    .outBusinessNo(simpleDateFormat.format(new Date()))
                    .build();
            List<String> rebateOrderIdList = iBehaviorRebateService.createRebateOrder(behaviorEntity);
            log.info("daily sign rebate complete, userId:{}, rebateOrderIdList:{}", userId, JSON.toJSONString(rebateOrderIdList));
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(true)
                    .build();
        } catch (AppException e) {
            log.error("daily sign rebate error, userId:{}", userId, e);
            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("daily sign rebate error, userId:{}", userId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * check if daily sign in complete
     * <a href="http://localhost:8091/api/v1/raffle/activity/is_daily_sign_rebate_get">/api/v1/raffle/activity/is_daily_sign_rebate_get</a>
     *
     * @param userId
     * @return
     */
    @Override
    @RequestMapping(value = "is_daily_sign_rebate_get", method = RequestMethod.POST)
    public Response<Boolean> isDailySignRebateGet(String userId) {
        try {
            log.info("is dail sign rebate get start, userId:{}", userId);
            List<BehaviorRebateOrderEntity> behaviorRebateOrderEntityList = iBehaviorRebateService.queryBehaviorRebateOrderByOutBusinessNo(userId, simpleDateFormat.format(new Date()));
            log.info("is dail sign rebate get complete, userId:{}", userId);
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(behaviorRebateOrderEntityList != null && !behaviorRebateOrderEntityList.isEmpty())
                    .build();
        } catch (Exception e) {
            log.error("is dail sign rebate get error, userId:{}", userId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * query user raffle activity account amount and remain
     * * <a href="http://localhost:8091/api/v1/raffle/activity/query_raffle_activity_account">/api/v1/raffle/activity/query_raffle_activity_account</a>
     *
     * @param userActivityAccountRequestDTO
     * @return
     */
    @Override
    @RequestMapping(value = "query_raffle_activity_account", method = RequestMethod.POST)
    public Response<UserActivityAccountResponseDTO> queryRaffleActivityAccount(@RequestBody UserActivityAccountRequestDTO userActivityAccountRequestDTO) {
        try {
            log.info("query raffle activity account start, userId:{}, activityId:{}", userActivityAccountRequestDTO.getUserId(), userActivityAccountRequestDTO.getActivityId());
            if (StringUtils.isBlank(userActivityAccountRequestDTO.getUserId()) || null == userActivityAccountRequestDTO.getActivityId()) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            ActivityAccountEntity activityAccountEntity = iRaffleActivityAccountQuotaService.queryActivityAccountEntity(userActivityAccountRequestDTO.getUserId(), userActivityAccountRequestDTO.getActivityId());
            UserActivityAccountResponseDTO userActivityAccountResponseDTO = UserActivityAccountResponseDTO.builder()
                    .totalAmount(activityAccountEntity.getTotalAmount())
                    .totalRemain(activityAccountEntity.getTotalRemain())
                    .dayAmount(activityAccountEntity.getDayAmount())
                    .dayRemain(activityAccountEntity.getDayRemain())
                    .monthAmount(activityAccountEntity.getMonthAmount())
                    .monthRemain(activityAccountEntity.getMonthRemain())
                    .build();
            log.info("query raffle activity account complete, userId:{}, activityId:{}, dto:{}", userActivityAccountRequestDTO.getUserId(), userActivityAccountRequestDTO.getActivityId(), JSON.toJSONString(userActivityAccountResponseDTO));
            return Response.<UserActivityAccountResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(userActivityAccountResponseDTO)
                    .build();
        } catch (Exception e) {
            log.error("query raffle activity account error, userId:{}, activityId:{}", userActivityAccountRequestDTO.getUserId(), userActivityAccountRequestDTO.getActivityId(), e);
            return Response.<UserActivityAccountResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
