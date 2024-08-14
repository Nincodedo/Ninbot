package dev.nincodedo.nincord.ruleprocessing;

import java.util.List;

public interface RuleProcessor<R extends Rule<C>, C extends RuleContext> {

    List<R> getRules();

    default C process(C context) {
        for (R rule : getRules()) {
            if (rule.canProcess(context)) {
                rule.process(context);
            }
        }
        return context;
    }
}
