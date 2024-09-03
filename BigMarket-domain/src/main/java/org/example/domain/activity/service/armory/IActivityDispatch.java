package org.example.domain.activity.service.armory;

import java.util.Date;

public interface IActivityDispatch {

    boolean subtractActivitySkuStock(Long sku, Date endDateTime);
}
