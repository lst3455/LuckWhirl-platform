package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.UserAwardRecord;

@Mapper
public interface IUserAwardRecordDao {
    void insertUserAwardRecord(UserAwardRecord userAwardRecord);

}
