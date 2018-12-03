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
    private MessageUtils messageUtils;
    @Getter
    private Map<String, AbstractCommand> commandHashMap = new HashMap<>();
    private Map<String, String> commandAliasMap = new HashMap<>();

    @Autowired
    CommandParser(ConfigService configService, MessageUtils messageUtils) {
        this.configService = configService;
        this.messageUtils = messageUtils;
    }

    void parseEvent(MessageReceivedEvent event) {
        String message = event.getMessage().getContentStripped();
        if (StringUtils.isNotBlank(message)) {
            AbstractCommand command = commandHashMap.get(getCommand(message));
            if (command != null) {
                try {
                    event.getChannel().sendTyping().queue();
                    command.execute(event);
                } catch (Exception e) {
                    log.error("Error executing command " + command.getName(), e);
                    reportError(event, e);
                }
            } else {
                val channelList = configService.getValuesByName(event.getGuild().getId(), ConfigConstants.CONVERSATION_CHANNELS);
                if (!channelList.contains(event.getChannel().getId())) {
                    messageUtils.reactUnknownResponse(event.getMessage());
                }
            }
        }
    }

    private void reportError(MessageReceivedEvent event, Exception e) {
        val config = configService.getConfigByName(event.getGuild().getId(), ConfigConstants.ERROR_ANNOUNCE_CHANNEL);
        if (config.size() > 0) {
            messageUtils.sendMessage(getChannel(event.getJDA(), event.getGuild().getId(), config.get(0).getValue()),
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
