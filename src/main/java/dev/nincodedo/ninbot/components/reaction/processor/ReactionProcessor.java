package dev.nincodedo.ninbot.components.reaction.processor;

public interface ReactionProcessor {
    void process(ReactionContext reactionContext);
    String getReplaceTarget();
}
