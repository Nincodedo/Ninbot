package dev.nincodedo.nincord.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.IntegrationType;

import java.util.Set;

public interface AutoCompleteCommand extends Command<CommandAutoCompleteInteractionEvent> {
    @Override
    default boolean isAbleToRegisterOnGuild() {
        return false;
    }

    @Override
    default Set<IntegrationType> allowedIntegrations() {
        return Set.of(IntegrationType.UNKNOWN);
    }

    @Override
    default CommandType getType() {
        return CommandType.AUTO_COMPLETE;
    }
}
