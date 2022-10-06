package dev.nincodedo.ninbot.components.reaction;

import dev.nincodedo.ninbot.components.reaction.processor.PreviousAuthor;
import dev.nincodedo.ninbot.components.reaction.processor.PreviousContent;
import dev.nincodedo.ninbot.components.reaction.processor.ReactionContext;
import dev.nincodedo.ninbot.components.reaction.processor.ReactionRule;
import dev.nincodedo.ninbot.components.reaction.processor.ReactionRuleProcessor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.util.List;

class SpecialReactionResponse extends ReactionResponse {


    public SpecialReactionResponse(ReactionResponse response) {
        super(response);
    }

    @Override
    public void react(Message message, MessageChannel channel) {
        List<ReactionRule> reactionRules = List.of(
                new PreviousAuthor(),
                new PreviousContent()
        );
        ReactionRuleProcessor reactionRuleProcessor = new ReactionRuleProcessor(reactionRules);
        ReactionContext reactionContext = new ReactionContext();
        reactionContext.setReactionMessage(response);
        reactionContext.setMessage(message);
        reactionContext.setChannel(channel);
        var context = reactionRuleProcessor.process(reactionContext);
        if (context.isCanReact()) {
            channel.sendMessage(context.getReactionMessage()).queue();
        }
    }
}
