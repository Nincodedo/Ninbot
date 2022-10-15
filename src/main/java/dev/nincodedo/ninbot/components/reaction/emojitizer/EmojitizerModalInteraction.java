package dev.nincodedo.ninbot.components.reaction.emojitizer;

import dev.nincodedo.ninbot.common.command.component.ComponentData;
import dev.nincodedo.ninbot.common.command.component.ModalInteraction;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.ModalInteractionCommandMessageExecutor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class EmojitizerModalInteraction implements ModalInteraction {
    @Override
    public String getName() {
        return EmojitizerCommandName.EMOJITIZER.get();
    }

    @Override
    public MessageExecutor<ModalInteractionCommandMessageExecutor> execute(@NotNull ModalInteractionEvent event,
            @NotNull ComponentData componentData) {
        var messageExecutor = new ModalInteractionCommandMessageExecutor(event);
        var textInput = event.getValue("emojitizer-text");
        if (textInput != null) {
            var emojitext = textInput.getAsString();

        } else {
            messageExecutor.addEphemeralMessage("You gotta submit something.");
        }
        return messageExecutor;
    }
}
