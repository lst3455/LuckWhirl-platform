package org.example.trigger.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.example.domain.award.model.entity.UserAwardRecordEntity;
import org.example.domain.award.service.IAwardService;
import org.example.trigger.api.IRaffleAwardService;
import org.example.trigger.api.dto.UserAwardRecordRequestDTO;
import org.example.trigger.api.dto.UserAwardRecordResponseDTO;
import org.example.types.enums.ResponseCode;
import org.example.types.model.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController()
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/award")
@DubboService(version = "1.0")
public class RaffleAwardController implements IRaffleAwardService {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Resource
    private IAwardService iAwardService;

    /**
     * query user award record list
     * <a href="http://localhost:8091/api/v1/raffle/award/query_user_award_record">/api/v1/raffle/award/query_user_award_record</a>
     *
     * @param userAwardRecordRequestDTO
     * @return
     */
    @Override
    @RequestMapping(value = "query_user_award_record", method = RequestMethod.POST)
    public Response<List<UserAwardRecordResponseDTO>> queryUserAwardRecordList(@RequestBody UserAwardRecordRequestDTO userAwardRecordRequestDTO) {
        try {
            log.info("query user award record start, userId:{}, activityId:{}", userAwardRecordRequestDTO.getUserId(),userAwardRecordRequestDTO.getActivityId());
            UserAwardRecordEntity userAwardRecordEntity = new UserAwardRecordEntity();
            userAwardRecordEntity.setUserId(userAwardRecordRequestDTO.getUserId());
            userAwardRecordEntity.setActivityId(userAwardRecordRequestDTO.getActivityId());
            List<UserAwardRecordEntity> userAwardRecordEntityList = iAwardService.queryUserAwardRecordList(userAwardRecordEntity);
            List<UserAwardRecordResponseDTO> userAwardRecordResponseDTOList = new ArrayList<>();

            for(UserAwardRecordEntity userAwardRecord : userAwardRecordEntityList){
                UserAwardRecordResponseDTO userAwardRecordResponseDTO = new UserAwardRecordResponseDTO();
                userAwardRecordResponseDTO.setAwardTitle(userAwardRecord.getAwardTitle());
                userAwardRecordResponseDTO.setUserId(userAwardRecord.getUserId());
                userAwardRecordResponseDTO.setAwardTime(simpleDateFormat.format(userAwardRecord.getAwardTime()));

                userAwardRecordResponseDTOList.add(userAwardRecordResponseDTO);
            }

            log.info("query user award record complete, userId:{}, activityId:{}", userAwardRecordRequestDTO.getUserId(),userAwardRecordRequestDTO.getActivityId());
            return Response.<List<UserAwardRecordResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(userAwardRecordResponseDTOList)
                    .build();
        } catch (Exception e) {
            log.error("query user award record error, userId:{}, activityId:{}", userAwardRecordRequestDTO.getUserId(),userAwardRecordRequestDTO.getActivityId(), e);
            return Response.<List<UserAwardRecordResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
