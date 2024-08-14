package dev.nincodedo.ninbot.components.reaction.processor;

import dev.nincodedo.nincord.ruleprocessing.RuleProcessor;
import lombok.Getter;

import java.util.List;

@Getter
public class ReactionRuleProcessor implements RuleProcessor<ReactionRule, ReactionContext> {
    private List<ReactionRule> rules;

    public ReactionRuleProcessor(List<ReactionRule> rules) {
        this.rules = rules;
    }
}
