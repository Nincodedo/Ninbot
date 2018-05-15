package com.nincraft.ninbot.components.admin;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.config.ConfigDao;
import com.nincraft.ninbot.util.MessageUtils;
import com.nincraft.ninbot.util.RolePermission;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class AdminCommand extends AbstractCommand {

    private final ConfigDao configDao;

    public AdminCommand(ConfigDao configDao) {
        length = 3;
        name = "admin";
        description = "Admin commands";
        permissionLevel = RolePermission.ADMIN;
        hidden = true;
        this.configDao = configDao;
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        switch (getSubcommand(event.getMessage().getContentStripped())) {
            case "restart":
                restart(event.getMessage());
                break;
            case "config":
                if (isCommandLengthCorrect(event.getMessage().getContentStripped(), 5)) {
                    setConfig(event);
                } else {
                    MessageUtils.reactUnsuccessfulResponse(event.getMessage());
                }
                break;
            default:
                break;
        }
    }

    private void setConfig(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        val configName = message.split(" ")[3];
        val configValue = message.split(" ")[4];
        val serverId = event.getGuild().getId();
        val isSuccessful = configDao.setConfig(serverId, configName, configValue);
        MessageUtils.reactAccordingly(event.getMessage(), isSuccessful);
    }

    private void restart(Message message) {
        MessageUtils.reactSuccessfulResponse(message);
        System.exit(0);
    }
}
