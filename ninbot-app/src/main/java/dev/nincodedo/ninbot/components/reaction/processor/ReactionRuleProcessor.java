package dev.nincodedo.ninbot.components.reaction.processor;

import java.util.List;

public class ReactionRuleProcessor {
    private List<ReactionRule> reactionRules;

    public ReactionRuleProcessor(List<ReactionRule> reactionRules) {
        this.reactionRules = reactionRules;
    }

    public ReactionContext process(ReactionContext reactionContext) {
        for (var reactionRule : reactionRules) {
            if (reactionRule.canProcess(reactionContext)) {
                reactionRule.process(reactionContext);
            }
        }
        return reactionContext;
    }
}
