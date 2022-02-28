package dev.nincodedo.ninbot.components.reaction.processor;

import org.apache.commons.lang3.StringUtils;

public class PreviousContent implements ReactionRule {
    @Override
    public void process(ReactionContext reactionContext) {
        String reactionMessage = reactionContext.getReactionMessage();
        if (canProcess(reactionContext)) {
            var lastMessageOptional = getPreviousMessage(reactionContext.getChannel(), reactionContext.getMessage());
            if (reactionMessage.contains(getReplaceTarget()) && lastMessageOptional.isPresent()) {
                var lastMessage = lastMessageOptional.get();
                String previousMessageContent = lastMessage.getContentRaw();
                if (reactionMessage.contains("#toUpper")) {
                    reactionMessage = reactionMessage.replace("#toUpper", "");
                    previousMessageContent = previousMessageContent.toUpperCase();
                }
                if (StringUtils.isBlank(previousMessageContent)) {
                    reactionContext.setCanReact(false);
                }
                reactionMessage = reactionMessage.replace(getReplaceTarget(), previousMessageContent);
            }
        }
        reactionContext.setReactionMessage(reactionMessage);
    }

    @Override
    public String getReplaceTarget() {
        return "$message.previous.content";
    }
}
