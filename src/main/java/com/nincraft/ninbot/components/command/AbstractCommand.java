package com.nincraft.ninbot.components.command;

import com.nincraft.ninbot.util.MessageUtils;
import com.nincraft.ninbot.util.RolePermission;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

@Log4j2
@Data
public abstract class AbstractCommand {

    protected int length;
    protected String description;
    protected String name;
    protected boolean hidden;
    protected RolePermission permissionLevel = RolePermission.EVERYONE;
    protected boolean checkExactLength = true;

    void execute(MessageReceivedEvent event) {
        if (userHasPermission(event.getGuild(), event.getMember())) {
            log.info("Executing command {}", name);
            executeCommand(event);
        } else {
            MessageUtils.reactUnsuccessfulResponse(event.getMessage());
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

    protected boolean isCommandLengthCorrect(String content) {
        val commandLength = getCommandLength(content);
        if (checkExactLength) {
            return commandLength == length;
        } else {
            return commandLength >= length;
        }
    }

    protected int getCommandLength(String content) {
        return content.split(" ").length;
    }

    protected void wrongCommandLengthMessage(MessageChannel channel) {
        MessageUtils.sendMessage(channel, "Wrong number of arguments for %s command", name);
    }

    protected String getSubcommand(String command) {
        return command.split(" ")[2].toLowerCase();
    }
}
