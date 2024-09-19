package org.example.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserActivityAccountResponseDTO {

    /** total amount */
    private Integer totalAmount;
    /** total remain */
    private Integer totalRemain;
    /** day amount */
    private Integer dayAmount;
    /** day remain */
    private Integer dayRemain;
    /** month amount */
    private Integer monthAmount;
    /** month remain */
    private Integer monthRemain;

}
