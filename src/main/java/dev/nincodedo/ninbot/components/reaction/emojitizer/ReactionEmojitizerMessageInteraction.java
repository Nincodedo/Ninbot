package dev.nincodedo.ninbot.components.reaction.emojitizer;

import dev.nincodedo.ninbot.common.command.message.MessageContextCommand;
import dev.nincodedo.ninbot.common.message.MessageContextInteractionEventMessageExecutor;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ReactionEmojitizerMessageInteraction implements MessageContextCommand {

    @Override
    public String getName() {
        return EmojitizerCommandName.EMOJITIZER.get();
    }

    @Override
    public MessageExecutor<MessageContextInteractionEventMessageExecutor> execute(@NotNull MessageContextInteractionEvent event) {
        var messageExecutor = new MessageContextInteractionEventMessageExecutor(event);
        TextInput input = TextInput.create("emojitizer-text", "What do you want to Emojitize?",
                        TextInputStyle.SHORT)
                .setMinLength(1)
                .setMaxLength(20)
                .build();
        Modal modal = Modal.create("emojitizer-modal-" + event.getTarget().getId(), "Emojitizer")
                .addActionRow(input)
                .build();
        event.replyModal(modal).queue();
        return messageExecutor;
    }
}
