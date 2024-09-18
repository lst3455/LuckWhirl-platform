package org.example.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.RaffleActivityAccount;

@Mapper
public interface IRaffleActivityAccountDao {
    int updateAccountQuota(RaffleActivityAccount raffleActivityAccount);

    void insertActivityAccount(RaffleActivityAccount raffleActivityAccount);

    @DBRouter
    RaffleActivityAccount queryActivityAccountByUserId(RaffleActivityAccount raffleActivityAccount);

    int updateActivityAccountRemain(RaffleActivityAccount raffleActivityAccount);

    void updateActivityAccountMonthRemain(RaffleActivityAccount raffleActivityAccount);

    void updateActivityAccountDayRemain(RaffleActivityAccount raffleActivityAccount);
}
