package com.nincraft.ninbot.components.info;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.MessageUtils;
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
    private MessageUtils messageUtils;

    public HelpCommand(Map<String, AbstractCommand> commandMap,
            MessageUtils messageUtils) {
        length = 2;
        name = "help";
        description = "Displays this awesome message";
        this.commandMap = commandMap;
        this.messageUtils = messageUtils;
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("List of commands");
        embedBuilder.setColor(Color.BLUE);
        List<String> keyList = new ArrayList<>(commandMap.keySet());
        Collections.sort(keyList);
        keyList.stream().filter(key -> userHasPermission(event.getGuild(), event.getMember(), commandMap.get(key).getPermissionLevel()))
                .forEach(key -> embedBuilder.addField(key, commandMap.get(key).getDescription(), false));
        embedBuilder.setFooter("Use \"help\" at the end of any command to get more information about it", null);
        messageBuilder.setEmbed(embedBuilder.build());
        messageUtils.sendPrivateMessage(event.getAuthor(), messageBuilder.build());
        messageUtils.reactSuccessfulResponse(event.getMessage());
    }
}
