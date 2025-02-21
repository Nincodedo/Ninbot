package dev.nincodedo.nincord.command.component;

import dev.nincodedo.nincord.command.AbstractCommandParser;
import dev.nincodedo.nincord.command.CommandMetrics;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.util.concurrent.ExecutorService;

public class StringSelectMenuInteractionCommandParser extends AbstractCommandParser<StringSelectMenuInteraction,
        StringSelectInteractionEvent> {
    public StringSelectMenuInteractionCommandParser(ExecutorService commandExecutorService,
            CommandMetrics commandMetrics) {
        super(commandExecutorService, StringSelectMenuInteraction.class, StringSelectInteractionEvent.class,
                commandMetrics);
    }

    @Override
    protected String getCommandName(StringSelectInteractionEvent event) {
        return getComponentName(event.getComponentId());
    }
}
