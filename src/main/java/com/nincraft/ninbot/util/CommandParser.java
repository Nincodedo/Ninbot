package com.nincraft.ninbot.util;

import com.nincraft.ninbot.command.AbstractCommand;
import com.nincraft.ninbot.command.ListCommand;
import com.nincraft.ninbot.command.SubscribeCommand;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class CommandParser {

    private HashMap<String, AbstractCommand> commandHashMap;

    public CommandParser() {
        commandHashMap = new HashMap<>();
        commandHashMap.put("subscribe", new SubscribeCommand());
        commandHashMap.put("list", new ListCommand());
    }

    public void parseEvent(MessageReceivedEvent event) {
        String message = event.getMessage().getContent();
        if (StringUtils.isNotBlank(message)) {
            AbstractCommand command = commandHashMap.get(getCommand(message));
            if (command != null) {
                command.execute(event);
            }
        }
    }

    private String getCommand(String message) {
        String[] splitMessage = message.split(" ");
        if (splitMessage.length > 1) {
            return splitMessage[1] != null ? splitMessage[1].toLowerCase() : StringUtils.EMPTY;
        }
        return null;
    }
}
