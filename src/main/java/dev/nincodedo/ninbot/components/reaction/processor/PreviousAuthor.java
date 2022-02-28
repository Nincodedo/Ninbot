package dev.nincodedo.ninbot.components.reaction.processor;

public class PreviousAuthor implements ReactionRule {

    @Override
    public String getReplaceTarget() {
        return "$message.previous.author";
    }

    @Override
    public void process(ReactionContext reactionContext) {
        String reactionMessage = reactionContext.getReactionMessage();
        if (canProcess(reactionContext)) {
            var lastMessageOptional = getPreviousMessage(reactionContext.getChannel(), reactionContext.getMessage());
            if (reactionMessage.contains(getReplaceTarget()) && lastMessageOptional.isPresent()) {
                var lastMessage = lastMessageOptional.get();
                var previousAuthorName = lastMessage.getMember() != null ? lastMessage.getMember()
                        .getEffectiveName() : lastMessage.getAuthor().getName();
                reactionMessage = reactionMessage.replace(getReplaceTarget(), previousAuthorName);
            }
        }
        reactionContext.setReactionMessage(reactionMessage);
    }
}
