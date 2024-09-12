package org.example.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.RaffleActivityAccountDay;

@Mapper
public interface IRaffleActivityAccountDayDao {

    @DBRouter
    RaffleActivityAccountDay queryActivityAccountDay(RaffleActivityAccountDay raffleActivityAccountDay);

    int updateActivityAccountDayRemain(RaffleActivityAccountDay build);

    void insertActivityAccountDay(RaffleActivityAccountDay build);
}
