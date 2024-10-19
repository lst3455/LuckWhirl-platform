package org.example.trigger.http;

import com.alibaba.fastjson2.JSON;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.example.domain.activity.model.entity.*;
import org.example.domain.activity.model.vo.OrderTradeTypeVO;
import org.example.domain.activity.service.IRaffleActivityAccountQuotaService;
import org.example.domain.activity.service.IRaffleActivityPartakeService;
import org.example.domain.activity.service.IRaffleActivitySkuProductService;
import org.example.domain.activity.service.armory.IActivityArmory;
import org.example.domain.award.model.entity.UserAwardRecordEntity;
import org.example.domain.award.model.vo.AwardStatusVO;
import org.example.domain.award.service.IAwardService;
import org.example.domain.point.model.entity.TradeEntity;
import org.example.domain.point.model.entity.UserPointAccountEntity;
import org.example.domain.point.model.vo.TradeNameVO;
import org.example.domain.point.model.vo.TradeTypeVO;
import org.example.domain.point.service.IPointUpdateService;
import org.example.domain.rebate.model.entity.BehaviorEntity;
import org.example.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import org.example.domain.rebate.model.vo.BehaviorTypeVO;
import org.example.domain.rebate.service.IBehaviorRebateService;
import org.example.domain.strategy.model.entity.RaffleAwardEntity;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;
import org.example.domain.strategy.service.IRaffleStrategy;
import org.example.domain.strategy.service.armory.IStrategyArmory;
import org.example.trigger.api.IRaffleActivityService;
import org.example.trigger.api.dto.*;
import org.example.types.annotation.DCCValue;
import org.example.types.annotation.RateLimitAccessInterceptor;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.example.types.model.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController()
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/activity")
@DubboService(version = "1.0")
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

    @Resource
    private IPointUpdateService iPointUpdateService;

    @Resource
    private IRaffleActivitySkuProductService iRaffleActivitySkuProductService;

    @DCCValue("degradeSwitch:open")
    private String degradeSwitch;

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
    @RateLimitAccessInterceptor(key = "userId", feedbackMethod = "drawRateLimitError", permitPerSecond = 60, blackListCount = 5)
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "150")
    }, fallbackMethod = "drawHystrixError"
    )
    public Response<ActivityDrawResponseDTO> draw(@RequestBody ActivityDrawRequestDTO activityDrawRequestDTO) {
        try {
            log.info("lucky draw - start, userId:{}, activityId:{}", activityDrawRequestDTO.getUserId(), activityDrawRequestDTO.getActivityId());
            /** for dynamic config control */
            if ("open".equals(degradeSwitch)){
                log.info("lucky draw - degrade switch, userId:{}, activityId:{}", activityDrawRequestDTO.getUserId(), activityDrawRequestDTO.getActivityId());
                return Response.<ActivityDrawResponseDTO>builder()
                        .code(ResponseCode.DEGRADE_SWITCH.getCode())
                        .info(ResponseCode.DEGRADE_SWITCH.getInfo())
                        .build();
            }

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

    public Response<ActivityDrawResponseDTO> drawRateLimitError(@RequestBody ActivityDrawRequestDTO request) {
        log.info("lucky draw - reach rate limit, please try after 1 min, userId:{} activityId:{}", request.getUserId(), request.getActivityId());
        return Response.<ActivityDrawResponseDTO>builder()
                .code(ResponseCode.RATE_LIMIT.getCode())
                .info(ResponseCode.RATE_LIMIT.getInfo())
                .build();
    }

    public Response<ActivityDrawResponseDTO> drawHystrixError(@RequestBody ActivityDrawRequestDTO request) {
        log.info("lucky draw - timeout, please try later, userId:{} activityId:{}", request.getUserId(), request.getActivityId());
        return Response.<ActivityDrawResponseDTO>builder()
                .code(ResponseCode.HYSTRIX.getCode())
                .info(ResponseCode.HYSTRIX.getInfo())
                .build();
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

    /**
     * user use point to redeem sku to charge draw amount
     * <a href="http://localhost:8091/api/v1/raffle/activity/point_redeem_sku">/api/v1/raffle/activity/point_redeem_sku</a>
     *
     * @param skuProductRequestDTO
     * @return
     */
    @Override
    @RequestMapping(value = "point_redeem_sku", method = RequestMethod.POST)
    public Response<Boolean> pointRedeemSku(@RequestBody SkuProductRequestDTO skuProductRequestDTO) {
        try {
            log.info("point redeem sku start, userId:{}, sku:{}", skuProductRequestDTO.getUserId(), skuProductRequestDTO.getSku());
            /** create activity order pending */
            ActivitySkuChargeEntity activitySkuChargeEntity = new ActivitySkuChargeEntity();
            activitySkuChargeEntity.setSku(skuProductRequestDTO.getSku());
            activitySkuChargeEntity.setUserId(skuProductRequestDTO.getUserId());
            activitySkuChargeEntity.setOutBusinessNo(RandomStringUtils.randomNumeric(12));
            activitySkuChargeEntity.setOrderTradeTypeVO(OrderTradeTypeVO.pay_trade);
            PendingActivityOrderEntity pendingActivityOrderEntity = iRaffleActivityAccountQuotaService.createSkuChargeOrder(activitySkuChargeEntity);

            TradeEntity tradeEntity = TradeEntity.builder()
                    .userId(skuProductRequestDTO.getUserId())
                    .tradeName(TradeNameVO.REBATE)
                    .tradeType(TradeTypeVO.SUBTRACTION)
                    .tradeAmount(pendingActivityOrderEntity.getPointAmount().negate())
                    .outBusinessNo(pendingActivityOrderEntity.getOutBusinessNo())
                    .build();
            /** update activity order pending to complete and update activity account*/
            String orderId = iPointUpdateService.createPayTypeUserPointOrder(tradeEntity);
            log.info("point redeem sku complete, userId:{}, sku:{}, orderId:{}", skuProductRequestDTO.getUserId(), skuProductRequestDTO.getSku(), orderId);
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("point redeem sku error, userId:{}", skuProductRequestDTO.getUserId(), e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * query user point account
     * <a href="http://localhost:8091/api/v1/raffle/activity/query_user_point_account">/api/v1/raffle/activity/query_user_point_account</a>
     *
     * @param userId
     * @return
     */
    @Override
    @RequestMapping(value = "query_user_point_account", method = RequestMethod.POST)
    public Response<BigDecimal> queryUserPointAccount(@RequestParam String userId) {
        try {
            log.info("query user point account start, userId:{}", userId);
            UserPointAccountEntity userPointAccountEntity = iPointUpdateService.queryUserPointAccount(userId);

            log.info("query user point account complete, userId:{}", userId);
            return Response.<BigDecimal>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(userPointAccountEntity.getAvailableAmount())
                    .build();
        } catch (Exception e) {
            log.error("query user point account complete, userId:{}", userId, e);
            return Response.<BigDecimal>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * query sku product list by activityId
     * <a href="http://localhost:8091/api/v1/raffle/activity/query_sku_product_list_by_activityId">/api/v1/raffle/activity/query_sku_product_list_by_activityId</a>
     *
     * @param activityId
     * @return
     */
    @Override
    @RequestMapping(value = "query_sku_product_list_by_activityId", method = RequestMethod.POST)
    public Response<List<SkuProductResponseDTO>> querySkuProductListByActivityId(@RequestParam Long activityId) {
        try {
            log.info("query sku product list by activityId start, activityId:{}", activityId);
            if (activityId == null){
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            List<SkuProductEntity> skuProductEntityList = iRaffleActivitySkuProductService.querySkuProductEntityListByActivityId(activityId);
            List<SkuProductResponseDTO> skuProductResponseDTOList = new ArrayList<>(skuProductEntityList.size());
            for (SkuProductEntity skuProductEntity : skuProductEntityList) {
                SkuProductResponseDTO.ActivityAmount activityAmount = new SkuProductResponseDTO.ActivityAmount();
                activityAmount.setTotalAmount(skuProductEntity.getActivityAmount().getTotalAmount());
                activityAmount.setMonthAmount(skuProductEntity.getActivityAmount().getMonthAmount());
                activityAmount.setDayAmount(skuProductEntity.getActivityAmount().getDayAmount());

                SkuProductResponseDTO skuProductResponseDTO = new SkuProductResponseDTO();
                skuProductResponseDTO.setSku(skuProductEntity.getSku());
                skuProductResponseDTO.setActivityId(skuProductEntity.getActivityId());
                skuProductResponseDTO.setActivityAmountId(skuProductEntity.getActivityAmountId());
                skuProductResponseDTO.setStockAmount(skuProductEntity.getStockAmount());
                skuProductResponseDTO.setStockRemain(skuProductEntity.getStockRemain());
                skuProductResponseDTO.setPointAmount(skuProductEntity.getPointAmount());
                skuProductResponseDTO.setActivityAmount(activityAmount);
                skuProductResponseDTOList.add(skuProductResponseDTO);
            }
            log.info("query sku product list by activityId complete, activityId:{}", activityId);
            return Response.<List<SkuProductResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(skuProductResponseDTOList)
                    .build();
        } catch (Exception e) {
            log.error("query sku product list by activityId error, activityId:{}", activityId, e);
            return Response.<List<SkuProductResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
