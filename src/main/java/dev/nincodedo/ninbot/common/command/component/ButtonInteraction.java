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
    default MessageExecutor execute(@NotNull ButtonInteractionEvent event) {
        var componentDataOptional = getComponentDataFromEvent(event);
        if (componentDataOptional.isPresent()) {
            return executeButtonPress(event, componentDataOptional.get());
        }
        var messageExecutor = new ButtonInteractionCommandMessageExecutor(event);
        messageExecutor.addEphemeralMessage("💀");
        return messageExecutor;
    }

    MessageExecutor executeButtonPress(@NotNull ButtonInteractionEvent event
            , ComponentData componentData);
}
