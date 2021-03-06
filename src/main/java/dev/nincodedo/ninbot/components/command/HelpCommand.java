package dev.nincodedo.ninbot.components.command;

import dev.nincodedo.ninbot.components.common.message.MessageAction;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.stats.StatManager;
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

    public HelpCommand(Map<String, AbstractCommand> commandMap, ComponentService componentService,
            ConfigService configService, StatManager statManager) {
        length = 2;
        name = "help";
        this.commandMap = commandMap;
        this.componentService = componentService;
        this.configService = configService;
        this.statManager = statManager;
    }

    @Override
    public MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
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
        messageAction.addPrivateMessageAction(embedBuilder.build())
                .addSuccessfulReaction();
        return messageAction;
    }
}
