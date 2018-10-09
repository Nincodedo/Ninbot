package com.nincraft.ninbot.components.owner;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.RolePermission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class OwnerCommand extends AbstractCommand {

    @Value("${logging.file}")
    private String ninbotLog;

    public OwnerCommand() {
        length = 3;
        name = "owner";
        description = "Owner commands";
        permissionLevel = RolePermission.OWNER;
    }

    @Override
    protected void executeCommand(MessageReceivedEvent event) {
        if (!event.getMessage().isFromType(ChannelType.PRIVATE)) {
            messageUtils.reactUnsuccessfulResponse(event.getMessage());
            return;
        }
        switch (getSubcommand(event.getMessage().getContentStripped())) {
            case "restart":
                restart(event.getMessage());
                break;
            case "logs":
                showLogs(event.getChannel());
                break;
            default:
                messageUtils.reactUnknownResponse(event.getMessage());
                break;
        }
    }

    private void restart(Message message) {
        messageUtils.reactSuccessfulResponse(message);
        System.exit(0);
    }

    private void showLogs(MessageChannel channel) {
        channel.sendFile(new File(ninbotLog)).queue();
    }
}
