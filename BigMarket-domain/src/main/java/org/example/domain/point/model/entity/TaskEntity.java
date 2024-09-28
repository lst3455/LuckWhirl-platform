package org.example.domain.point.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.point.event.SendPointMessageEvent;
import org.example.domain.point.model.vo.TaskStatusVO;
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
    private BaseEvent.EventMessage<SendPointMessageEvent.SendPointMessage> message;
    /** task status；create,completed,fail */
    private TaskStatusVO status;
}
