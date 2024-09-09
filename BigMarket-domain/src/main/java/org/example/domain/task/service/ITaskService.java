package org.example.domain.task.service;

import org.example.domain.task.model.entity.TaskEntity;

import java.util.List;

public interface ITaskService {
    List<TaskEntity> queryNoSendMessageTaskList();

    void sendMessage(TaskEntity taskEntity);

    void updateTaskSendMessageCompleted(String userId, String messageId);

    void updateTaskSendMessageFail(String userId, String messageId);

}
