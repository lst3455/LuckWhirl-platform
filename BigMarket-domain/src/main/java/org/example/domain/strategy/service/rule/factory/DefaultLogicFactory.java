package org.example.domain.strategy.service.rule.factory;

import com.alibaba.fastjson2.util.AnnotationUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.RuleActionEntity;
import org.example.domain.strategy.service.annotation.LogicStrategy;
import org.example.domain.strategy.service.rule.ILogicFilter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class DefaultLogicFactory {

    public Map<String, ILogicFilter<?>> logicFilterMap = new ConcurrentHashMap<>();

    public DefaultLogicFactory(List<ILogicFilter<?>> logicFilters) {
        logicFilters.forEach(logic -> {
            LogicStrategy strategy = AnnotationUtils.findAnnotation(logic.getClass(), LogicStrategy.class);
            if (null != strategy) {
                logicFilterMap.put(strategy.logicMode().getCode(), logic);
            }
        });
    }

    public <T extends RuleActionEntity.RaffleEntity> Map<String, ILogicFilter<T>> openLogicFilter() {
        return (Map<String, ILogicFilter<T>>) (Map<?, ?>) logicFilterMap;
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {

        RULE_WEIGHT("rule_weight","[rule before raffle] depend on the raffleTimes to return available key"),
        RULE_BLACKLIST("rule_blacklist","[rule before raffle] if userId inside blacklist, return blacklist lucky award"),

        ;

        private final String code;
        private final String info;

    }

}
