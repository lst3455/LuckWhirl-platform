package org.example.domain.activity.service.quota.rule.factory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.domain.activity.service.quota.rule.IActionChain;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DefaultActivityChainFactory {

    private final IActionChain iActionChain;

    public DefaultActivityChainFactory(Map<String, IActionChain> actionChainMap) {
        this.iActionChain = actionChainMap.get(ActionModel.activity_basic_action.code);
        this.iActionChain.appendNext(actionChainMap.get(ActionModel.activity_sku_stock_action.code));
    }

    public IActionChain openActionChain(){return this.iActionChain;}

    @Getter
    @AllArgsConstructor
    public enum ActionModel{
        activity_basic_action("activity_basic_action","activity date and status validation"),
        activity_sku_stock_action("activity_sku_stock_action","activity sku stock validation"),
        ;

        private final String code;
        private final String info;
    }
}
