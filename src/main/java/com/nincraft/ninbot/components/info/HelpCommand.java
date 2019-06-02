package com.nincraft.ninbot.components.info;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
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
        this.commandMap = commandMap;
    }

    @Override
    public CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(resourceBundle.getString("command.help.message.title"))
                .setColor(Color.BLUE);
        List<String> keyList = new ArrayList<>(commandMap.keySet());
        Collections.sort(keyList);
        keyList.stream().filter(commandName -> userHasPermission(event.getGuild(), event.getAuthor(), commandMap.get(commandName).getPermissionLevel()))
                .forEach(commandName -> {
                    val command = commandMap.get(commandName);
                    command.setResourceBundle(resourceBundle);
                    embedBuilder.addField(commandName, command.getCommandDescription(commandName), false);
                });
        embedBuilder.setFooter(resourceBundle.getString("command.help.message.footer"), null);
        commandResult.addPrivateMessageAction(embedBuilder.build())
                .addSuccessfulReaction();
        return commandResult;
    }
}
