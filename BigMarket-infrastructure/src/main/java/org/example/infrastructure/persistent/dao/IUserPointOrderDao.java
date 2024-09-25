package org.example.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.UserPointOrder;

@Mapper
@DBRouterStrategy(splitTable = true)
public interface IUserPointOrderDao {

    void insertUserPointOrder(UserPointOrder userPointOrder);
}
