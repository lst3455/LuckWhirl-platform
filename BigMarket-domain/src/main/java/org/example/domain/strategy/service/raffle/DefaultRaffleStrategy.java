package org.example.domain.strategy.service.raffle;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;
import org.example.domain.strategy.model.entity.RuleActionEntity;
import org.example.domain.strategy.model.entity.RuleMatterEntity;
import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.model.vo.*;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.domain.strategy.service.AbstractRaffleStrategy;
import org.example.domain.strategy.service.IRaffleAward;
import org.example.domain.strategy.service.IRaffleAwardRule;
import org.example.domain.strategy.service.IRaffleStock;
import org.example.domain.strategy.service.armory.IStrategyDispatch;
import org.example.domain.strategy.service.rule.chain.ILogicChain;
import org.example.domain.strategy.service.rule.chain.factory.DefaultLogicChainFactory;
import org.example.domain.strategy.service.rule.filter_deprecated.ILogicFilter;
import org.example.domain.strategy.service.rule.filter_deprecated.factory.DefaultLogicFactory;
import org.example.domain.strategy.service.rule.tree.factory.DefaultLogicTreeFactory;
import org.example.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy implements IRaffleAward, IRaffleStock, IRaffleAwardRule {


    public DefaultRaffleStrategy(IStrategyRepository iStrategyRepository, IStrategyDispatch iStrategyDispatch, DefaultLogicChainFactory defaultLogicChainFactory, DefaultLogicTreeFactory defaultLogicTreeFactory) {
        super(iStrategyRepository, iStrategyDispatch, defaultLogicChainFactory, defaultLogicTreeFactory);
    }


    @Override
    public DefaultLogicChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId) {
        ILogicChain iLogicChain = defaultLogicChainFactory.openLogicChain(strategyId);
        return iLogicChain.treeVersionLogic(userId,strategyId,null);
    }

    @Override
    public DefaultLogicTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Long awardId) {
        return raffleLogicTree(userId,strategyId,awardId,null);
    }

    @Override
    public DefaultLogicTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Long awardId, Date endDateTime) {
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = iStrategyRepository.queryStrategyAwardRuleModelVO(strategyId, awardId);
        if (strategyAwardRuleModelVO == null) {
            return DefaultLogicTreeFactory.StrategyAwardVO.builder()
                    .awardId(awardId)
                    .build();
        }
        RuleTreeVO ruleTreeVO = iStrategyRepository.queryRuleTreeByTreeId(strategyAwardRuleModelVO.getRuleModels());
        if (ruleTreeVO == null) {
            throw new RuntimeException("can't find ruleTreeVO base on the provide ruleModel, please check database strategy_award and rule_tree, wrong tree_id: " + strategyAwardRuleModelVO.getRuleModels());
        }
        IDecisionTreeEngine iDecisionTreeEngine = defaultLogicTreeFactory.openLogicTree(ruleTreeVO);
        return iDecisionTreeEngine.process(userId,strategyId,awardId,endDateTime);
    }


    /** doCheckRaffleBeforeLogic has been deprecated */
    @Override
    protected RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String[] ruleModels) {
        if (ruleModels == null || ruleModels.length == 0) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        Map<String, ILogicFilter<RuleActionEntity.RaffleBeforeEntity>> logicFilterGroup = defaultLogicFactory.openLogicFilter();

        /** check blacklist configuration */
        String ruleBackList = Arrays.stream(ruleModels)
                .filter(str -> str.contains(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .findFirst()
                .orElse(null);

        /** filter user inside blacklist */
        if (StringUtils.isNotBlank(ruleBackList)) {
            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> logicFilter = logicFilterGroup.get(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode());
            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
            ruleMatterEntity.setAwardId(ruleMatterEntity.getAwardId());
            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
            ruleMatterEntity.setRuleModel(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode());
            RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = logicFilter.filter(ruleMatterEntity);
            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode())) {
                return ruleActionEntity;
            }
        }

        /** check others filter */
        List<String> ruleList = Arrays.stream(ruleModels)
                .filter(s -> !s.equals(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .collect(Collectors.toList());

        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = null;
        for (String ruleModel : ruleList) {
            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> logicFilter = logicFilterGroup.get(ruleModel);
            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
            ruleMatterEntity.setAwardId(ruleMatterEntity.getAwardId());
            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
            ruleMatterEntity.setRuleModel(ruleModel);
            ruleActionEntity = logicFilter.filter(ruleMatterEntity);
            /** go into all filter */
            log.info("[rule before raffle] userId: {}, ruleModel: {}, code: {}, info: {}", raffleFactorEntity.getUserId(), ruleModel, ruleActionEntity.getCode(), ruleActionEntity.getInfo());
            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode())) return ruleActionEntity;
        }

        return ruleActionEntity;
    }
    /** doCheckRaffleCentreLogic has been deprecated */
    @Override
    protected RuleActionEntity<RuleActionEntity.RaffleCentreEntity> doCheckRaffleCentreLogic(RaffleFactorEntity raffleFactorEntity, String[] ruleModels) {
        if (ruleModels == null || ruleModels.length == 0) {
            return RuleActionEntity.<RuleActionEntity.RaffleCentreEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        Map<String, ILogicFilter<RuleActionEntity.RaffleCentreEntity>> logicFilterGroup = defaultLogicFactory.openLogicFilter();

        RuleActionEntity<RuleActionEntity.RaffleCentreEntity> ruleActionEntity = null;
        for (String ruleModel : ruleModels) {
            ILogicFilter<RuleActionEntity.RaffleCentreEntity> logicFilter = logicFilterGroup.get(ruleModel);
            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
            ruleMatterEntity.setAwardId(raffleFactorEntity.getAwardId());
            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
            ruleMatterEntity.setRuleModel(ruleModel);
            ruleActionEntity = logicFilter.filter(ruleMatterEntity);
            /** go into all filter */
            log.info("[rule during raffle] userId: {}, ruleModel: {}, code: {}, info: {}", raffleFactorEntity.getUserId(), ruleModel, ruleActionEntity.getCode(), ruleActionEntity.getInfo());
            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode())) return ruleActionEntity;
        }
        return ruleActionEntity;
    }


    @Override
    public StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException {
        return iStrategyRepository.takeQueueValue();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Long awardId) {
        iStrategyRepository.updateStrategyAwardStock(strategyId,awardId);
    }

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        return iStrategyRepository.queryStrategyAwardList(strategyId);
    }

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardListByActivityId(Long activityId) {
        Long strategyId = iStrategyRepository.queryStrategyIdByActivityId(activityId);
        return queryStrategyAwardList(strategyId);
    }

    @Override
    public Map<String, Integer> queryRuleTreeLockNodeValueByTreeIds(String[] treeIds) {
        return iStrategyRepository.queryRuleTreeLockNodeValueByTreeIds(treeIds);
    }

    @Override
    public List<RuleWeightVO> queryAwardRuleWeight(Long strategyId) {
        return iStrategyRepository.queryAwardRuleWeight(strategyId);
    }

    @Override
    public List<RuleWeightVO> queryAwardRuleWeightByActivityId(Long activityId) {
        Long strategyId = iStrategyRepository.queryStrategyIdByActivityId(activityId);
        return queryAwardRuleWeight(strategyId);
    }
}
