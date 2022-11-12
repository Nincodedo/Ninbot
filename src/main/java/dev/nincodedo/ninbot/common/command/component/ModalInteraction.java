package dev.nincodedo.ninbot.common.command.component;

import dev.nincodedo.ninbot.common.command.Command;
import dev.nincodedo.ninbot.common.command.CommandType;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.ModalInteractionCommandMessageExecutor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public interface ModalInteraction extends Command<ModalInteractionEvent>,
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
        messageExecutor.addEphemeralMessage("A weird error has come up, please try again.");
        log().error("ButtonInteraction {} received event {} without parseable component data {} {}", getName(),
                event.getResponseNumber(), event.getModalId(), event.getRawData());
        return messageExecutor;
    }

    MessageExecutor execute(@NotNull ModalInteractionEvent event, @NotNull ComponentData componentData);

    @Override
    default boolean isAbleToRegisterOnGuild() {
        return false;
    }

    Logger log();
}
