package org.example.domain.strategy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleTreeNodeVO {
    /** rule tree id */
    private String treeId;
    /** rule tree key */
    private String ruleKey;
    /** rule tree describe */
    private String ruleDesc;
    /** rule tree value */
    private String ruleValue;

    /** rule tree line (between two node) */
    private List<RuleTreeNodeLineVO> treeNodeLineVOList;

}
