package org.example.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.award.model.vo.AccountStatusVO;
import org.example.domain.point.model.aggregate.TradeAggregate;
import org.example.domain.point.model.entity.TaskEntity;
import org.example.domain.point.model.entity.UserPointAccountEntity;
import org.example.domain.point.model.entity.UserPointOrderEntity;
import org.example.domain.point.repository.IPointRepository;
import org.example.infrastructure.event.EventPublisher;
import org.example.infrastructure.persistent.dao.ITaskDao;
import org.example.infrastructure.persistent.dao.IUserPointAccountDao;
import org.example.infrastructure.persistent.dao.IUserPointOrderDao;
import org.example.infrastructure.persistent.po.Task;
import org.example.infrastructure.persistent.po.UserPointAccount;
import org.example.infrastructure.persistent.po.UserPointOrder;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@Repository
public class PointRepository implements IPointRepository {

    @Resource
    private IUserPointAccountDao iUserPointAccountDao;
    @Resource
    private IUserPointOrderDao iUserPointOrderDao;
    @Resource
    private IDBRouterStrategy idbRouterStrategy;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private ITaskDao iTaskDao;
    @Resource
    private EventPublisher eventPublisher;

    @Override
    public void doSavePayTypeUserPointOrder(TradeAggregate tradeAggregate) {
        String userId = tradeAggregate.getUserId();
        UserPointAccountEntity userPointAccountEntity = tradeAggregate.getUserPointAccountEntity();
        UserPointOrderEntity userPointOrderEntity = tradeAggregate.getUserPointOrderEntity();
        TaskEntity taskEntity = tradeAggregate.getTaskEntity();

        if (userPointAccountEntity == null || userPointOrderEntity == null) return;

        /** UserPointAccount po */
        UserPointAccount userPointAccount = new UserPointAccount();
        userPointAccount.setUserId(userId);
        userPointAccount.setTotalAmount(userPointAccountEntity.getAvailableAmount());
        userPointAccount.setAvailableAmount(userPointAccountEntity.getAvailableAmount());
        userPointAccount.setAccountStatus(AccountStatusVO.open.getCode());
        /** UserPointOrder po */
        UserPointOrder userPointOrder = new UserPointOrder();
        userPointOrder.setUserId(userId);
        userPointOrder.setOrderId(userPointOrderEntity.getOrderId());
        userPointOrder.setTradeName(userPointOrderEntity.getTradeName().getName());
        userPointOrder.setTradeType(userPointOrderEntity.getTradeType().getCode());
        userPointOrder.setTradeAmount(userPointOrderEntity.getTradeAmount());
        userPointOrder.setOutBusinessNo(userPointOrderEntity.getOutBusinessNo());

        Task task = new Task();
        task.setUserId(userId);
        task.setTopic(taskEntity.getTopic());
        task.setMessageId(taskEntity.getMessageId());
        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
        task.setStatus(taskEntity.getStatus().getCode());

        try {
            idbRouterStrategy.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    int updateAccountPointAmount = iUserPointAccountDao.updatePointAmount(userPointAccount);
                    /** update fail, do not have this user account */
                    if (0 == updateAccountPointAmount) {
                        iUserPointAccountDao.insertUserPointAccount(userPointAccount);
                    }
                    /** insert user point account */
                    iUserPointOrderDao.insertUserPointOrder(userPointOrder);
                    iTaskDao.insertTask(task);
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("update user point account error - duplicate key conflict, userId:{}, orderId:{}", userId, userPointOrder.getOrderId(),e);
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.error("update user point account fail, userId:{}, orderId:{}", userId, userPointOrder.getOrderId(),e);
                }
                return 1;
            });
        } finally {
            idbRouterStrategy.clear();
        }

        try {
            /** send MQ */
            eventPublisher.publish(task.getTopic(),task.getMessage());
            iTaskDao.updateTaskSendMessageCompleted(task);
            log.info("update user point account and record, send MQ success, userId:{}, orderId:{}, topic:{}",userId,userPointOrder.getOrderId(),task.getTopic());
        }catch (Exception e){
            log.error("update user point account and record, send MQ success, userId:{}, orderId:{}, topic:{}",userId,userPointOrder.getOrderId(),task.getTopic(),e);
            iTaskDao.updateTaskSendMessageFail(task);
        }
    }

    @Override
    public void doSaveNonPayTypeUserPointOrder(TradeAggregate tradeAggregate) {
        String userId = tradeAggregate.getUserId();
        UserPointAccountEntity userPointAccountEntity = tradeAggregate.getUserPointAccountEntity();
        UserPointOrderEntity userPointOrderEntity = tradeAggregate.getUserPointOrderEntity();

        if (userPointAccountEntity == null || userPointOrderEntity == null) return;

        /** UserPointAccount po */
        UserPointAccount userPointAccount = new UserPointAccount();
        userPointAccount.setUserId(userId);
        userPointAccount.setTotalAmount(userPointAccountEntity.getAvailableAmount());
        userPointAccount.setAvailableAmount(userPointAccountEntity.getAvailableAmount());
        userPointAccount.setAccountStatus(AccountStatusVO.open.getCode());
        /** UserPointOrder po */
        UserPointOrder userPointOrder = new UserPointOrder();
        userPointOrder.setUserId(userId);
        userPointOrder.setOrderId(userPointOrderEntity.getOrderId());
        userPointOrder.setTradeName(userPointOrderEntity.getTradeName().getName());
        userPointOrder.setTradeType(userPointOrderEntity.getTradeType().getCode());
        userPointOrder.setTradeAmount(userPointOrderEntity.getTradeAmount());
        userPointOrder.setOutBusinessNo(userPointOrderEntity.getOutBusinessNo());

        try {
            idbRouterStrategy.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    int updateAccountPointAmount = iUserPointAccountDao.updatePointAmount(userPointAccount);
                    /** update fail, do not have this user account */
                    if (0 == updateAccountPointAmount) {
                        iUserPointAccountDao.insertUserPointAccount(userPointAccount);
                    }
                    /** insert user point account */
                    iUserPointOrderDao.insertUserPointOrder(userPointOrder);
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("update user point account error - duplicate key conflict, userId:{}, orderId:{}", userId, userPointOrder.getOrderId(),e);
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.error("update user point account fail, userId:{}, orderId:{}", userId, userPointOrder.getOrderId(),e);
                }
                return 1;
            });
        } finally {
            idbRouterStrategy.clear();
        }
    }

    @Override
    public UserPointAccountEntity queryUserPointAccount(String userId) {
        UserPointAccount userPointAccount = new UserPointAccount();
        userPointAccount.setUserId(userId);
        try{
            idbRouterStrategy.doRouter(userId);
            userPointAccount = iUserPointAccountDao.queryUserPointAccount(userId);
            return UserPointAccountEntity.builder()
                    .userId(userId)
                    .availableAmount(userPointAccount == null? BigDecimal.ZERO : userPointAccount.getAvailableAmount())
                    .build();
        }finally {
            idbRouterStrategy.clear();
        }
    }
}
