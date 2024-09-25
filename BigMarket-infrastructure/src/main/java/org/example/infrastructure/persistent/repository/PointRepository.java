package org.example.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.point.model.aggregate.TradeAggregate;
import org.example.domain.point.model.entity.UserPointAccountEntity;
import org.example.domain.point.model.entity.UserPointOrderEntity;
import org.example.domain.point.repository.IPointRepository;
import org.example.infrastructure.persistent.dao.IUserPointAccountDao;
import org.example.infrastructure.persistent.dao.IUserPointOrderDao;
import org.example.infrastructure.persistent.po.UserPointAccount;
import org.example.infrastructure.persistent.po.UserPointOrder;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

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


    @Override
    public String createUserPointOrder() {
        return "";
    }

    @Override
    public void doSaveUserCreditTradeOrder(TradeAggregate tradeAggregate) {
        String userId = tradeAggregate.getUserId();
        UserPointAccountEntity userPointAccountEntity = tradeAggregate.getUserPointAccountEntity();
        UserPointOrderEntity userPointOrderEntity = tradeAggregate.getUserPointOrderEntity();

        /** UserPointAccount po */
        UserPointAccount userPointAccount = new UserPointAccount();
        userPointAccount.setUserId(userId);
        userPointAccount.setTotalAmount(userPointAccountEntity.getUpdatedAmount());
        userPointAccount.setAvailableAmount(userPointAccountEntity.getUpdatedAmount());
        /** UserPointOrder po */
        UserPointOrder userPointOrder = new UserPointOrder();
        userPointOrder.setUserId(userPointOrderEntity.getUserId());
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
}
