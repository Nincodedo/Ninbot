package com.nincraft.ninbot.components.info;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandParser;
import com.nincraft.ninbot.util.MessageUtils;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HelpCommand extends AbstractCommand {

    private Map<String, AbstractCommand> commandMap;

    public HelpCommand(CommandParser commandParser) {
        length = 2;
        name = "help";
        description = "Displays this awesome message";
        commandMap = commandParser.getCommandHashMap();
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("List of commands");
        embedBuilder.setColor(Color.BLUE);
        List<String> keyList = new ArrayList<>(commandMap.keySet());
        Collections.sort(keyList);
        for (val key : keyList) {
            if (!commandMap.get(key).isHidden()) {
                embedBuilder.addField(key, commandMap.get(key).getDescription(), false);
            }
        }
        messageBuilder.setEmbed(embedBuilder.build());
        MessageUtils.sendMessage(event.getChannel(), messageBuilder.build());
    }
}
