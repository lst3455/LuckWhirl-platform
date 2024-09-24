package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.UserPointAccount;

@Mapper
public interface IUserPointAccountDao {

    int updatePointAmount(UserPointAccount userPointAccount);

    void insertUserPointAccount(UserPointAccount userPointAccount);

}
