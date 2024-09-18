package org.example.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import org.example.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import org.example.domain.rebate.model.entity.TaskEntity;
import org.example.domain.rebate.model.vo.BehaviorTypeVO;
import org.example.domain.rebate.model.vo.DailyBehaviorRebateVO;
import org.example.domain.rebate.repository.IBehaviorRebateRepository;
import org.example.infrastructure.event.EventPublisher;
import org.example.infrastructure.persistent.dao.IDailyBehaviorRebateDao;
import org.example.infrastructure.persistent.dao.ITaskDao;
import org.example.infrastructure.persistent.dao.IUserBehaviorRebateOrderDao;
import org.example.infrastructure.persistent.po.DailyBehaviorRebate;
import org.example.infrastructure.persistent.po.Task;
import org.example.infrastructure.persistent.po.UserBehaviorRebateOrder;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class BehaviorRebateRepository implements IBehaviorRebateRepository {

    @Resource
    private IDailyBehaviorRebateDao iDailyBehaviorRebateDao;
    @Resource
    private IUserBehaviorRebateOrderDao iUserBehaviorRebateOrderDao;
    @Resource
    private ITaskDao iTaskDao;
    @Resource
    private IDBRouterStrategy idbRouterStrategy;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private EventPublisher eventPublisher;


    @Override
    public List<DailyBehaviorRebateVO> queryDailyBehaviorRebateConfig(BehaviorTypeVO behaviorTypeVO) {
        List<DailyBehaviorRebate> dailyBehaviorRebateList = iDailyBehaviorRebateDao.queryDailyBehaviorRebateByBehaviorType(behaviorTypeVO.getCode());
        List<DailyBehaviorRebateVO> dailyBehaviorRebateVOS = new ArrayList<>();
        for (DailyBehaviorRebate dailyBehaviorRebate : dailyBehaviorRebateList) {
            dailyBehaviorRebateVOS.add(DailyBehaviorRebateVO.builder()
                    .behaviorType(dailyBehaviorRebate.getBehaviorType())
                    .rebateDesc(dailyBehaviorRebate.getRebateDesc())
                    .rebateType(dailyBehaviorRebate.getRebateType())
                    .rebateConfig(dailyBehaviorRebate.getRebateConfig())
                    .build());
        }
        return dailyBehaviorRebateVOS;

    }

    @Override
    public void doSaveUserRebateOrder(String userId, List<BehaviorRebateAggregate> behaviorRebateAggregateList) {
        try{
            idbRouterStrategy.doRouter(userId);
            transactionTemplate.execute(status -> {
                try{
                    for (BehaviorRebateAggregate behaviorRebateAggregate : behaviorRebateAggregateList){
                        BehaviorRebateOrderEntity behaviorRebateOrderEntity = behaviorRebateAggregate.getBehaviorRebateOrderEntity();
                        /** convert to po object */
                        UserBehaviorRebateOrder userBehaviorRebateOrder = new UserBehaviorRebateOrder();
                        userBehaviorRebateOrder.setUserId(behaviorRebateOrderEntity.getUserId());
                        userBehaviorRebateOrder.setOrderId(behaviorRebateOrderEntity.getOrderId());
                        userBehaviorRebateOrder.setBehaviorType(behaviorRebateOrderEntity.getBehaviorType());
                        userBehaviorRebateOrder.setRebateDesc(behaviorRebateOrderEntity.getRebateDesc());
                        userBehaviorRebateOrder.setRebateType(behaviorRebateOrderEntity.getRebateType());
                        userBehaviorRebateOrder.setRebateConfig(behaviorRebateOrderEntity.getRebateConfig());
                        userBehaviorRebateOrder.setBizId(behaviorRebateOrderEntity.getBizId());
                        iUserBehaviorRebateOrderDao.insertUserBehaviorRebateOrder(userBehaviorRebateOrder);

                        TaskEntity taskEntity = behaviorRebateAggregate.getTaskEntity();
                        Task task = new Task();
                        task.setUserId(taskEntity.getUserId());
                        task.setTopic(taskEntity.getTopic());
                        task.setMessageId(taskEntity.getMessageId());
                        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
                        task.setStatus(taskEntity.getStatus().getCode());
                        iTaskDao.insertTask(task);
                    }
                    return 1;
                }catch (DuplicateKeyException e){
                    status.setRollbackOnly();
                    log.error("save user rebate order - unique key conflict, userId:{}",userId, e);
                    throw new AppException(ResponseCode.INDEX_DUPLICATE.getCode(),e);
                }
            });
        }finally {
            idbRouterStrategy.clear();
        }
        /** send MQ message if transaction success*/
        for (BehaviorRebateAggregate behaviorRebateAggregate : behaviorRebateAggregateList) {
            TaskEntity taskEntity = behaviorRebateAggregate.getTaskEntity();
            Task task = new Task();
            task.setUserId(taskEntity.getUserId());
            task.setMessageId(taskEntity.getMessageId());
            try {
                log.info("save user rebate order - send MQ message success userId: {} topic: {}", userId, task.getTopic());
                eventPublisher.publish(taskEntity.getTopic(), taskEntity.getMessage());
                iTaskDao.updateTaskSendMessageCompleted(task);
            } catch (Exception e) {
                log.error("save user rebate order - send MQ message fail userId: {} topic: {}", userId, task.getTopic());
                iTaskDao.updateTaskSendMessageFail(task);
            }
        }


    }
}
