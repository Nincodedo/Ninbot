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

import java.util.Arrays;
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
        if (textInput == null) {
            return messageExecutor;
        }
        var emojiText = textInput.getAsString().replace(" ", "");
        String[] existingEmojiLetters = new String[0];
        if (componentData.data().split("\\$").length > 1) {
            existingEmojiLetters = componentData.data().split("\\$")[1].split(",");
        }
        if (ReactionUtils.isCanEmoji(emojiText) && !isExistingLetterUsed(emojiText, existingEmojiLetters)) {
            event.deferReply(true).queue();
            var reaction = new EmojiReactionResponse(emojiText);
            var messageId = componentData.data().split("\\$")[0];
            event.getGuildChannel()
                    .getHistoryAround(messageId, 1)
                    .queue(replyAfterSuccess(event, reaction, messageId));
        } else if (ReactionUtils.isCanEmoji(emojiText)) {
            messageExecutor.addEphemeralMessage(resourceBundle(event.getUserLocale()).getString("modal.emojitizer"
                    + ".cantbecauseletters"));
        } else {
            messageExecutor.addEphemeralMessage(resourceBundle(event.getUserLocale()).getString("modal.emojitizer"
                    + ".cantbecausenotemojitizable"));
        }
        return messageExecutor;
    }

    private boolean isExistingLetterUsed(String emojiText, String[] existingEmojiLetters) {
        if (existingEmojiLetters.length == 0) {
            return false;
        }
        boolean existingLetterUsed = false;
        for (char c : emojiText.toCharArray()) {
            var emojiLetter = ReactionUtils.getLetterMap().get(Character.toString(c).toUpperCase());
            if (Arrays.asList(existingEmojiLetters).contains(emojiLetter)) {
                existingLetterUsed = true;
                break;
            }
        }
        return existingLetterUsed;
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
