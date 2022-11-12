package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.logging.FormatLogObject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

@Slf4j
public abstract class AbstractCommandParser<T extends Command<F>, F extends GenericInteractionCreateEvent> {

    private Map<String, T> commandMap = new HashMap<>();
    private ExecutorService executorService;

    protected AbstractCommandParser(ExecutorService commandExecutorService) {
        this.executorService = commandExecutorService;
    }

    public void parseEvent(@NotNull F event) {
        getCommand(event).ifPresent(command -> executorService.execute(() -> {
            try {
                log.trace("Running {} command {} in server {} by user {}", command.getType(), command.getName(),
                        FormatLogObject.guildName(event.getGuild()), FormatLogObject.userInfo(event.getUser()));
                command.execute(event).executeActions();
            } catch (Exception e) {
                log.error("{} Command {} failed with an exception: Ran in server {} by {}", command.getType(),
                        command.getName(), FormatLogObject.guildName(event.getGuild()),
                        FormatLogObject.userInfo(event.getUser()), e);
            }
        }));
    }

    private Optional<T> getCommand(F event) {
        String commandName = getCommandName(event);
        return Optional.ofNullable(commandMap.get(commandName));
    }

    protected abstract String getCommandName(F event);

    @NotNull
    protected String getComponentName(String componentId) {
        if (componentId.contains("-")) {
            var split = componentId.split("-");
            if (split.length > 0) {
                return split[0];
            }
        }
        return "";
    }

    void addCommand(T command) {
        commandMap.put(command.getName(), command);
    }

    public boolean isEventMatchParser(GenericEvent event) {
        return getEventClass().isInstance(event);
    }

    public boolean isCommandMatchParser(Command<?> command) {
        return getCommandClass().isInstance(command);
    }

    public abstract Class<T> getCommandClass();

    public abstract Class<F> getEventClass();
}
