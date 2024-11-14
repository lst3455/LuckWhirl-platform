package org.example.domain.award.service.delivery.impl;


import org.apache.commons.lang3.StringUtils;
import org.example.domain.award.adapter.port.IAwardPort;
import org.example.domain.award.adapter.repository.IAwardRepository;
import org.example.domain.award.model.entity.DeliveryAwardEntity;
import org.example.domain.award.service.delivery.IDeliveryAward;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("chatbot_use_count")
public class OpenAIAccountAdjustQuotaAward implements IDeliveryAward {

    @Resource
    private IAwardPort port;
    @Resource
    private IAwardRepository repository;

    @Override
    public void deliveryAward(DeliveryAwardEntity distributeAwardEntity) throws Exception {
        // 奖品ID
        Long awardId = distributeAwardEntity.getAwardId();
        // 查询奖品ID 「优先走透传的随机积分奖品配置」
        String awardConfig = distributeAwardEntity.getAwardConfig();
        if (StringUtils.isBlank(awardConfig)) {
            awardConfig = repository.queryAwardConfigByAwardId(awardId);
        }
        // 发奖接口
        port.adjustAmount(distributeAwardEntity.getUserId(), Integer.valueOf(awardConfig));
    }

}
