package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.logging.FormatLogObject;
import dev.nincodedo.ninbot.common.logging.ServerLogger;
import dev.nincodedo.ninbot.common.logging.ServerLoggerFactory;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public abstract class AbstractCommandParser<T extends Command<?, F>, F extends GenericInteractionCreateEvent> {

    private Map<String, T> commandMap = new HashMap<>();
    private ExecutorService executorService;
    private ServerLogger log;

    protected AbstractCommandParser(ServerLoggerFactory serverLoggerFactory, ExecutorService executorService) {
        this.executorService = executorService;
        log = serverLoggerFactory.getLogger(this.getClass());
    }

    public void parseEvent(@NotNull F event) {
        T command = getCommand(event);
        if (command != null) {
            executorService.execute(() -> {
                try {
                    log.trace(event.getGuild()
                            .getId(), "Running command {} by user {}", command.getName(),
                            FormatLogObject.userInfo(event.getUser()));
                    command.execute(event).executeActions();
                } catch (Exception e) {
                    log.error(event.getGuild()
                                    .getId(), e, "Command {} failed with an exception: Ran by {}", command.getName(),
                            FormatLogObject.userInfo(event.getUser()));
                }
            });
        }
    }

    private T getCommand(F event) {
        if (event instanceof CommandAutoCompleteInteractionEvent autoCompleteEvent) {
            return commandMap.get(autoCompleteEvent.getName());
        } else if (event instanceof GenericCommandInteractionEvent commandInteractionEvent) {
            return commandMap.get(commandInteractionEvent.getName());
        } else {
            return null;
        }
    }

    void addCommand(T command) {
        commandMap.put(command.getName(), command);
    }

    public boolean isEventMatchParser(GenericCommandInteractionEvent event) {
        return getEventClass().isInstance(event);
    }

    public boolean isCommandMatchParser(Command command) {
        return getCommandClass().isInstance(command);
    }

    public abstract Class<T> getCommandClass();

    public abstract Class<F> getEventClass();
}
