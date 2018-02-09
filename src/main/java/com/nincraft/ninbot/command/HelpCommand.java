package com.nincraft.ninbot.command;

import com.nincraft.ninbot.command.util.CommandParser;
import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.util.MessageUtils;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HelpCommand extends AbstractCommand {

    public HelpCommand() {
        length = 2;
        name = "help";
        description = "Displays this awesome message";
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        val map = CommandParser.getCommandHashMap();
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("List of commands");
        embedBuilder.setColor(Color.BLUE);
        List<String> keyList = new ArrayList<>(map.keySet());
        Collections.sort(keyList);
        for (val key : keyList) {
            if (!map.get(key).isHidden()) {
                embedBuilder.addField(key, map.get(key).getDescription(), false);
            }
        }
        messageBuilder.setEmbed(embedBuilder.build());
        MessageUtils.sendMessage(event.getChannel(), messageBuilder.build());
    }
}
