package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.logging.FormatLogObject;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public abstract class AbstractCommandParser<T extends Command<?, F>, F extends GenericCommandInteractionEvent,
        G extends AbstractCommandParser<T, F, G>> {
    private Map<String, T> commandMap = new HashMap<>();
    private ExecutorService executorService;

    protected AbstractCommandParser() {
        this.executorService = Executors.newCachedThreadPool(new NamedThreadFactory("command-parser"));
    }

    public void parseEvent(@NotNull F event) {
        T command = commandMap.get(event.getName());
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

    void addCommand(T command) {
        commandMap.put(command.getName(), command);
    }

    public boolean isCommandMatchParser(Command command) {
        return command.getClass().isInstance(getCommandClass());
    }

    public boolean isEventMatchParser(GenericCommandInteractionEvent event) {
        return getEventClass().isInstance(event);
    }

    abstract Class<T> getCommandClass();

    abstract Class<F> getEventClass();
}
