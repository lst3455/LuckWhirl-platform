package org.example.domain.task.repository;

import org.example.domain.task.model.entity.TaskEntity;

import java.util.List;

public interface ITaskRepository {

    List<TaskEntity> queryNoSendMessageTaskList();

    void sendMessage(TaskEntity taskEntity);

    void updateTaskSendMessageCompeted(String userId, String messageId);

    void updateTaskSendMessageFail(String userId, String messageId);
}
