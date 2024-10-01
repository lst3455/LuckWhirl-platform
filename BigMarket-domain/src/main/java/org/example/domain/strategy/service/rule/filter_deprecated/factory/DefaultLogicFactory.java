package org.example.domain.strategy.service.rule.filter_deprecated.factory;

import com.alibaba.fastjson2.util.AnnotationUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.RuleActionEntity;
import org.example.domain.strategy.service.annotation.LogicStrategy;
import org.example.domain.strategy.service.rule.filter_deprecated.ILogicFilter;
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

        RULE_WEIGHT("rule_weight","[rule before raffle] depend on the raffleTimes to return available key","before"),
        RULE_BLACKLIST("rule_blacklist","[rule before raffle] if userId inside blacklist, return blacklist lucky award","before"),
        RULE_LOCK("rule_lock","[rule during raffle] depend on the raffleTimes to return available award","centre"),
        RULE_LUCKY("rule_lucky","[rule before raffle] award remain is none, return the lucky award","after"),
        RULE_RANDOM("rule_random","[rule before raffle] normal random award","after")
        ;

        private final String code;
        private final String info;
        private final String type;

        public static boolean isCenter(String code){
            return "centre".equals(LogicModel.valueOf(code.toUpperCase()).type);
        }

    }

}
