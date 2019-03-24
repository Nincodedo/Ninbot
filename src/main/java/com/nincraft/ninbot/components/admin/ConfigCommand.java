package com.nincraft.ninbot.components.admin;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.config.Config;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class ConfigCommand extends AbstractCommand {

    private ConfigService configService;

    public ConfigCommand(ConfigService configService) {
        length = 3;
        name = "config";
        checkExactLength = false;
        permissionLevel = RolePermission.ADMIN;
        this.configService = configService;
    }

    @Override
    public CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(event.getMessage().getContentStripped())) {
            case "add":
                if (getCommandLength(message) >= 5) {
                    commandResult.addCorrectReaction(addConfig(message, event.getGuild().getId()));
                } else {
                    commandResult.addUnsuccessfulReaction();
                }
                break;
            case "remove":
                if (getCommandLength(message) >= 5) {
                    removeConfig(message, event.getGuild().getId());
                    commandResult.addSuccessfulReaction();
                } else {
                    commandResult.addUnsuccessfulReaction();
                }
                break;
            case "list":
                commandResult.addChannelAction(listConfigs(event));
                break;
            default:
                commandResult.addUnknownReaction();
                break;
        }
        return commandResult;
    }

    private Message listConfigs(MessageReceivedEvent event) {
        val configsByServerId = configService.getConfigsByServerId(event.getGuild().getId());
        val serverName = event.getGuild().getName();
        if (configsByServerId.isEmpty()) {
            return new MessageBuilder().appendFormat(resourceBundle.getString("command.config.noconfigfound"), serverName).build();
        }
        MessageBuilderHelper messageBuilder = new MessageBuilderHelper();
        messageBuilder.setTitle(resourceBundle.getString("command.config.list.title")+" " + serverName);
        for (val config : configsByServerId) {
            messageBuilder.addField(config.getName(), config.getValue(), false);
        }
        return messageBuilder.build();
    }

    private void removeConfig(String messageString, String guildId) {
        Config config = new Config(guildId, messageString.split("\\s+")[3], messageString.split("\\s+")[4]);
        configService.removeConfig(config);
    }

    private boolean addConfig(String messageString, String guildId) {
        Config config = new Config(guildId, messageString.split("\\s+")[3], messageString.split("\\s+")[4]);
        return configService.addConfig(config);
    }
}
