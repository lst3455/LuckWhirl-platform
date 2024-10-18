package org.example.trigger.api;

import org.example.types.model.Response;

public interface IDCCService {

    Response<Boolean> updateConfig(String key, String value);

}
