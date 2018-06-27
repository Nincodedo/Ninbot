package com.nincraft.ninbot.components.command;

import com.nincraft.ninbot.components.common.MessageUtils;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
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

    private ConfigService configService;
    @Getter
    private Map<String, AbstractCommand> commandHashMap = new HashMap<>();

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
                    reportError(event, e);
                }
            } else {
                MessageUtils.reactUnknownResponse(event.getMessage());
            }
        }
    }

    private void reportError(MessageReceivedEvent event, Exception e) {
        val config = configService.getConfigByName(event.getGuild().getId(), ConfigConstants.ERROR_ANNOUNCE_CHANNEL);
        if (config.size() > 0) {
            MessageUtils.sendMessage(getChannel(event.getJDA(), event.getGuild().getId(), config.get(0).getValue()),
                    e.toString() +
                            "\n" + e.getStackTrace()[0].toString());
        }
    }

    private MessageChannel getChannel(JDA jda, String serverId, String channel) {
        return jda.getGuildById(serverId).getTextChannelById(channel);
    }

    private String getCommand(String message) {
        String[] splitMessage = message.split(" ");
        if (splitMessage.length > 1) {
            return splitMessage[1] != null ? splitMessage[1].toLowerCase() : StringUtils.EMPTY;
        }
        return null;
    }

    void addCommands(List<AbstractCommand> commands) {
        commands.forEach(this::addCommand);
    }

    void addCommand(AbstractCommand command) {
        commandHashMap.put(command.getName(), command);
    }
}
