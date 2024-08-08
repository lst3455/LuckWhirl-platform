package org.example.domain.strategy.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.domain.strategy.model.entity.RaffleAwardEntity;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;
import org.example.domain.strategy.model.entity.RuleActionEntity;
import org.example.domain.strategy.model.entity.StrategyEntity;
import org.example.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import org.example.domain.strategy.model.vo.StrategyAwardRuleModelVO;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.domain.strategy.service.armory.IStrategyDispatch;
import org.example.domain.strategy.service.rule.chain.ILogicChain;
import org.example.domain.strategy.service.rule.chain.factory.DefaultLogicChainFactory;
import org.example.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import org.example.domain.strategy.service.rule.tree.factory.DefaultLogicTreeFactory;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;

@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    protected IStrategyRepository iStrategyRepository;

    protected IStrategyDispatch iStrategyDispatch;

    protected DefaultLogicChainFactory defaultLogicChainFactory;

    protected DefaultLogicTreeFactory defaultLogicTreeFactory;

    protected DefaultLogicFactory defaultLogicFactory;

    public AbstractRaffleStrategy(IStrategyRepository iStrategyRepository, IStrategyDispatch iStrategyDispatch, DefaultLogicChainFactory defaultLogicChainFactory, DefaultLogicTreeFactory defaultLogicTreeFactory) {
        this.iStrategyRepository = iStrategyRepository;
        this.iStrategyDispatch = iStrategyDispatch;
        this.defaultLogicChainFactory = defaultLogicChainFactory;
        this.defaultLogicTreeFactory = defaultLogicTreeFactory;
    }

    /** performRaffleLogicFilter has been deprecated */
    @Override
    public RaffleAwardEntity performRaffleLogicFilter(RaffleFactorEntity raffleFactorEntity) {

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
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = iStrategyRepository.queryStrategyAwardRuleModelVO(strategyId,awardId);



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

    /** performRaffleLogicChain has been deprecated */
    @Override
    public RaffleAwardEntity performRaffleLogicChain(RaffleFactorEntity raffleFactorEntity) {
        /** verify parameters */
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        if (strategyId == null || StringUtils.isBlank(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        /** open rule chain filter */
        ILogicChain iLogicChain = defaultLogicChainFactory.openLogicChain(strategyId);
        /** pass all chain filter and return awardId*/
        Long awardId = iLogicChain.logic(userId,strategyId);


        /** check award rule */
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = iStrategyRepository.queryStrategyAwardRuleModelVO(strategyId,awardId);

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

    @Override
    public RaffleAwardEntity performRaffleLogicChainWithRuleTree(RaffleFactorEntity raffleFactorEntity){
        /** verify parameters */
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        if (strategyId == null || StringUtils.isBlank(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        /** before raffle filter, go through rule chain */
        DefaultLogicChainFactory.StrategyAwardVO strategyChainAwardVO = raffleLogicChain(userId, strategyId);
        if(!DefaultLogicChainFactory.LogicModel.RULE_DEFAULT.getCode().equals(strategyChainAwardVO.getRuleModel())){
            /*log.info("take over by raffle strategy[rule-chain(blacklist,weight)] useId:{} strategyId:{} awardId:{} ruleModel:{}",userId,strategyId,strategyChainAwardVO.getAwardId(),strategyChainAwardVO.getLogicModel());*/
            return RaffleAwardEntity.builder()
                    .awardId(strategyChainAwardVO.getAwardId())
                    .awardConfig(strategyChainAwardVO.getRuleModel())
                    .build();
        }
        /*log.info("pass raffle strategy[rule-chain(blacklist,weight)] useId:{} strategyId:{} awardId:{}",userId,strategyId,strategyChainAwardVO.getAwardId());*/
        /** centre raffle filter, go through rule tree */
        DefaultLogicTreeFactory.StrategyAwardVO strategyTreeAwardVO = raffleLogicTree(userId,strategyId,strategyChainAwardVO.getAwardId());
        /*log.info("take over by raffle strategy[rule-tree(lock,lucky)] useId:{} strategyId:{} awardId:{} ruleModel:{}",userId,strategyId,strategyTreeAwardVO.getAwardId(),strategyTreeAwardVO.getAwardRuleValue());*/

        return RaffleAwardEntity.builder()
                .awardId(strategyTreeAwardVO.getAwardId())
                .awardConfig(strategyTreeAwardVO.getRuleModel())
                .build();
    }

    public abstract DefaultLogicChainFactory.StrategyAwardVO raffleLogicChain(String userId,Long StrategyId);
    public abstract DefaultLogicTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long StrategyId, Long awardId);

    /** doCheckRaffleBeforeLogic has been deprecated */
    protected abstract RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String[] ruleModels);
    /** doCheckRaffleCentreLogic has been deprecated */
    protected abstract RuleActionEntity<RuleActionEntity.RaffleCentreEntity> doCheckRaffleCentreLogic(RaffleFactorEntity raffleFactorEntity, String[] ruleModels);

}
