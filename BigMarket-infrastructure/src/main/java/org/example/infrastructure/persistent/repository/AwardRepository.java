package org.example.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.award.model.aggregate.DeliveryAwardAggregate;
import org.example.domain.award.model.aggregate.UserAwardRecordAggregate;
import org.example.domain.award.model.entity.TaskEntity;
import org.example.domain.award.model.entity.UserAwardRecordEntity;
import org.example.domain.award.model.entity.UserPointAwardEntity;
import org.example.domain.award.model.vo.AccountStatusVO;
import org.example.domain.award.repository.IAwardRepository;
import org.example.infrastructure.event.EventPublisher;
import org.example.infrastructure.persistent.dao.*;
import org.example.infrastructure.persistent.po.Task;
import org.example.infrastructure.persistent.po.UserAwardRecord;
import org.example.infrastructure.persistent.po.UserPointAccount;
import org.example.infrastructure.persistent.po.UserRaffleOrder;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.springframework.dao.DuplicateKeyException;
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
    @Resource
    private IAwardDao iAwardDao;
    @Resource
    private IUserPointAccountDao iUserPointAccountDao;

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

    @Override
    public String queryAwardConfigByAwardId(Long awardId) {
        return iAwardDao.queryAwardConfigByAwardId(awardId);
    }

    @Override
    public void doSaveDeliveryAwardAggregate(DeliveryAwardAggregate deliveryAwardAggregate) {
        String userId = deliveryAwardAggregate.getUserId();
        UserPointAwardEntity userCreditAwardEntity = deliveryAwardAggregate.getUserPointAwardEntity();
        UserAwardRecordEntity userAwardRecordEntity = deliveryAwardAggregate.getUserAwardRecordEntity();
        /** build award record object */
        UserAwardRecord userAwardRecord = new UserAwardRecord();
        userAwardRecord.setUserId(userId);
        userAwardRecord.setOrderId(userAwardRecordEntity.getOrderId());
        userAwardRecord.setAwardStatus(userAwardRecordEntity.getAwardStatus().getCode());
        /** build award record object */
        UserPointAccount userPointAccount = new UserPointAccount();
        userPointAccount.setUserId(userCreditAwardEntity.getUserId());
        userPointAccount.setTotalAmount(userCreditAwardEntity.getPointAmount());
        userPointAccount.setAvailableAmount(userCreditAwardEntity.getPointAmount());
        userPointAccount.setAccountStatus(AccountStatusVO.open.getCode());

        try {
            idbRouterStrategy.doRouter(deliveryAwardAggregate.getUserId());
            transactionTemplate.execute(status -> {
                try {
                    int updateAccountPointAmount = iUserPointAccountDao.updatePointAmount(userPointAccount);

                    if (0 == updateAccountPointAmount) {
                        iUserPointAccountDao.insertUserPointAccount(userPointAccount);
                    }

                    int updateAwardCount = iUserAwardRecordDao.updateAwardRecordCompletedStatus(userAwardRecord);
                    if (0 == updateAwardCount) {
                        log.warn("更新中奖记录，重复更新拦截 userId:{} deliveryAwardAggregate:{}", userId, JSON.toJSONString(deliveryAwardAggregate));
                        status.setRollbackOnly();
                    }
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("save user award record - unique key conflict, userId: {} ", userId, e);
                    throw new AppException(ResponseCode.INDEX_DUPLICATE.getCode(), e);
                }
            });
        } finally {
            idbRouterStrategy.clear();
        }


    }

    @Override
    public String queryAwardKeyByAwardId(Long awardId) {
        return iAwardDao.queryAwardKeyByAwardId(awardId);
    }
}
