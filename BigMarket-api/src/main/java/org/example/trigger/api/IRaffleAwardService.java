package org.example.trigger.api;

import org.example.trigger.api.dto.UserAwardRecordRequestDTO;
import org.example.trigger.api.dto.UserAwardRecordResponseDTO;
import org.example.types.model.Response;

import java.util.List;

public interface IRaffleAwardService {
    Response<List<UserAwardRecordResponseDTO>> queryUserAwardRecordList(UserAwardRecordRequestDTO userAwardRecordRequestDTO);

}
