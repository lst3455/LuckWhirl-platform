package org.example.domain.task.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String message;
}
