package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.command.message.MessageContextCommand;
import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.command.user.UserContextCommand;
import dev.nincodedo.ninbot.common.logging.UtilLogging;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
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

    public void parseEvent(@NotNull SlashCommandInteractionEvent event) {
        SlashCommand slashCommand = slashCommandMap.get(event.getName());
        if (slashCommand != null) {
            executorService.execute(() -> {
                try {
                    log.trace("Running slash command {} in server {} by user {}", slashCommand.getName(),
                            UtilLogging.logGuildName(event.getGuild()), UtilLogging.logUserInfo(event.getUser()));
                    slashCommand.execute(event).executeActions();
                } catch (Exception e) {
                    log.error("Slash command {} failed with an exception: Ran in server {} by {}",
                            slashCommand.getName(), UtilLogging.logGuildName(event.getGuild()),
                            UtilLogging.logUserInfo(event.getUser()), e);
                    event.reply("Ninbot encountered an error. :(").setEphemeral(true).queue();
                }
            });
        }
    }

    public void parseEvent(@NotNull MessageContextInteractionEvent event) {
        MessageContextCommand messageContextCommand = messageContextCommandMap.get(event.getName());
        if (messageContextCommand != null) {
            executorService.execute(() -> {
                try {
                    log.trace("Running message context command {} in server {} by user {}",
                            messageContextCommand.getName(), UtilLogging.logGuildName(event.getGuild()),
                            UtilLogging.logUserInfo(event.getUser()));
                    messageContextCommand.execute(event).executeActions();
                } catch (Exception e) {
                    log.error("Message context command {} failed with an exception: Ran in server {} by {}",
                            messageContextCommand.getName(), UtilLogging.logGuildName(event.getGuild()),
                            UtilLogging.logUserInfo(event.getUser()), e);
                }
            });
        }
    }

    public void parseEvent(@NotNull UserContextInteractionEvent event) {
        UserContextCommand userContextCommand = userContextCommandMap.get(event.getName());
        if (userContextCommand != null) {
            executorService.execute(() -> {
                try {
                    log.trace("Running user context command {} in server {} by user {}", userContextCommand.getName()
                            , UtilLogging.logGuildName(event.getGuild()), UtilLogging.logUserInfo(event.getUser()));
                    userContextCommand.execute(event).executeActions();
                } catch (Exception e) {
                    log.error("User context command {} failed with an exception: Ran in server {} by {}",
                            userContextCommand.getName(), UtilLogging.logGuildName(event.getGuild()),
                            UtilLogging.logUserInfo(event.getUser()), e);
                }
            });
        }
    }

    public void parseEvent(@NotNull CommandAutoCompleteInteractionEvent event) {
        AutoCompleteCommand autoCompleteCommand =
                autoCompleteCommandMap.get(event.getName());
        if (autoCompleteCommand != null) {
            executorService.execute(() -> {
                try {
                    log.trace("Running autocomplete {} in server {} by user {}", autoCompleteCommand.getName(),
                            UtilLogging.logGuildName(event.getGuild()), UtilLogging.logUserInfo(event.getUser()));
                    autoCompleteCommand.autoComplete(event);
                } catch (Exception e) {
                    log.error("Command autocomplete {} failed with an exception: Ran in server {} by {}",
                            autoCompleteCommand.getName(), UtilLogging.logGuildName(event.getGuild()),
                            UtilLogging.logUserInfo(event.getUser()), e);
                }
            });
        }
    }

    private void addSlashCommand(SlashCommand slashCommand) {
        slashCommandMap.put(slashCommand.getName(), slashCommand);
        if (slashCommand instanceof AutoCompleteCommand autoCompleteCommand) {
            autoCompleteCommandMap.put(autoCompleteCommand.getName(), autoCompleteCommand);
        }
    }

    private void addMessageContextCommand(MessageContextCommand messageContextCommand) {
        messageContextCommandMap.put(messageContextCommand.getName(), messageContextCommand);
    }

    private void addUserContextCommand(UserContextCommand userContextCommand) {
        userContextCommandMap.put(userContextCommand.getName(), userContextCommand);
    }

    void addCommands(List<Command> commands) {
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
