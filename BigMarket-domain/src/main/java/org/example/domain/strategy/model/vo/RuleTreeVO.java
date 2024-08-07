package org.example.domain.strategy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleTreeVO {
    /** rule tree id */
    private String treeId;
    /** rule tree name */
    private String treeName;
    /** rule tree describe */
    private String treeDesc;
    /** rule tree root */
    private String ruleTreeRootNode;
    /** rule tree node */
    private Map<String, RuleTreeNodeVO> treeNodeMap;

}
