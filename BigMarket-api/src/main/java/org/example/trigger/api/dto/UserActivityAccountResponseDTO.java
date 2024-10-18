package org.example.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserActivityAccountResponseDTO implements Serializable {

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
