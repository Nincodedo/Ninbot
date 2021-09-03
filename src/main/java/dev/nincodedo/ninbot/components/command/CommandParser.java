package dev.nincodedo.ninbot.components.command;

import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.config.component.ComponentType;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static dev.nincodedo.ninbot.common.Emojis.QUESTION_MARK;

@Slf4j
@Component
public class CommandParser {

    private ConfigService configService;
    private ComponentService componentService;
    @Getter
    private Map<String, AbstractCommand> commandHashMap = new HashMap<>();
    private Map<String, SlashCommand> slashCommandMap = new HashMap<>();
    private Map<String, String> commandAliasMap = new HashMap<>();
    private ExecutorService executorService;

    CommandParser(ConfigService configService, ComponentService componentService) {
        this.configService = configService;
        this.executorService = Executors.newCachedThreadPool(new NamedThreadFactory("command-parser"));
        this.componentService = componentService;
    }

    void parseEvent(PrivateMessageReceivedEvent event) {
        String message = event.getMessage().getContentStripped();
        if (StringUtils.isNotBlank(message)) {
            String commandName = getCommand(message);
            AbstractCommand command = commandHashMap.get(commandName);
            if (command != null) {
                try {
                    executorService.execute(() -> command.execute(event));
                } catch (Exception e) {
                    log.error("Error executing command " + command.getName(), e);
                }
            } else {
                event.getMessage().addReaction(QUESTION_MARK).queue();
            }
        }
    }

    private String getCommand(String message) {
        String[] splitMessage = message.split("\\s+");
        if (splitMessage.length > 1) {
            var commandName = translateAlias(splitMessage[1]);
            return commandName != null ? commandName.toLowerCase() : StringUtils.EMPTY;
        }
        return null;
    }

    private String translateAlias(String alias) {
        var commandName = commandAliasMap.get(alias);
        return commandName != null ? commandName : alias;
    }

    void addCommands(List<AbstractCommand> commands) {
        commands.forEach(this::addCommand);
    }

    void addCommand(AbstractCommand command) {
        componentService.registerComponent(command.getName(), ComponentType.COMMAND);
        commandHashMap.put(command.getName(), command);
    }

    void registerAliases(List<AbstractCommand> commands) {
        for (var command : commands) {
            command.getAliases().forEach(alias -> commandAliasMap.put(alias, command.getName()));
            commandAliasMap.put(command.getName(), command.getName());
        }
    }

    public void parseEvent(SlashCommandEvent slashCommandEvent) {
        SlashCommand slashCommand = slashCommandMap.get(slashCommandEvent.getName());
        if (slashCommand != null) {
            slashCommand.execute(slashCommandEvent);
        }
    }

    public void addSlashCommands(List<SlashCommand> slashCommands) {
        slashCommands.forEach(this::addSlashCommand);
    }

    public void addSlashCommand(SlashCommand slashCommand) {
        slashCommandMap.put(slashCommand.getName(), slashCommand);
    }
}
