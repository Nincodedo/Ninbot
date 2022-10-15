package dev.nincodedo.ninbot.common.command.component;

import dev.nincodedo.ninbot.common.command.Command;
import dev.nincodedo.ninbot.common.command.CommandType;
import dev.nincodedo.ninbot.common.message.ButtonInteractionCommandMessageExecutor;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface ButtonInteraction extends Command<ButtonInteractionCommandMessageExecutor,
        ButtonInteractionEvent>, Interaction {

    @Override
    default CommandType getType() {
        return CommandType.BUTTON;
    }

    @Override
    default boolean isAbleToRegisterOnGuild() {
        return false;
    }

    @Override
    default MessageExecutor<ButtonInteractionCommandMessageExecutor> execute(@NotNull ButtonInteractionEvent event) {
        var componentDataOptional = getComponentDataFromEvent(event);
        if (componentDataOptional.isPresent()) {
            return executeButtonPress(event, componentDataOptional.get());
        }
        return new ButtonInteractionCommandMessageExecutor(event).addEphemeralMessage("💀");
    }

    MessageExecutor<ButtonInteractionCommandMessageExecutor> executeButtonPress(@NotNull ButtonInteractionEvent event
            , ComponentData componentData);
}
