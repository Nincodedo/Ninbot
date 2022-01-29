package dev.nincodedo.ninbot.common.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandListener extends ListenerAdapter {

    private CommandParser commandParser;

    public CommandListener(CommandParser commandParser, List<SlashCommand> slashCommands) {
        this.commandParser = commandParser;
        addSlashCommands(slashCommands);
    }

    private void addSlashCommands(List<SlashCommand> slashCommands) {
        commandParser.addSlashCommands(slashCommands);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        commandParser.parseEvent(slashCommandEvent);
    }
}
