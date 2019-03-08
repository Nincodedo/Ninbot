package com.nincraft.ninbot.components.command;

import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
class CommandParser {

    private static final String QUESTION_MARK = "\u2754";
    private ConfigService configService;
    @Getter
    private Map<String, AbstractCommand> commandHashMap = new HashMap<>();
    private Map<String, String> commandAliasMap = new HashMap<>();

    @Autowired
    CommandParser(ConfigService configService) {
        this.configService = configService;
    }

    void parseEvent(MessageReceivedEvent event) {
        String message = event.getMessage().getContentStripped();
        if (StringUtils.isNotBlank(message)) {
            AbstractCommand command = commandHashMap.get(getCommand(message));
            if (command != null) {
                try {
                    command.execute(event);
                } catch (Exception e) {
                    log.error("Error executing command " + command.getName(), e);
                }
            } else {
                val channelList = configService.getValuesByName(event.getGuild().getId(), ConfigConstants.CONVERSATION_CHANNELS);
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
        commandHashMap.put(command.getName(), command);
    }

    void registerAliases(List<AbstractCommand> commands) {
        for (val command : commands) {
            command.getAliases().forEach(alias -> commandAliasMap.put(alias, command.getName()));
            commandAliasMap.put(command.getName(), command.getName());
        }
    }
}
