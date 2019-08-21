package com.nincraft.ninbot.components.info;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.config.component.ComponentService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.*;

@Log4j2
public class HelpCommand extends AbstractCommand {

    private Map<String, AbstractCommand> commandMap;
    private ComponentService componentService;

    public HelpCommand(Map<String, AbstractCommand> commandMap,
            ComponentService componentService) {
        length = 2;
        name = "help";
        this.commandMap = commandMap;
        this.componentService = componentService;
    }

    @Override
    public CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(resourceBundle.getString("command.help.message.title"))
                .setColor(Color.BLUE);
        List<String> keyList = new ArrayList<>(commandMap.keySet());
        Collections.sort(keyList);
        keyList.stream()
                .filter(commandName -> userHasPermission(event.getGuild(), event.getAuthor(),
                        commandMap.get(commandName)
                                .getPermissionLevel()))
                .filter(commandName -> !componentService.isDisabled(commandName, event.getGuild().getId()))
                .forEach(commandName -> {
                    val command = commandMap.get(commandName);
                    command.setResourceBundle(resourceBundle);
                    try {
                        embedBuilder.addField(commandName, command.getCommandDescription(commandName), false);
                    } catch (MissingResourceException e) {
                        log.error("Missing command description text for command " + commandName, e);
                    }
                });
        embedBuilder.setFooter(resourceBundle.getString("command.help.message.footer"), null);
        commandResult.addPrivateMessageAction(embedBuilder.build())
                .addSuccessfulReaction();
        return commandResult;
    }
}
