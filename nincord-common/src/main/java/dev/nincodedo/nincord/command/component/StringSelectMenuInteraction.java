package dev.nincodedo.nincord.command.component;

import dev.nincodedo.nincord.command.Command;
import dev.nincodedo.nincord.command.CommandType;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.message.StringSelectMenuInteractionCommandMessageExecutor;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public interface StringSelectMenuInteraction extends Command<StringSelectInteractionEvent>, Interaction {
    @Override
    default CommandType getType() {
        return CommandType.STRING_SELECT_MENU;
    }

    @Override
    default boolean isAbleToRegisterOnGuild() {
        return false;
    }

    MessageExecutor execute(@NotNull StringSelectInteractionEvent event,
            @NotNull StringSelectMenuInteractionCommandMessageExecutor messageExecutor,
            @NotNull ComponentData componentData);

    @Override
    default MessageExecutor execute(@NotNull StringSelectInteractionEvent event) {
        var componentDataOptional = getComponentDataFromEvent(event);
        var messageExecutor = new StringSelectMenuInteractionCommandMessageExecutor(event);
        if (componentDataOptional.isPresent()) {
            return execute(event, messageExecutor, componentDataOptional.get());
        }
        messageExecutor.addEphemeralMessage("A weird error has come up, please try again.");
        log().error("StringSelectMenuInteraction {} received event {} without parseable component data {} {}",
                getName(),
                event.getResponseNumber(), event.getComponentId(), event.getRawData());
        return messageExecutor;
    }

    Logger log();
}
