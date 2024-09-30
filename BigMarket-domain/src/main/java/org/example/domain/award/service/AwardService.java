package org.example.domain.award.service;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.award.event.SendAwardMessageEvent;
import org.example.domain.award.model.aggregate.UserAwardRecordAggregate;
import org.example.domain.award.model.entity.DeliveryAwardEntity;
import org.example.domain.award.model.entity.TaskEntity;
import org.example.domain.award.model.entity.UserAwardRecordEntity;
import org.example.domain.award.model.vo.TaskStatusVO;
import org.example.domain.award.repository.IAwardRepository;
import org.example.domain.award.service.delivery.IDeliveryAward;
import org.example.types.event.BaseEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AwardService implements IAwardService{

    @Resource
    private IAwardRepository iAwardRepository;

    @Resource
    private SendAwardMessageEvent sendAwardMessageEvent;

    @Resource
    private Map<String, IDeliveryAward> deliveryAwardMap;

    @Override
    public void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity) {
        /** build message object */
        SendAwardMessageEvent.SendAwardMessage sendAwardMessage = SendAwardMessageEvent.SendAwardMessage.builder()
                .userId(userAwardRecordEntity.getUserId())
                .awardId(userAwardRecordEntity.getAwardId())
                .awardTitle(userAwardRecordEntity.getAwardTitle())
                .orderId(userAwardRecordEntity.getOrderId())
                .awardConfig(userAwardRecordEntity.getAwardConfig())
                .build();

        BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> sendAwardMessageEventMessage = sendAwardMessageEvent.buildEventMessage(sendAwardMessage);
        /** build task object */
        TaskEntity taskEntity = TaskEntity.builder()
                .userId(userAwardRecordEntity.getUserId())
                .topic(sendAwardMessageEvent.topic())
                .messageId(sendAwardMessageEventMessage.getId())
                .message(sendAwardMessageEventMessage)
                .status(TaskStatusVO.create)
                .build();
        /** build aggregate object */
        UserAwardRecordAggregate userAwardRecordAggregate = UserAwardRecordAggregate.builder()
                .taskEntity(taskEntity)
                .userAwardRecordEntity(userAwardRecordEntity)
                .build();

        iAwardRepository.doSaveUserAwardRecord(userAwardRecordAggregate);
    }

    @Override
    public void deliveryAward(DeliveryAwardEntity deliveryAwardEntity) {
        String awardKey = iAwardRepository.queryAwardKeyByAwardId(deliveryAwardEntity.getAwardId());
        if (null == awardKey) {
            log.error("delivery award - awardKey doesn't exist, awardKey:{}", awardKey);
            return;
        }

        /** use map to get corresponding deliveryAward method for certain award */
        IDeliveryAward deliveryAward = deliveryAwardMap.get(awardKey);

        if (null == deliveryAward) {
            log.error("delivery award - delivery doesn't implement。awardKey:{}", awardKey);
            /*throw new RuntimeException("delivery award, award:" + awardKey + "corresponding delivery doesn't implement");*/
            return;
        }
        /** delivery award */
        deliveryAward.deliveryAward(deliveryAwardEntity);

    }

    @Override
    public List<UserAwardRecordEntity> queryUserAwardRecordList(UserAwardRecordEntity userAwardRecordEntity) {
        return iAwardRepository.queryUserAwardRecordList(userAwardRecordEntity);
    }
}
