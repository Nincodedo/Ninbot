package com.nincraft.ninbot.components.admin;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class AdminCommand extends AbstractCommand {

    private ConfigService configService;

    public AdminCommand(ConfigService configService) {
        length = 3;
        name = "admin";
        description = "Admin commands";
        permissionLevel = RolePermission.ADMIN;
        this.configService = configService;
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        switch (getSubcommand(event.getMessage().getContentStripped())) {
            case "config-add":
                if (isCommandLengthCorrect(event.getMessage().getContentStripped(), 5)) {
                    addConfig(event);
                } else {
                    messageUtils.reactUnsuccessfulResponse(event.getMessage());
                }
                break;
            case "config-remove":
                if (isCommandLengthCorrect(event.getMessage().getContentStripped(), 5)) {
                    removeConfig(event);
                } else {
                    messageUtils.reactUnsuccessfulResponse(event.getMessage());
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
        messageUtils.reactSuccessfulResponse(event.getMessage());
    }

    private void addConfig(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        val configName = message.split(" ")[3];
        val configValue = message.split(" ")[4];
        val serverId = event.getGuild().getId();
        val isSuccessful = configService.addConfig(serverId, configName, configValue);
        messageUtils.reactAccordingly(event.getMessage(), isSuccessful);
    }
}
