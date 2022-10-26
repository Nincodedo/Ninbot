package dev.nincodedo.ninbot.common.command.component;

import dev.nincodedo.ninbot.common.command.Command;
import dev.nincodedo.ninbot.common.command.CommandType;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.ModalInteractionCommandMessageExecutor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface ModalInteraction extends Command<ModalInteractionCommandMessageExecutor, ModalInteractionEvent>,
        Interaction {
    @Override
    default CommandType getType() {
        return CommandType.MODAL;
    }

    @Override
    default MessageExecutor execute(@NotNull ModalInteractionEvent event) {
        var componentDataOptional = getComponentDataFromEvent(event);
        if (componentDataOptional.isPresent()) {
            return execute(event, componentDataOptional.get());
        }
        var messageExecutor = new ModalInteractionCommandMessageExecutor(event);
        messageExecutor.addEphemeralMessage("💀");
        return messageExecutor;
    }

    MessageExecutor execute(@NotNull ModalInteractionEvent event,
            @NotNull ComponentData componentData);

    @Override
    default boolean isAbleToRegisterOnGuild() {
        return false;
    }
}
