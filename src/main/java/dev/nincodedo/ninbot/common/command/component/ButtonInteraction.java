package dev.nincodedo.ninbot.common.command.component;

import dev.nincodedo.ninbot.common.command.Command;
import dev.nincodedo.ninbot.common.command.CommandType;
import dev.nincodedo.ninbot.common.message.ButtonInteractionCommandMessageExecutor;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public interface ButtonInteraction extends Command<ButtonInteractionEvent>, Interaction {

    @Override
    default CommandType getType() {
        return CommandType.BUTTON;
    }

    @Override
    default boolean isAbleToRegisterOnGuild() {
        return false;
    }

    @Override
    default MessageExecutor execute(@NotNull ButtonInteractionEvent event) {
        var componentDataOptional = getComponentDataFromEvent(event);
        if (componentDataOptional.isPresent()) {
            return execute(event, componentDataOptional.get());
        }
        var messageExecutor = new ButtonInteractionCommandMessageExecutor(event);
        messageExecutor.addEphemeralMessage("A weird error has come up, please try again.");
        log().error("ButtonInteraction {} received event {} without parseable component data {} {}", getName(),
                event.getResponseNumber(), event.getComponentId(), event.getRawData());
        return messageExecutor;
    }

    MessageExecutor execute(@NotNull ButtonInteractionEvent event, ComponentData componentData);

    Logger log();
}
