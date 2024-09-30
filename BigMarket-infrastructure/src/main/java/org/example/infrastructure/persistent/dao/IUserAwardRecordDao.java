package org.example.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.UserAwardRecord;

import java.util.List;

@Mapper
@DBRouterStrategy(splitTable = true)
public interface IUserAwardRecordDao {

    void insertUserAwardRecord(UserAwardRecord userAwardRecord);

    int updateAwardRecordCompletedStatus(UserAwardRecord userAwardRecord);

    @DBRouter
    List<UserAwardRecord> queryUserAwardRecordList(UserAwardRecord userAwardRecord);
}
