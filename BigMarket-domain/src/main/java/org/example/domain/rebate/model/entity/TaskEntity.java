package org.example.domain.rebate.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.rebate.event.SendRebateMessageEvent;
import org.example.domain.rebate.model.vo.TaskStatusVO;
import org.example.types.event.BaseEvent;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {
    /** user id */
    private String userId;
    /** message topic */
    private String topic;
    /** message id */
    private String messageId;
    /** message */
    private BaseEvent.EventMessage<SendRebateMessageEvent.SendRebateMessage> message;
    /** task status；create,completed,fail */
    private TaskStatusVO status;
}
