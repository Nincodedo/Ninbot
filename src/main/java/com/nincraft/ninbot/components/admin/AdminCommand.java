package com.nincraft.ninbot.components.admin;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.MessageUtils;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class AdminCommand extends AbstractCommand {

    private ConfigService configService;

    public AdminCommand(ConfigService configService) {
        length = 3;
        name = "admin";
        description = "Admin commands";
        permissionLevel = RolePermission.ADMIN;
        hidden = true;
        this.configService = configService;
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        switch (getSubcommand(event.getMessage().getContentStripped())) {
            case "restart":
                restart(event.getMessage());
                break;
            case "config-add":
                if (isCommandLengthCorrect(event.getMessage().getContentStripped(), 5)) {
                    addConfig(event);
                } else {
                    MessageUtils.reactUnsuccessfulResponse(event.getMessage());
                }
                break;
            case "config-remove":
                if (isCommandLengthCorrect(event.getMessage().getContentStripped(), 5)) {
                    removeConfig(event);
                } else {
                    MessageUtils.reactUnsuccessfulResponse(event.getMessage());
                }
                break;
            default:
                break;
        }
    }

    private void removeConfig(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        val configName = message.split(" ")[3];
        val configValue = message.split(" ")[4];
        val serverId = event.getGuild().getId();
        configService.removeConfig(serverId, configName, configValue);
        MessageUtils.reactSuccessfulResponse(event.getMessage());
    }

    private void addConfig(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        val configName = message.split(" ")[3];
        val configValue = message.split(" ")[4];
        val serverId = event.getGuild().getId();
        val isSuccessful = configService.addConfig(serverId, configName, configValue);
        MessageUtils.reactAccordingly(event.getMessage(), isSuccessful);
    }

    private void restart(Message message) {
        MessageUtils.reactSuccessfulResponse(message);
        System.exit(0);
    }
}
