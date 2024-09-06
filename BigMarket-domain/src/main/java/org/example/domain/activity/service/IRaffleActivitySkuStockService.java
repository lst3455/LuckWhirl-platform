package org.example.domain.activity.service;

import org.example.domain.activity.model.vo.ActivitySkuStockKeyVO;

public interface IRaffleActivitySkuStockService {

    ActivitySkuStockKeyVO takeQueueValue() throws InterruptedException;

    void clearQueueValue();

    void updateActivitySkuStock(Long sku);

    void clearActivitySkuStock(Long sku);
}
