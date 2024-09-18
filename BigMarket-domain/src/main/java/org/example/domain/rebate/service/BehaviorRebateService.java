package org.example.domain.rebate.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.example.domain.rebate.event.SendRebateMessageEvent;
import org.example.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import org.example.domain.rebate.model.entity.BehaviorEntity;
import org.example.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import org.example.domain.rebate.model.entity.TaskEntity;
import org.example.domain.rebate.model.vo.DailyBehaviorRebateVO;
import org.example.domain.rebate.model.vo.TaskStatusVO;
import org.example.domain.rebate.repository.IBehaviorRebateRepository;
import org.example.types.event.BaseEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class BehaviorRebateService implements IBehaviorRebateService{

    @Resource
    private IBehaviorRebateRepository iBehaviorRebateRepository;
    @Resource
    private SendRebateMessageEvent sendRebateMessageEvent;

    @Override
    public List<String> createRebateOrder(BehaviorEntity behaviorEntity) {
        /** get the rebate config */
        List<DailyBehaviorRebateVO> dailyBehaviorRebateVOList = iBehaviorRebateRepository.queryDailyBehaviorRebateConfig(behaviorEntity.getBehaviorTypeVO());
        if (dailyBehaviorRebateVOList == null || dailyBehaviorRebateVOList.isEmpty()) return new ArrayList<>();

        /** build aggregation List for further database operation */
        List<String> rebateOrderIdList = new ArrayList<>();
        List<BehaviorRebateAggregate> behaviorRebateAggregateList = new ArrayList<>();
        for(DailyBehaviorRebateVO dailyBehaviorRebateVO : dailyBehaviorRebateVOList){
            String bizId = behaviorEntity.getUserId() + "_" + dailyBehaviorRebateVO.getRebateType() + "_" + behaviorEntity.getOutBusinessNo();
            BehaviorRebateOrderEntity behaviorRebateOrderEntity = BehaviorRebateOrderEntity.builder()
                        .userId(behaviorEntity.getUserId())
                        .orderId(RandomStringUtils.randomNumeric(11))
                        .behaviorType(dailyBehaviorRebateVO.getBehaviorType())
                        .rebateDesc(dailyBehaviorRebateVO.getRebateDesc())
                        .rebateType(dailyBehaviorRebateVO.getRebateType())
                        .rebateConfig(dailyBehaviorRebateVO.getRebateConfig())
                        .bizId(bizId)
                        .build();
            rebateOrderIdList.add(behaviorRebateOrderEntity.getOrderId());
            /** build MQ message */
            SendRebateMessageEvent.SendRebateMessage rebateMessage = SendRebateMessageEvent.SendRebateMessage.builder()
                    .userId(behaviorEntity.getUserId())
                    .rebateType(dailyBehaviorRebateVO.getRebateType())
                    .rebateConfig(dailyBehaviorRebateVO.getRebateConfig())
                    .rebateDesc(dailyBehaviorRebateVO.getRebateDesc())
                    .bizId(bizId)
                    .build();
            /** build message event object*/
            BaseEvent.EventMessage<SendRebateMessageEvent.SendRebateMessage> rebateMessageEventMessage = sendRebateMessageEvent.buildEventMessage(rebateMessage);
            /** build task object */
            TaskEntity taskEntity = TaskEntity.builder()
                    .topic(sendRebateMessageEvent.topic())
                    .userId(behaviorEntity.getUserId())
                    .messageId(rebateMessageEventMessage.getId())
                    .message(rebateMessageEventMessage)
                    .status(TaskStatusVO.create)
                    .build();
            /** build aggregation object */
            BehaviorRebateAggregate behaviorRebateAggregate = BehaviorRebateAggregate.builder()
                    .behaviorRebateOrderEntity(behaviorRebateOrderEntity)
                    .taskEntity(taskEntity)
                    .userId(behaviorEntity.getUserId())
                    .build();

            behaviorRebateAggregateList.add(behaviorRebateAggregate);
        }

        iBehaviorRebateRepository.doSaveUserRebateOrder(behaviorEntity.getUserId(), behaviorRebateAggregateList);

        return rebateOrderIdList;
    }
}
