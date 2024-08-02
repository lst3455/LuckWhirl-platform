package org.example.domain.strategy.service.rule.chain.factory;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.example.domain.strategy.model.entity.StrategyEntity;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.domain.strategy.service.armory.IStrategyDispatch;
import org.example.domain.strategy.service.rule.chain.ILogicChain;
import org.example.domain.strategy.service.rule.filter.ILogicFilter;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DefaultLogicChainFactory {

    private final Map<String, ILogicChain> logicChainMap;

    private final IStrategyRepository iStrategyRepository;

    public DefaultLogicChainFactory(Map<String, ILogicChain> logicChainMap, IStrategyRepository iStrategyRepository) {
        this.logicChainMap = logicChainMap;
        this.iStrategyRepository = iStrategyRepository;
    }

    public ILogicChain openLogicChain(Long strategyId){
        /** query database to get strategyEntity */
        StrategyEntity strategyEntity = iStrategyRepository.queryStrategyEntityByStrategyId(strategyId);
        String[] ruleModels = strategyEntity.getRuleModels();
        if (ruleModels.length == 0) return logicChainMap.get("default");

        /** get first chain node */
        ILogicChain iLogicChain = logicChainMap.get(ruleModels[0]);
        /** save first chain node as a pointer*/
        ILogicChain currentChain = iLogicChain;
        for (int i = 1; i < ruleModels.length; i++) {
            /** get next chain node */
            ILogicChain nextChain = logicChainMap.get(ruleModels[i]);
            /** update chain node pointer to next*/
            currentChain = currentChain.appendNext(nextChain);
        }

        /** finally add the default chain node */
        currentChain.appendNext(logicChainMap.get("default"));
        /** return first chain node */
        return iLogicChain;
    }
}
