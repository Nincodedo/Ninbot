package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.command.message.MessageContextCommand;
import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.command.user.UserContextCommand;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class CommandParser {

    private Map<String, SlashCommand> slashCommandMap = new HashMap<>();
    private Map<String, MessageContextCommand> messageContextCommandMap = new HashMap<>();
    private Map<String, UserContextCommand> userContextCommandMap = new HashMap<>();
    private Map<String, AutoCompleteCommand> autoCompleteCommandMap = new HashMap<>();
    private ExecutorService executorService;

    CommandParser() {
        this.executorService = Executors.newCachedThreadPool(new NamedThreadFactory("command-parser"));
    }

    public void parseEvent(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        SlashCommand slashCommand = slashCommandMap.get(slashCommandEvent.getName());
        if (slashCommand != null) {
            executorService.execute(() -> {
                try {
                    log.trace("Running slash command {} in server {} by user {}", slashCommand.getName(),
                            slashCommandEvent.getGuild()
                                    .getId(), slashCommandEvent.getUser().getId());
                    slashCommand.execute(slashCommandEvent).executeActions();
                } catch (Exception e) {
                    log.error("Slash command {} failed with an exception: Ran in server {} by {}",
                            slashCommand.getName(), slashCommandEvent.getGuild()
                                    .getId(), slashCommandEvent.getUser().getId(), e);
                }
            });
        }
    }

    public void parseEvent(@NotNull MessageContextInteractionEvent messageContextInteractionEvent) {
        MessageContextCommand messageContextCommand =
                messageContextCommandMap.get(messageContextInteractionEvent.getName());
        if (messageContextCommand != null) {
            executorService.execute(() -> {
                try {
                    log.trace("Running message context command {} in server {} by user {}",
                            messageContextCommand.getName(), messageContextInteractionEvent.getGuild()
                                    .getId(), messageContextInteractionEvent.getUser().getId());
                    messageContextCommand.execute(messageContextInteractionEvent).executeActions();
                } catch (Exception e) {
                    log.error("Message context command {} failed with an exception: Ran in server {} by {}",
                            messageContextCommand.getName(), messageContextInteractionEvent.getGuild()
                                    .getId(), messageContextInteractionEvent.getUser().getId(), e);
                }
            });
        }
    }

    public void parseEvent(@NotNull CommandAutoCompleteInteractionEvent commandAutoCompleteInteractionEvent) {
        AutoCompleteCommand autoCompleteCommand =
                autoCompleteCommandMap.get(commandAutoCompleteInteractionEvent.getName());
        if (autoCompleteCommand != null) {
            executorService.execute(() -> {
                try {
                    log.trace("Running autocomplete {} in server {} by user {}", autoCompleteCommand.getName(),
                            commandAutoCompleteInteractionEvent.getGuild()
                                    .getId(), commandAutoCompleteInteractionEvent.getUser().getId());
                    autoCompleteCommand.autoComplete(commandAutoCompleteInteractionEvent);
                } catch (Exception e) {
                    log.error("Command autocomplete {} failed with an exception: Ran in server {} by {}",
                            autoCompleteCommand.getName(), commandAutoCompleteInteractionEvent.getGuild()
                                    .getId(), commandAutoCompleteInteractionEvent.getUser().getId(), e);
                }
            });
        }
    }

    public void addSlashCommand(SlashCommand slashCommand) {
        slashCommandMap.put(slashCommand.getName(), slashCommand);
        if (slashCommand instanceof AutoCompleteCommand autoCompleteCommand) {
            autoCompleteCommandMap.put(autoCompleteCommand.getName(), autoCompleteCommand);
        }
    }

    public void addMessageContextCommand(MessageContextCommand messageContextCommand) {
        messageContextCommandMap.put(messageContextCommand.getName(), messageContextCommand);
    }

    public void addUserContextCommand(UserContextCommand userContextCommand) {
        userContextCommandMap.put(userContextCommand.getName(), userContextCommand);
    }

    public void addCommands(List<Command> commands) {
        commands.forEach(command -> {
            switch (command) {
                case SlashCommand slashCommand -> addSlashCommand(slashCommand);
                case MessageContextCommand messageContextCommand -> addMessageContextCommand(messageContextCommand);
                case UserContextCommand userContextCommand -> addUserContextCommand(userContextCommand);
                default -> throw new IllegalStateException("Unexpected value: " + command);
            }
        });
    }
}
