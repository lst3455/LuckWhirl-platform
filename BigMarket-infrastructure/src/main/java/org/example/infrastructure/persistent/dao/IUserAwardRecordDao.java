package org.example.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.UserAwardRecord;

@Mapper
@DBRouterStrategy(splitTable = true)
public interface IUserAwardRecordDao {

    void insertUserAwardRecord(UserAwardRecord userAwardRecord);

    int updateAwardRecordCompletedStatus(UserAwardRecord userAwardRecord);
}
