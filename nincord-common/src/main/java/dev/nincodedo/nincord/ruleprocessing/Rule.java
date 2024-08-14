package dev.nincodedo.nincord.ruleprocessing;

public interface Rule<C extends RuleContext> {
    boolean canProcess(C context);

    void execute(C context);

    default void process(C context) {
        if (canProcess(context)) {
            execute(context);
        }
    }
}
