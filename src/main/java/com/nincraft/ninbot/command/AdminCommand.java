package com.nincraft.ninbot.command;

import com.nincraft.ninbot.util.RolePermission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class AdminCommand extends AbstractCommand {

    public AdminCommand() {
        commandLength = 3;
        commandName = "admin";
        commandDescription = "Admin commands";
        commandPermission = RolePermission.ADMIN;
        hidden = true;
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        switch (getSubcommand(event.getMessage().getContent())) {
            case "restart":
                restart();
                break;
            default:
                break;
        }
    }

    private void restart() {
        System.exit(0);
    }
}
