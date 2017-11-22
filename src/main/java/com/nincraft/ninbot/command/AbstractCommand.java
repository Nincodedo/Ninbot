package com.nincraft.ninbot.command;

import com.nincraft.ninbot.util.MessageUtils;
import com.nincraft.ninbot.util.RolePermission;
import lombok.Data;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

@Data
public abstract class AbstractCommand {

    int length;
    String description;
    String name;
    boolean hidden;
    RolePermission permissionLevel = RolePermission.EVERYONE;
    boolean checkExactLength = true;

    public void execute(MessageReceivedEvent event) {
        if (userHasPermission(event.getGuild(), event.getMember())) {
            executeCommand(event);
        } else {
            MessageUtils.sendMessage(event.getChannel(), "Insufficient privileges for %s command, %s required", name, permissionLevel.toString());
        }
    }

    @Override
    public String toString() {
        return name + ": " + description;
    }

    private boolean userHasPermission(Guild guild, Member member) {
        if (RolePermission.EVERYONE.equals(permissionLevel)) {
            return true;
        }
        val role = guild.getRolesByName(permissionLevel.getRoleName(), true).get(0);
        return guild.getMembersWithRoles(role).contains(member);
    }

    public abstract void executeCommand(MessageReceivedEvent event);

    boolean isCommandLengthCorrect(String content) {
        val commandLength = content.split(" ").length;
        if (checkExactLength) {
            return commandLength == length;
        } else {
            return commandLength >= length;
        }
    }

    void wrongCommandLengthMessage(MessageChannel channel) {
        MessageUtils.sendMessage(channel, "Wrong number of arguments for %s command", name);
    }

    String getSubcommand(String command) {
        return command.split(" ")[2].toLowerCase();
    }
}
