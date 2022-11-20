package dev.nincodedo.nincord.command.component;

import dev.nincodedo.nincord.command.Command;
import dev.nincodedo.nincord.command.CommandType;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.message.ModalInteractionCommandMessageExecutor;
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
        var messageExecutor = new ModalInteractionCommandMessageExecutor(event);
        if (componentDataOptional.isPresent()) {
            return execute(event, messageExecutor, componentDataOptional.get());
        }
        messageExecutor.addEphemeralMessage("A weird error has come up, please try again.");
        log().error("ButtonInteraction {} received event {} without parseable component data {} {}", getName(),
                event.getResponseNumber(), event.getModalId(), event.getRawData());
        return messageExecutor;
    }

    MessageExecutor execute(@NotNull ModalInteractionEvent event,
            @NotNull ModalInteractionCommandMessageExecutor messageExecutor, @NotNull ComponentData componentData);

    @Override
    default boolean isAbleToRegisterOnGuild() {
        return false;
    }

    Logger log();
}
