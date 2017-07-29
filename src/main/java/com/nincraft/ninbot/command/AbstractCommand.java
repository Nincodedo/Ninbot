package com.nincraft.ninbot.command;

import com.nincraft.ninbot.util.MessageSenderHelper;
import com.nincraft.ninbot.util.RolePermission;
import lombok.Data;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

@Data
public abstract class AbstractCommand {

    int commandLength;
    String commandDescription;
    String commandName;
    boolean hidden;
    RolePermission commandPermission = RolePermission.EVERYONE;

    public void execute(MessageReceivedEvent event) {
        if (userHasPermission(event.getGuild(), event.getMember())) {
            executeCommand(event);
        }
    }

    @Override
    public String toString() {
        return commandName + ": " + commandDescription;
    }

    private boolean userHasPermission(Guild guild, Member member) {
        val role = guild.getRolesByName(commandPermission.getRoleName(), true).get(0);
        return guild.getMembersWithRoles(role).contains(member);
    }

    public abstract void executeCommand(MessageReceivedEvent event);

    boolean isCommandLengthCorrect(String content) {
        return content.split(" ").length == commandLength;
    }

    void wrongCommandLengthMessage(MessageChannel channel) {
        MessageSenderHelper.sendMessage(channel, "Wrong number of arguments for %s command", commandName);
    }

    String getSubcommand(String command) {
        return command.split(" ")[2].toLowerCase();
    }
}
