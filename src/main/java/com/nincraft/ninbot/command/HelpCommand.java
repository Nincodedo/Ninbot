package com.nincraft.ninbot.command;

import com.nincraft.ninbot.command.util.CommandParser;
import com.nincraft.ninbot.util.MessageUtils;
import lombok.val;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class HelpCommand extends AbstractCommand {

    public HelpCommand() {
        length = 2;
        name = "help";
        description = "Displays this awesome message";
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        val map = CommandParser.getCommandHashMap();
        StringBuilder stringBuilder = new StringBuilder();
        for (val key : map.keySet()) {
            if (!map.get(key).isHidden()) {
                stringBuilder.append(map.get(key));
                stringBuilder.append("\n");
            }
        }
        MessageUtils.sendMessage(event.getChannel(), stringBuilder.toString());
    }
}
