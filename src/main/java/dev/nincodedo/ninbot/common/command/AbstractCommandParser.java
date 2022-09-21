package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.logging.FormatLogObject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Slf4j
public abstract class AbstractCommandParser<T extends Command<?, F>, F extends GenericInteractionCreateEvent> {

    private Map<String, T> commandMap = new HashMap<>();
    private ExecutorService executorService;

    protected AbstractCommandParser(ExecutorService commandExecutorService) {
        this.executorService = commandExecutorService;
    }

    public void parseEvent(@NotNull F event) {
        T command = getCommand(event);
        if (command != null) {
            executorService.execute(() -> {
                try {
                    log.trace("Running command {} in server {} by user {}", command.getName(),
                            FormatLogObject.guildName(event.getGuild()), FormatLogObject.userInfo(event.getUser()));
                    command.execute(event).executeActions();
                } catch (Exception e) {
                    log.error("Command {} failed with an exception: Ran in server {} by {}",
                            command.getName(), FormatLogObject.guildName(event.getGuild()),
                            FormatLogObject.userInfo(event.getUser()), e);
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
