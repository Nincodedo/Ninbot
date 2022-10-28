package dev.nincodedo.ninbot.components.reaction.emojitizer;

import dev.nincodedo.ninbot.common.command.message.MessageContextCommand;
import dev.nincodedo.ninbot.common.message.MessageContextInteractionEventMessageExecutor;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.components.reaction.ReactionUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
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
    public MessageExecutor execute(@NotNull MessageContextInteractionEvent event) {
        var messageExecutor = new MessageContextInteractionEventMessageExecutor(event);
        var currentEmojiCount = event.getTarget().getReactions().size();
        if (currentEmojiCount >= Message.MAX_REACTIONS) {
            messageExecutor.addEphemeralMessage(resourceBundle(event.getUserLocale()).getString("command.emojitizer"
                    + ".messagecontext.maxreactions"));
        } else {
            var maxCount = Message.MAX_REACTIONS - currentEmojiCount;
            TextInput input = TextInput.create("emojitizer-text", resourceBundle(event.getUserLocale()).getString(
                                    "command.emojitizer.messagecontext.textinput"),
                            TextInputStyle.SHORT)
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
            var characterBundle = "command.emojitizer.messagecontext.modal.character.";
            var character = maxCount == 1 ? resourceBundle(event.getUserLocale()).getString(
                    characterBundle + "single") : resourceBundle(event.getUserLocale()).getString(
                    characterBundle + "plural");
            Modal modal = Modal.create("emojitizer-modal-" + event.getTarget().getId() + "$"
                                    + alreadyExistingLetterEmojis,
                            String.format(resourceBundle(event.getUserLocale()).getString("command.emojitizer"
                                    + ".messagecontext.modal.title"), maxCount, character))
                    .addActionRow(input)
                    .build();
            event.replyModal(modal).queue();
        }
        return messageExecutor;
    }
}
