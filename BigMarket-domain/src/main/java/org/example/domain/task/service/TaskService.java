package org.example.domain.task.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.task.model.entity.TaskEntity;
import org.example.domain.task.repository.ITaskRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TaskService implements ITaskService {

    @Resource
    private ITaskRepository iTaskRepository;

    @Override
    public List<TaskEntity> queryNoSendMessageTaskList() {
        return iTaskRepository.queryNoSendMessageTaskList();
    }

    @Override
    public void sendMessage(TaskEntity taskEntity) {
        iTaskRepository.sendMessage(taskEntity);
    }

    @Override
    public void updateTaskSendMessageCompleted(String userId, String messageId) {
        iTaskRepository.updateTaskSendMessageCompeted(userId, messageId);

    }

    @Override
    public void updateTaskSendMessageFail(String userId, String messageId) {
        iTaskRepository.updateTaskSendMessageFail(userId, messageId);
    }
}
