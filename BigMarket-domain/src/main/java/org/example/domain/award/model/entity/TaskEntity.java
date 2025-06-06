package org.example.domain.award.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.award.adapter.event.SendAwardMessageEvent;
import org.example.domain.award.model.vo.TaskStatusVO;
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
    private BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> message;
    /** task status；create,completed,fail */
    private TaskStatusVO status;
}
