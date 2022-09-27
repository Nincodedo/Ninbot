package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.logging.FormatLogObject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
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
        switch (event) {
            case CommandAutoCompleteInteractionEvent autoCompleteEvent -> {
                return commandMap.get(autoCompleteEvent.getName());
            }
            case GenericCommandInteractionEvent commandInteractionEvent -> {
                return commandMap.get(commandInteractionEvent.getName());
            }
            case ButtonInteractionEvent buttonInteractionEvent -> {
                return commandMap.get(getButtonName(buttonInteractionEvent));
            }
            default -> {
                return null;
            }
        }
    }

    @NotNull
    private String getButtonName(ButtonInteractionEvent buttonInteractionEvent) {
        var buttonId = buttonInteractionEvent.getButton().getId();
        if (buttonId != null && buttonId.contains("-")) {
            var split = buttonId.split("-");
            if (split.length > 0) {
                return split[0];
            }
        }
        return "";
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
