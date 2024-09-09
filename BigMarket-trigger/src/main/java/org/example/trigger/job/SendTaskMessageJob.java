package org.example.trigger.job;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.task.model.entity.TaskEntity;
import org.example.domain.task.service.ITaskService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component()
public class SendTaskMessageJob {

    @Resource
    private ITaskService iTaskService;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private IDBRouterStrategy idbRouterStrategy;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() {
        try {
            int dbCount = idbRouterStrategy.dbCount();
            for (int i = 1; i < dbCount; i++) {
                int finalI = i;
                threadPoolExecutor.execute(() -> {
                    try {
                        /** check all database */
                        idbRouterStrategy.setDBKey(finalI);
                        idbRouterStrategy.setTBKey(0);
                        List<TaskEntity> taskEntityList = iTaskService.queryNoSendMessageTaskList();
                        if (taskEntityList.isEmpty()) return;
                        /** send MQ message*/
                        for (TaskEntity taskEntity : taskEntityList) {
                            /**
                             * Enable thread sending to improve sending efficiency.
                             * The configured thread pool policy is CallerRunsPolicy.
                             * There are 4 strategies in the ThreadPoolConfig configuration.
                             */
                            threadPoolExecutor.execute(() -> {
                                try {
                                    iTaskService.sendMessage(taskEntity);
                                    iTaskService.updateTaskSendMessageCompleted(taskEntity.getUserId(), taskEntity.getMessageId());
                                    log.info("scheduled task，send task message success userId: {} topic: {}", taskEntity.getUserId(), taskEntity.getTopic());
                                } catch (Exception e) {
                                    log.error("scheduled task，send task message fail in taskEntityList userId: {} topic: {}", taskEntity.getUserId(), taskEntity.getTopic());
                                    iTaskService.updateTaskSendMessageFail(taskEntity.getUserId(), taskEntity.getMessageId());
                                }
                            });
                        }
                    }finally {
                        idbRouterStrategy.clear();
                    }
                });
            }
        } catch (Exception e) {
            log.error("scheduled task，send task message fail", e);
        }
    }
}
