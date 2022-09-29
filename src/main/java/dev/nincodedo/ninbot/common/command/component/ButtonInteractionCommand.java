package dev.nincodedo.ninbot.common.command.component;

import dev.nincodedo.ninbot.common.command.Command;
import dev.nincodedo.ninbot.common.command.CommandType;
import dev.nincodedo.ninbot.common.message.ButtonInteractionCommandMessageExecutor;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface ButtonInteractionCommand extends Command<ButtonInteractionCommandMessageExecutor,
        ButtonInteractionEvent> {

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
        var buttonId = event.getButton().getId();
        if (buttonId != null && buttonId.contains("-")) {
            var splitId = buttonId.split("-");
            if (splitId.length == 3) {
                var button = new Button(splitId[0], splitId[1], splitId[2]);
                return executeButtonPress(event, button);
            }
        }
        return new ButtonInteractionCommandMessageExecutor(event).editEphemeralMessage("💀");
    }

    MessageExecutor<ButtonInteractionCommandMessageExecutor> executeButtonPress(@NotNull ButtonInteractionEvent event
            , @NotNull Button button);
}
