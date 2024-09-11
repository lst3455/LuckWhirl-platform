package org.example.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.award.model.aggregate.UserAwardRecordAggregate;
import org.example.domain.award.model.entity.TaskEntity;
import org.example.domain.award.model.entity.UserAwardRecordEntity;
import org.example.domain.award.repository.IAwardRepository;
import org.example.infrastructure.event.EventPublisher;
import org.example.infrastructure.persistent.dao.ITaskDao;
import org.example.infrastructure.persistent.dao.IUserAwardRecordDao;
import org.example.infrastructure.persistent.dao.IUserRaffleOrderDao;
import org.example.infrastructure.persistent.po.Task;
import org.example.infrastructure.persistent.po.UserAwardRecord;
import org.example.infrastructure.persistent.po.UserRaffleOrder;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

@Slf4j
@Repository
public class AwardRepository implements IAwardRepository {
    @Resource
    private ITaskDao iTaskDao;
    @Resource
    private IUserAwardRecordDao iUserAwardRecordDao;
    @Resource
    private IDBRouterStrategy idbRouterStrategy;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private IUserRaffleOrderDao iUserRaffleOrderDao;

    @Override
    public void doSaveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate) {
        /** get data */
        UserAwardRecordEntity userAwardRecordEntity = userAwardRecordAggregate.getUserAwardRecordEntity();
        TaskEntity taskEntity = userAwardRecordAggregate.getTaskEntity();
        String userId = userAwardRecordEntity.getUserId();
        Long activityId = userAwardRecordEntity.getActivityId();
        Long awardId = userAwardRecordEntity.getAwardId();
        /** convert to po object */
        UserAwardRecord userAwardRecord = new UserAwardRecord();
        userAwardRecord.setUserId(userAwardRecordEntity.getUserId());
        userAwardRecord.setActivityId(userAwardRecordEntity.getActivityId());
        userAwardRecord.setStrategyId(userAwardRecordEntity.getStrategyId());
        userAwardRecord.setOrderId(userAwardRecordEntity.getOrderId());
        userAwardRecord.setAwardId(userAwardRecordEntity.getAwardId());
        userAwardRecord.setAwardTitle(userAwardRecordEntity.getAwardTitle());
        userAwardRecord.setAwardTime(userAwardRecordEntity.getAwardTime());
        userAwardRecord.setAwardStatus(userAwardRecordEntity.getAwardStatus().getCode());
        /** convert to po object */
        Task task = new Task();
        task.setUserId(taskEntity.getUserId());
        task.setTopic(taskEntity.getTopic());
        task.setMessageId(taskEntity.getMessageId());
        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
        task.setStatus(taskEntity.getStatus().getCode());

        UserRaffleOrder userRaffleOrder = new UserRaffleOrder();
        userRaffleOrder.setUserId(userAwardRecordEntity.getUserId());
        userRaffleOrder.setOrderId(userAwardRecordEntity.getOrderId());
        /** start the transaction */
        try {
            idbRouterStrategy.doRouter(userId);
            transactionTemplate.execute(status -> {
                try{
                    iTaskDao.insertTask(task);
                    iUserAwardRecordDao.insertUserAwardRecord(userAwardRecord);

                    int amount = iUserRaffleOrderDao.updateUserRaffleOrderStatusUsed(userRaffleOrder);
                    if (amount != 1){
                        status.setRollbackOnly();
                        log.error("save user award record - update userRaffleOrder fail, userId: {}, activityId: {}, awardId: {}", userId, activityId, awardId);
                        throw new AppException(ResponseCode.ACTIVITY_RAFFLE_ORDER_ERROR.getCode(),ResponseCode.ACTIVITY_RAFFLE_ORDER_ERROR.getInfo());
                    }
                    return 1;
                }catch (DuplicateKeyException e){
                    status.setRollbackOnly();
                    log.error("save user award record - unique key conflict, userId: {}, activityId: {}, awardId: {}", userId, activityId, awardId, e);
                    throw new AppException(ResponseCode.INDEX_DUPLICATE.getCode(),e);
                }
            });
        } finally {
            idbRouterStrategy.clear();
        }
        /** send the MQ message */
        try{
            eventPublisher.publish(task.getTopic(),task.getMessage());
            iTaskDao.updateTaskSendMessageCompleted(task);
        }catch (Exception e){
            log.error("save user award record - send MQ message fail userId: {} topic: {}", userId, task.getTopic());
            iTaskDao.updateTaskSendMessageFail(task);
        }

    }
}
