package com.nincraft.ninbot.util;

import com.nincraft.ninbot.command.ICommand;
import com.nincraft.ninbot.command.SubscribeCommand;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class CommandParser {

    private HashMap<String, ICommand> commandHashMap;

    public CommandParser() {
        commandHashMap = new HashMap<>();
        commandHashMap.put("subscribe", new SubscribeCommand());
    }

    public void parseEvent(MessageReceivedEvent event) {
        String message = event.getMessage().getContent();
        if (StringUtils.isNotBlank(message)) {
            ICommand command = commandHashMap.get(getCommand(message));
            if (command != null) {
                command.execute(event);
            }
        }
    }

    private String getCommand(String message) {
        if (message.split(" ").length > 1) {
            return message.split(" ")[1] != null ? message.split(" ")[1] : StringUtils.EMPTY;
        }
        return null;
    }
}
