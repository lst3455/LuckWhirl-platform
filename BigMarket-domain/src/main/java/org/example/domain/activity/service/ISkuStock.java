package org.example.domain.activity.service;

import org.example.domain.activity.model.vo.ActivitySkuStockKeyVO;

public interface ISkuStock {

    ActivitySkuStockKeyVO takeQueueValue() throws InterruptedException;

    void clearQueueValue();

    void updateActivitySkuStock(Long sku);

    void clearActivitySkuStock(Long sku);
}
