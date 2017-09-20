package com.nincraft.ninbot.command;

import com.nincraft.ninbot.util.MessageUtils;
import com.nincraft.ninbot.util.RolePermission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class AdminCommand extends AbstractCommand {

    public AdminCommand() {
        length = 3;
        name = "admin";
        description = "Admin commands";
        permissionLevel = RolePermission.ADMIN;
        hidden = true;
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        switch (getSubcommand(event.getMessage().getContent())) {
            case "restart":
                restart(event.getMessage());
                break;
            default:
                break;
        }
    }

    private void restart(Message message) {
        MessageUtils.reactSuccessfulResponse(message);
        System.exit(0);
    }
}
