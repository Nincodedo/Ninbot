package com.nincraft.ninbot.components.info;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HelpCommand extends AbstractCommand {

    private Map<String, AbstractCommand> commandMap;

    public HelpCommand(Map<String, AbstractCommand> commandMap) {
        length = 2;
        name = "help";
        description = "Displays this awesome message";
        this.commandMap = commandMap;
    }

    @Override
    public CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        MessageBuilderHelper messageBuilder = new MessageBuilderHelper();
        messageBuilder.setTitle("List of commands");
        messageBuilder.setColor(Color.BLUE);
        List<String> keyList = new ArrayList<>(commandMap.keySet());
        Collections.sort(keyList);
        keyList.stream().filter(key -> userHasPermission(event.getGuild(), event.getAuthor(), commandMap.get(key).getPermissionLevel()))
                .forEach(key -> messageBuilder.addField(key, commandMap.get(key).getDescription(), false));
        messageBuilder.setFooter("Use \"help\" at the end of any command to get more information about it", null);
        commandResult.addPrivateMessageAction(messageBuilder.build());
        commandResult.addSuccessfulReaction();
        return commandResult;
    }
}
