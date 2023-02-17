package dev.nincodedo.nincord.command;

import dev.nincodedo.nincord.BaseListenerAdapter;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

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

    private Optional<AbstractCommandParser> getCommandParserForEvent(GenericInteractionCreateEvent event) {
        return commandParsers.stream()
                .filter(commandParser -> commandParser.isEventMatchParser(event))
                .findFirst();
    }

    @Override
    public void onGenericInteractionCreate(@NotNull GenericInteractionCreateEvent event) {
        getCommandParserForEvent(event).ifPresent(abstractCommandParser -> abstractCommandParser.parseEvent(event));
    }
}
