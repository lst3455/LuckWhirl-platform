package org.example.domain.strategy.service.raffle;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.domain.strategy.model.entity.RaffleAwardEntity;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;
import org.example.domain.strategy.model.entity.RuleActionEntity;
import org.example.domain.strategy.model.entity.StrategyEntity;
import org.example.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import org.example.domain.strategy.model.vo.StrategyAwardRuleModelVO;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.domain.strategy.service.IRaffleStrategy;
import org.example.domain.strategy.service.armory.IStrategyDispatch;
import org.example.domain.strategy.service.rule.factory.DefaultLogicFactory;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;

import java.util.Set;

@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    protected IStrategyRepository iStrategyRepository;

    protected IStrategyDispatch iStrategyDispatch;

    public AbstractRaffleStrategy(IStrategyRepository iStrategyRepository, IStrategyDispatch iStrategyDispatch){
        this.iStrategyDispatch = iStrategyDispatch;
        this.iStrategyRepository = iStrategyRepository;
    }

    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {

        /** verify parameters */
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        if (strategyId == null || StringUtils.isBlank(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        /** query database to get strategyEntity */
        StrategyEntity strategyEntity = iStrategyRepository.queryStrategyEntityByStrategyId(strategyId);

        /** filter before raffle */
        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionBeforeEntity = this.doCheckRaffleBeforeLogic(RaffleFactorEntity.builder().userId(userId).strategyId(strategyId).build(), strategyEntity.getRuleModels());

        if (RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleActionBeforeEntity.getCode())) {
            if (DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode().equals(ruleActionBeforeEntity.getRuleModel())) {
                /** inside blacklist, return blacklist lucky award */
                log.info("[temporary log] take over by rule filter(rule_blacklist)");
                return RaffleAwardEntity.builder()
                        .awardId(ruleActionBeforeEntity.getData().getAwardId())
                        .build();
            } else if (DefaultLogicFactory.LogicModel.RULE_WEIGHT.getCode().equals(ruleActionBeforeEntity.getRuleModel())) {
                /** outside blacklist, base on raffleTimes to raffle award */
                log.info("[temporary log] take over by rule filter(rule_weight)");
                RuleActionEntity.RaffleBeforeEntity raffleBeforeEntity = ruleActionBeforeEntity.getData();
                /*Set<Long> ruleWeightValueKey = raffleBeforeEntity.getRuleWeightValueKey();*/
                Long userRaffleTimes = raffleBeforeEntity.getUserRaffleTimes();
                Long awardId = iStrategyDispatch.getRandomAwardId(strategyId, userRaffleTimes);
                return RaffleAwardEntity.builder()
                        .awardId(awardId)
                        .build();
            }
        }

        /** outside filter, go default raffle */
        Long awardId = iStrategyDispatch.getRandomAwardId(strategyId);


        /** check award rule */
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = iStrategyRepository.queryStrategyAwardRuleModel(strategyId,awardId);

        /** filter during raffle */
        RuleActionEntity<RuleActionEntity.RaffleCentreEntity> ruleActionCentreEntity = this.doCheckRaffleCentreLogic(RaffleFactorEntity.builder()
                .userId(userId)
                .strategyId(strategyId)
                .awardId(awardId).build(), strategyAwardRuleModelVO.raffleCentreRuleModelList());

        if (RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleActionCentreEntity.getCode())){
            log.info("[temporary log] take over by rule filter(rule_lock)");
            return RaffleAwardEntity.builder()
                    .awardDesc("take over by raffleCentreRule, go to lucky award")
                    .build();
        }

        log.info("[temporary log] pass all rule filter, execute normal raffle");
        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .build();

    }

    protected abstract RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String[] ruleModels);
    protected abstract RuleActionEntity<RuleActionEntity.RaffleCentreEntity> doCheckRaffleCentreLogic(RaffleFactorEntity raffleFactorEntity, String[] ruleModels);
}
