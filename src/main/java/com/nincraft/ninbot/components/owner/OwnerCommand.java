package com.nincraft.ninbot.components.owner;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.RolePermission;
import net.dv8tion.jda.core.entities.ChannelType;
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
    protected CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        if (!event.getMessage().isFromType(ChannelType.PRIVATE)) {
            return commandResult.addUnsuccessfulReaction();
        }
        switch (getSubcommand(event.getMessage().getContentStripped())) {
            case "restart":
                commandResult.addSuccessfulReaction();
                restart();
                break;
            case "logs":
                showLogs(event.getChannel());
                break;
            default:
                commandResult.addUnknownReaction();
                break;
        }
        return commandResult;
    }

    private void restart() {
        System.exit(0);
    }

    private void showLogs(MessageChannel channel) {
        channel.sendFile(new File(ninbotLog)).queue();
    }
}
