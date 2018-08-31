package com.nincraft.ninbot.components.command;

import com.nincraft.ninbot.components.common.MessageUtils;
import com.nincraft.ninbot.components.common.RolePermission;
import lombok.Data;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
@Data
public abstract class AbstractCommand {

    protected int length;
    protected String description;
    protected String name;
    protected boolean hidden;
    protected RolePermission permissionLevel = RolePermission.EVERYONE;
    protected boolean checkExactLength = true;
    protected String helpText;
    @Autowired
    @Setter
    protected MessageUtils messageUtils;

    void execute(MessageReceivedEvent event) {
        if (userHasPermission(event.getGuild(), event.getMember())) {
            val message = event.getMessage().getContentStripped();
            log.info("Executing command {} by {}: {}", name, event.getAuthor().getName(), message);
            if (getSubcommand(message).equalsIgnoreCase("help")) {
                displayHelp(event);
            } else {
                executeCommand(event);
            }
        } else {
            messageUtils.reactUnsuccessfulResponse(event.getMessage());
        }
    }

    protected void displayHelp(MessageReceivedEvent event) {
        val help = helpText != null ? helpText : description;
        messageUtils.sendPrivateMessage(event.getAuthor(), help);
        messageUtils.reactSuccessfulResponse(event.getMessage());
    }

    @Override
    public String toString() {
        return name + ": " + description;
    }

    private boolean userHasPermission(Guild guild, Member member) {
        return userHasPermission(guild, member, this.permissionLevel);
    }

    protected boolean userHasPermission(Guild guild, Member member, RolePermission rolePermission) {
        if (RolePermission.EVERYONE.equals(rolePermission)) {
            return true;
        } else if (RolePermission.OWNER.equals(rolePermission)) {
            return member.getUser().getId().equals(RolePermission.OWNER.getRoleName());
        } else {
            val role = guild.getRolesByName(rolePermission.getRoleName(), true).get(0);
            return guild.getMembersWithRoles(role).contains(member);
        }
    }

    protected abstract void executeCommand(MessageReceivedEvent event);

    protected boolean isCommandLengthCorrect(String content, int length) {
        val commandLength = getCommandLength(content);
        if (checkExactLength) {
            return commandLength == length;
        } else {
            return commandLength >= length;
        }
    }

    protected boolean isCommandLengthCorrect(String content) {
        val commandLength = getCommandLength(content);
        if (checkExactLength) {
            return commandLength == length;
        } else {
            return commandLength >= length;
        }
    }

    private int getCommandLength(String content) {
        return content.split(" ").length;
    }

    protected void wrongCommandLengthMessage(MessageChannel channel) {
        messageUtils.sendMessage(channel, "Wrong number of arguments for %s command", name);
    }

    protected String getSubcommand(String command) {
        if (getCommandLength(command) >= 3) {
            return command.split(" ")[2].toLowerCase();
        } else {
            return "";
        }
    }
}
