package org.example.domain.strategy.service.rule.chain.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.RuleActionEntity;
import org.example.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.domain.strategy.service.armory.IStrategyDispatch;
import org.example.domain.strategy.service.rule.chain.AbstractLogicChain;
import org.example.domain.strategy.service.rule.chain.factory.DefaultLogicChainFactory;
import org.example.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import org.example.types.common.Constants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component("rule_blacklist")
public class BlackListLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyRepository iStrategyRepository;

    /** logic has been deprecated */
    @Override
    public Long logic(String userId, Long strategyId) {
        log.info("raffle rule chain start - blacklist userId: {} strategyId: {} ruleModel: {}",userId,strategyId,ruleModel());
        String ruleValue = iStrategyRepository.queryStrategyRuleValue(strategyId,ruleModel());
        String[] splitRuleValue = ruleValue.split(Constants.SPLIT_COLON);
        Long awardId = Long.parseLong(splitRuleValue[0]);
        /** data sample => 100:user001,user002,user003 */

        String[] userBlackListIds = splitRuleValue[1].split(Constants.SPLIT_COMMA);
        for (String userBlackListId : userBlackListIds) {
            if (userId.equals(userBlackListId)) {
                /** take over by rule chain - blacklist */
                log.info("raffle rule chain take over - blacklist userId: {} strategyId: {} ruleModel: {} awardId: {}",userId,strategyId,ruleModel(),awardId);
                return awardId;
            }
        }
        /** pass rule chain - blacklist, go to next chain node */
        log.info("raffle rule chain pass - blacklist userId: {} strategyId: {} ruleModel: {}",userId,strategyId,ruleModel());
        return next().logic(userId,strategyId);
    }

    @Override
    public DefaultLogicChainFactory.StrategyAwardVO treeVersionLogic(String userId, Long strategyId) {
        log.info("raffle rule chain start - blacklist userId: {} strategyId: {} ruleModel: {}",userId,strategyId,ruleModel());
        String ruleValue = iStrategyRepository.queryStrategyRuleValue(strategyId,ruleModel());
        String[] splitRuleValue = ruleValue.split(Constants.SPLIT_COLON);
        Long awardId = Long.parseLong(splitRuleValue[0]);
        /** data sample => 100:user001,user002,user003 */

        String[] userBlackListIds = splitRuleValue[1].split(Constants.SPLIT_COMMA);
        for (String userBlackListId : userBlackListIds) {
            if (userId.equals(userBlackListId)) {
                /** take over by rule chain - blacklist */
                log.info("raffle rule chain take over - blacklist userId: {} strategyId: {} ruleModel: {} awardId: {}",userId,strategyId,ruleModel(),awardId);
                return DefaultLogicChainFactory.StrategyAwardVO.builder()
                        .awardId(awardId)
                        .ruleModel(ruleModel())
                        .build();
            }
        }
        /** pass rule chain - blacklist, go to next chain node */
        log.info("raffle rule chain pass - blacklist userId: {} strategyId: {} ruleModel: {}",userId,strategyId,ruleModel());
        return next().treeVersionLogic(userId,strategyId);
    }


    @Override
    protected String ruleModel() {
        return DefaultLogicChainFactory.LogicModel.RULE_BLACKLIST.getCode();
    }
}
