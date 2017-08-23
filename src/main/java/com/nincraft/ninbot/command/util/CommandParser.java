package com.nincraft.ninbot.command.util;

import com.nincraft.ninbot.Ninbot;
import com.nincraft.ninbot.command.*;
import com.nincraft.ninbot.util.MessageUtils;
import com.nincraft.ninbot.util.Reference;
import lombok.Getter;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CommandParser {

    @Getter
    private static final CommandParser instance = new CommandParser();
    @Getter
    private Map<String, AbstractCommand> commandHashMap;

    public CommandParser() {
        commandHashMap = new HashMap<>();
        commandHashMap.put("subscribe", new SubscribeCommand());
        commandHashMap.put("unsubscribe", new UnsubscribeCommand());
        commandHashMap.put("list", new ListCommand());
        commandHashMap.put("help", new HelpCommand());
        commandHashMap.put("events", new EventCommand());
        commandHashMap.put("stats", new StatsCommand());
        commandHashMap.put("admin", new AdminCommand());
    }

    public void parseEvent(MessageReceivedEvent event) {
        String message = event.getMessage().getContent();
        if (StringUtils.isNotBlank(message)) {
            AbstractCommand command = commandHashMap.get(getCommand(message));
            if (command != null) {
                try {
                    command.execute(event);
                } catch (Exception e) {
                    MessageUtils.sendMessage(getChannel(Reference.OCW_DEBUG_CHANNEL), e.toString() +
                            "\n" + e.getStackTrace()[0].toString());
                }
            }
        }
    }

    private MessageChannel getChannel(String channel) {
        return Ninbot.getJda().getGuildById(Reference.OCW_SERVER_ID).getTextChannelById(channel);
    }

    private String getCommand(String message) {
        String[] splitMessage = message.split(" ");
        if (splitMessage.length > 1) {
            return splitMessage[1] != null ? splitMessage[1].toLowerCase() : StringUtils.EMPTY;
        }
        return null;
    }
}
