package com.nincraft.ninbot.components.owner;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.MessageUtils;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class OwnerCommand extends AbstractCommand {

    private ConfigService configService;

    public OwnerCommand(ConfigService configService) {
        length = 3;
        name = "owner";
        description = "Owner commands";
        permissionLevel = RolePermission.OWNER;
        hidden = true;
        this.configService = configService;
    }

    @Override
    protected void executeCommand(MessageReceivedEvent event) {
        switch (getSubcommand(event.getMessage().getContentStripped())) {
            case "restart":
                restart(event.getMessage());
                break;
            case "logs":
                showLogs(event);
                break;
            default:
                break;
        }
    }

    private void restart(Message message) {
        MessageUtils.reactSuccessfulResponse(message);
        System.exit(0);
    }

    private void showLogs(MessageReceivedEvent event) {
        val announceChannelId = configService.getSingleValueByName(event.getGuild().getId(), ConfigConstants.ERROR_ANNOUNCE_CHANNEL);
        if (announceChannelId.isPresent()) {
            val announceChannel = event.getJDA().getTextChannelById(announceChannelId.get());
            announceChannel.sendFile(new File("ninbot.log")).queue();
        }
    }
}
