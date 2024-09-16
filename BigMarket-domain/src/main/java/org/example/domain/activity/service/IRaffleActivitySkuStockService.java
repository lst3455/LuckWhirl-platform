package org.example.domain.activity.service;

import org.example.domain.activity.model.vo.ActivitySkuStockKeyVO;

public interface IRaffleActivitySkuStockService {

    ActivitySkuStockKeyVO takeQueueValue(Long sku) throws InterruptedException;

    void clearQueueValue(Long sku);

    void updateActivitySkuStock(Long sku);

    void clearActivitySkuStock(Long sku);

}
