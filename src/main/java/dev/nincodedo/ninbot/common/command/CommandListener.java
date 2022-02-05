package dev.nincodedo.ninbot.common.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandListener extends ListenerAdapter {

    private CommandParser commandParser;

    public CommandListener(CommandParser commandParser, List<Command> commands) {
        this.commandParser = commandParser;
        addCommands(commands);
    }

    private void addCommands(List<Command> commands) {
        commandParser.addCommands(commands);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        commandParser.parseEvent(slashCommandEvent);
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent messageContextInteractionEvent) {
        commandParser.parseEvent(messageContextInteractionEvent);
    }

    @Override
    public void onCommandAutoCompleteInteraction(
            @NotNull CommandAutoCompleteInteractionEvent commandAutoCompleteInteractionEvent) {
        commandParser.parseEvent(commandAutoCompleteInteractionEvent);
    }
}
