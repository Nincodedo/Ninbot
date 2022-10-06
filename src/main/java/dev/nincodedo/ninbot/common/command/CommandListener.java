package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.BaseListenerAdapter;
import dev.nincodedo.ninbot.common.command.component.ButtonInteractionCommandParser;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandListener extends BaseListenerAdapter {

    private List<AbstractCommandParser> commandParsers;

    public CommandListener(List<AbstractCommandParser> commandParsers,
            List<Command> commands) {
        this.commandParsers = commandParsers;
        addCommands(commands);
    }

    private void addCommands(List<Command> commands) {
        commands.forEach(this::addCommand);
    }

    private void addCommand(Command command) {
        commandParsers.stream()
                .filter(commandParser -> commandParser.isCommandMatchParser(command))
                .forEach(commandParser -> commandParser.addCommand(command));
    }

    private AbstractCommandParser getCommandParserForEvent(GenericCommandInteractionEvent event) {
        return commandParsers.stream()
                .filter(commandParser -> commandParser.isEventMatchParser(event))
                .findFirst()
                .get();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        getCommandParserForEvent(event).parseEvent(event);
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        getCommandParserForEvent(event).parseEvent(event);
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        getCommandParserForEvent(event).parseEvent(event);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        getButtonParser().parseEvent(event);
    }

    @Override
    public void onCommandAutoCompleteInteraction(
            @NotNull CommandAutoCompleteInteractionEvent event) {
        getAutoCompleteParser().parseEvent(event);
    }

    private AutoCompleteCommandParser getAutoCompleteParser() {
        return commandParsers.stream()
                .filter(AutoCompleteCommandParser.class::isInstance)
                .map(AutoCompleteCommandParser.class::cast)
                .findFirst().get();
    }

    private ButtonInteractionCommandParser getButtonParser() {
        return commandParsers.stream()
                .filter(ButtonInteractionCommandParser.class::isInstance)
                .map(ButtonInteractionCommandParser.class::cast)
                .findFirst().get();
    }
}
