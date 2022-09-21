package dev.nincodedo.ninbot.common.command.slash;

import dev.nincodedo.ninbot.common.command.AbstractCommandParser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class SlashCommandParser extends AbstractCommandParser<SlashCommand, SlashCommandInteractionEvent> {

    protected SlashCommandParser(ExecutorService commandExecutorService) {
        super(commandExecutorService);
    }

    @Override
    public Class<SlashCommand> getCommandClass() {
        return SlashCommand.class;
    }

    @Override
    public Class<SlashCommandInteractionEvent> getEventClass() {
        return SlashCommandInteractionEvent.class;
    }

}
