package dev.nincodedo.nincord.command.slash;

import dev.nincodedo.nincord.command.AbstractCommandParser;
import dev.nincodedo.nincord.command.CommandMetrics;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.ExecutorService;

public class SlashCommandParser extends AbstractCommandParser<SlashCommand, SlashCommandInteractionEvent> {

    public SlashCommandParser(@Qualifier("commandParserThreadPool") ExecutorService commandExecutorService,
            CommandMetrics commandMetrics) {
        super(commandExecutorService, SlashCommand.class, SlashCommandInteractionEvent.class, commandMetrics);
    }

    @Override
    protected String getCommandName(SlashCommandInteractionEvent event) {
        return event.getName();
    }
}
