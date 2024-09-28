package org.example.domain.activity.service.product;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.entity.SkuProductEntity;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.domain.activity.service.IRaffleActivitySkuProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class RaffleActivitySkuProductService implements IRaffleActivitySkuProductService {

    @Resource
    private IActivityRepository iActivityRepository;

    @Override
    public List<SkuProductEntity> querySkuProductEntityListByActivityId(Long activityId) {

        return iActivityRepository.querySkuProductEntityListByActivityId(activityId);
    }
}
