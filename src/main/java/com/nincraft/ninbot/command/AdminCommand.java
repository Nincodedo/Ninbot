package com.nincraft.ninbot.command;

import com.nincraft.ninbot.util.RolePermission;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
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
    public void execute(MessageReceivedEvent event) {
        if (userHasPermission(event.getGuild(), event.getMember())) {
            switch (getSubcommand(event.getMessage().getContent())) {
                case "restart":
                    restart();
                    break;
                default:
                    break;
            }
        }
    }

    private void restart() {
        System.exit(0);
    }

    private boolean userHasPermission(Guild guild, Member member) {
        val role = guild.getRolesByName(commandPermission.getRoleName(), true).get(0);
        return guild.getMembersWithRoles(role).contains(member);
    }
}
