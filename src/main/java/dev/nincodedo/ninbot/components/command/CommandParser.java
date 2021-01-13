package dev.nincodedo.ninbot.components.command;

import dev.nincodedo.ninbot.components.common.LocaleService;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.config.component.ComponentType;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static dev.nincodedo.ninbot.components.common.Emojis.QUESTION_MARK;

@Log4j2
@Component
public class CommandParser {

    private ConfigService configService;
    private ComponentService componentService;
    @Getter
    private Map<String, AbstractCommand> commandHashMap = new HashMap<>();
    private Map<String, String> commandAliasMap = new HashMap<>();
    private ExecutorService executorService;

    CommandParser(ConfigService configService, ComponentService componentService) {
        this.configService = configService;
        this.executorService = Executors.newCachedThreadPool(new NamedThreadFactory("command-parser"));
        this.componentService = componentService;
    }

    void parseEvent(MessageReceivedEvent event) {
        String message = event.getMessage().getContentStripped();
        if (StringUtils.isNotBlank(message)) {
            String commandName = getCommand(message);
            if (event.isFromGuild() && componentService.isDisabled(commandName, event.getGuild().getId())) {
                event.getMessage().addReaction(QUESTION_MARK).queue();
                return;
            }
            AbstractCommand command = commandHashMap.get(commandName);
            if (command != null) {
                try {
                    executorService.execute(() -> command.execute(event, LocaleService.getLocale(event)));
                } catch (Exception e) {
                    log.error("Error executing command " + command.getName(), e);
                }
            } else {
                val channelList = configService.getValuesByName(event.getGuild()
                        .getId(), ConfigConstants.CONVERSATION_CHANNELS);
                if (!channelList.contains(event.getChannel().getId())) {
                    event.getMessage().addReaction(QUESTION_MARK).queue();
                }
            }
        }
    }

    private String getCommand(String message) {
        String[] splitMessage = message.split("\\s+");
        if (splitMessage.length > 1) {
            val commandName = translateAlias(splitMessage[1]);
            return commandName != null ? commandName.toLowerCase() : StringUtils.EMPTY;
        }
        return null;
    }

    private String translateAlias(String alias) {
        val commandName = commandAliasMap.get(alias);
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
        for (val command : commands) {
            command.getAliases().forEach(alias -> commandAliasMap.put(alias, command.getName()));
            commandAliasMap.put(command.getName(), command.getName());
        }
    }
}
