package org.example.trigger.api;


import org.example.trigger.api.dto.RebateRequestDTO;
import org.example.trigger.api.request.Request;
import org.example.types.model.Response;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 返利服务
 * @create 2024-10-20 13:38
 */
public interface IRebateService {

    Response<Boolean> rebate(Request<RebateRequestDTO> request);

}
