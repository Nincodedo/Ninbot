package com.nincraft.ninbot.components.admin;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.config.Config;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
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
        description = "Config commands";
        permissionLevel = RolePermission.ADMIN;
        this.configService = configService;
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(event.getMessage().getContentStripped())) {
            case "add":
                if (getCommandLength(message) >= 5) {
                    addConfig(message, event.getGuild().getId(), event.getMessage());
                } else {
                    messageUtils.reactUnsuccessfulResponse(event.getMessage());
                }
                break;
            case "remove":
                if (getCommandLength(message) >= 5) {
                    removeConfig(message, event.getGuild().getId(), event.getMessage());
                } else {
                    messageUtils.reactUnsuccessfulResponse(event.getMessage());
                }
                break;
            case "list":
                listConfigs(event);
                break;
            default:
                messageUtils.reactUnknownResponse(event.getMessage());
                break;
        }
    }

    private void listConfigs(MessageReceivedEvent event) {
        val configsByServerId = configService.getConfigsByServerId(event.getGuild().getId());
        val serverName = event.getGuild().getName();
        if (configsByServerId.isEmpty()) {
            messageUtils.sendMessage(event.getChannel(), "No configs found for server %s", serverName);
            return;
        }
        MessageBuilderHelper messageBuilder = new MessageBuilderHelper();
        messageBuilder.setTitle("Configs for " + serverName);
        for (val config : configsByServerId) {
            messageBuilder.addField(config.getName(), config.getValue(), false);
        }
        messageUtils.sendMessage(event.getChannel(), messageBuilder.build());
    }

    private void removeConfig(String messageString, String guildId, Message message) {
        Config config = new Config(guildId, messageString.split("\\s+")[3], messageString.split("\\s+")[4]);
        configService.removeConfig(config);
        messageUtils.reactSuccessfulResponse(message);
    }

    private void addConfig(String messageString, String guildId, Message message) {
        Config config = new Config(guildId, messageString.split("\\s+")[3], messageString.split("\\s+")[4]);
        val isSuccessful = configService.addConfig(config);
        messageUtils.reactAccordingly(message, isSuccessful);
    }
}
