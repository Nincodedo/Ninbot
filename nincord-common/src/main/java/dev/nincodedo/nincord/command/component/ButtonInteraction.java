package dev.nincodedo.nincord.command.component;

import dev.nincodedo.nincord.command.Command;
import dev.nincodedo.nincord.command.CommandType;
import dev.nincodedo.nincord.message.ButtonInteractionCommandMessageExecutor;
import dev.nincodedo.nincord.message.MessageExecutor;
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
        var messageExecutor = new ButtonInteractionCommandMessageExecutor(event);
        if (componentDataOptional.isPresent()) {
            return execute(event, messageExecutor, componentDataOptional.get());
        }
        messageExecutor.addEphemeralMessage("A weird error has come up, please try again.");
        log().error("ButtonInteraction {} received event {} without parseable component data {} {}", getName(),
                event.getResponseNumber(), event.getComponentId(), event.getRawData());
        return messageExecutor;
    }

    MessageExecutor execute(@NotNull ButtonInteractionEvent event,
            @NotNull ButtonInteractionCommandMessageExecutor messageExecutor, @NotNull ComponentData componentData);

    Logger log();
}
