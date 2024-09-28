package org.example.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.UserPointAccount;

@Mapper
public interface IUserPointAccountDao {

    int updatePointAmount(UserPointAccount userPointAccount);

    void insertUserPointAccount(UserPointAccount userPointAccount);

    @DBRouter
    UserPointAccount queryUserPointAccount(String userId);
}
