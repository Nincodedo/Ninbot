package dev.nincodedo.ninbot.components.reaction.emojitizer;

import dev.nincodedo.ninbot.common.command.component.ComponentData;
import dev.nincodedo.ninbot.common.command.component.ModalInteraction;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.ModalInteractionCommandMessageExecutor;
import dev.nincodedo.ninbot.components.reaction.EmojiReactionResponse;
import dev.nincodedo.ninbot.components.reaction.ReactionUtils;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class EmojitizerModalInteraction implements ModalInteraction {

    @Override
    public String getName() {
        return EmojitizerCommandName.EMOJITIZER.get();
    }

    @Override
    public MessageExecutor execute(@NotNull ModalInteractionEvent event,
            @NotNull ComponentData componentData) {
        var messageExecutor = new ModalInteractionCommandMessageExecutor(event);
        var textInput = event.getValue("emojitizer-text");
        if (textInput != null) {
            var emojiText = textInput.getAsString().replace(" ", "");
            if (ReactionUtils.isCanEmoji(emojiText)) {
                event.deferReply(true).queue();
                var reaction = new EmojiReactionResponse(emojiText);
                var target = componentData.data();
                event.getGuildChannel().getHistoryAround(target, 1).queue(replyAfterSuccess(event, reaction, target));
            } else {
                messageExecutor.addEphemeralMessage("That ain't something I can emojitize");
            }
        } else {
            messageExecutor.addEphemeralMessage("You gotta submit something.");
        }
        return messageExecutor;
    }

    @NotNull
    private Consumer<MessageHistory> replyAfterSuccess(@NotNull ModalInteractionEvent event,
            EmojiReactionResponse reaction, String target) {
        return messageHistory -> {
            var message = messageHistory.getMessageById(target);
            reaction.react(message, null);
            event.getHook().editOriginal("Emojitized!").queue();
        };
    }
}
