package com.nincraft.ninbot.components.command;

import com.nincraft.ninbot.components.common.MessageUtils;
import com.nincraft.ninbot.components.common.RolePermission;
import lombok.Data;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Data
public abstract class AbstractCommand {

    protected int length;
    protected String description;
    protected String name;
    protected RolePermission permissionLevel = RolePermission.EVERYONE;
    protected boolean checkExactLength = true;
    protected String helpText;
    protected String usageText;
    protected List<String> aliases = new ArrayList<>();
    @Autowired
    @Setter
    protected MessageUtils messageUtils;

    void execute(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        if (userHasPermission(event.getGuild(), event.getAuthor())) {
            log.info("Executing command {} by {}: {}", name, event.getAuthor().getName(), message);
            if (getSubcommand(message).equalsIgnoreCase("help")) {
                displayHelp(event);
            } else {
                executeCommand(event);
            }
        } else {
            log.debug("User {} does not have permission to run {}: {}", event.getAuthor().getName(), name, message);
            messageUtils.reactUnsuccessfulResponse(event.getMessage());
        }
    }

    protected void displayHelp(MessageReceivedEvent event) {
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        String help = "Description: ";
        help += helpText != null ? helpText : description;
        if (usageText != null) {
            help += "\nUsage: ";
            help += usageText;
        }
        if (!aliases.isEmpty()) {
            help += "\nCommand aliases: " + printAliases();
        }
        embedBuilder.addField(StringUtils.capitalize(name) + " Command Help", help, false);
        messageBuilder.setEmbed(embedBuilder.build());
        messageUtils.sendPrivateMessage(event.getAuthor(), messageBuilder.build());
        messageUtils.reactSuccessfulResponse(event.getMessage());
    }

    private String printAliases() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < aliases.size(); i++) {
            String alias = aliases.get(i);
            stringBuilder.append(alias);
            if (i + 1 != aliases.size()) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return name + ": " + description;
    }

    private boolean userHasPermission(Guild guild, User user) {
        return userHasPermission(guild, user, this.permissionLevel);
    }

    protected boolean userHasPermission(Guild guild, User user, RolePermission rolePermission) {
        if (RolePermission.EVERYONE.equals(rolePermission)) {
            return true;
        } else if (RolePermission.OWNER.equals(rolePermission)) {
            return user.getId().equals(RolePermission.OWNER.getRoleName());
        } else {
            val member = guild.getMember(user);
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

    protected int getCommandLength(String content) {
        return content.split(" ").length;
    }

    protected String getSubcommand(String command) {
        if (getCommandLength(command) >= 3) {
            return command.split(" ")[2].toLowerCase();
        } else {
            return "";
        }
    }
}
