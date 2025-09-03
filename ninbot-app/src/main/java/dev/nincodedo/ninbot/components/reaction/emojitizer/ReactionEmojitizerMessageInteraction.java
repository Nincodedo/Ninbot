package dev.nincodedo.ninbot.components.reaction.emojitizer;

import dev.nincodedo.ninbot.components.reaction.ReactionUtils;
import dev.nincodedo.nincord.command.message.MessageContextCommand;
import dev.nincodedo.nincord.message.MessageContextInteractionEventMessageExecutor;
import dev.nincodedo.nincord.message.MessageExecutor;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ReactionEmojitizerMessageInteraction implements MessageContextCommand {

    @Override
    public String getName() {
        return EmojitizerCommandName.EMOJITIZER.get();
    }

    @Override
    public MessageExecutor execute(@NotNull MessageContextInteractionEvent event,
            @NotNull MessageContextInteractionEventMessageExecutor messageExecutor) {
        var currentEmojiCount = event.getTarget().getReactions().size();
        if (currentEmojiCount >= Message.MAX_REACTIONS) {
            messageExecutor.addEphemeralMessage(resourceBundle(event.getUserLocale()).getString(
                    "command.emojitizer.messagecontext.maxreactions"));
        } else {
            var maxCount = Message.MAX_REACTIONS - currentEmojiCount;
            TextInput input = TextInput.create("emojitizer-text", TextInputStyle.SHORT)
                    .setMinLength(1)
                    .setMaxLength(maxCount)
                    .build();
            var alreadyExistingLetterEmojis = event.getTarget()
                    .getReactions()
                    .stream()
                    .map(MessageReaction::getEmoji)
                    .filter(emojiUnion -> emojiUnion.getType() == Emoji.Type.UNICODE)
                    .map(EmojiUnion::asUnicode)
                    .map(UnicodeEmoji::getFormatted)
                    .filter(s -> ReactionUtils.getLetterMap().containsValue(s))
                    .collect(Collectors.joining(","));
            var amount = maxCount == 1 ? "single" : "plural";
            var characterBundle = STR."command.emojitizer.messagecontext.modal.character.\{amount}";
            var character = resourceBundle(event.getUserLocale()).getString(characterBundle);
            Modal modal = Modal.create(STR."emojitizer-modal-\{event.getTarget()
                                    .getId()};\{alreadyExistingLetterEmojis}",
                            resourceBundle(event.getUserLocale()).getString(
                                    "command.emojitizer.messagecontext.modal.title").formatted(maxCount, character))
                    .addComponents(Label.of(resourceBundle(event.getUserLocale()).getString("command.emojitizer"
                            + ".messagecontext.textinput"), input))
                    .build();
            messageExecutor.addModal(modal);
        }
        return messageExecutor;
    }
}
