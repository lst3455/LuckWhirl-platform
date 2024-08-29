package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.RaffleActivityAmount;

@Mapper
public interface IRaffleActivityAmountDao {

    RaffleActivityAmount queryRaffleActivityAmountByActivityAmountId(Long activityAmountId);
}
