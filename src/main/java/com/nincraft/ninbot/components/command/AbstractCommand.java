package com.nincraft.ninbot.components.command;

import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import com.nincraft.ninbot.components.common.RolePermission;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

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

    void execute(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        if (userHasPermission(event.getGuild(), event.getAuthor(), permissionLevel)) {
            log.info("Executing command {} by {}: {}", name, event.getAuthor().getName(), message);
            if (getSubcommand(message).equalsIgnoreCase("help")) {
                displayHelp(event).executeActions();
            } else {
                executeCommand(event).executeActions();
            }
        } else {
            log.debug("User {} does not have permission to run {}: {}", event.getAuthor().getName(), name, message);
            new CommandResult(event)
                    .addUnsuccessfulReaction()
                    .executeActions();
        }
    }

    protected CommandResult displayHelp(MessageReceivedEvent event) {
        MessageBuilderHelper messageBuilder = new MessageBuilderHelper();
        String help = "Description: ";
        help += helpText != null ? helpText : description;
        if (usageText != null) {
            help += "\nUsage: ";
            help += usageText;
        }
        if (!aliases.isEmpty()) {
            help += "\nCommand aliases: " + printAliases();
        }
        messageBuilder.addField(StringUtils.capitalize(name) + " Command Help", help, false);
        CommandResult commandResult = new CommandResult(event);
        commandResult.addPrivateMessageAction(messageBuilder.build());
        commandResult.addSuccessfulReaction();
        return commandResult;
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

    protected boolean userHasPermission(Guild guild, User user, RolePermission rolePermission) {
        if (RolePermission.EVERYONE.equals(rolePermission) || (RolePermission.ADMIN.equals(rolePermission)
                && guild.getOwner().getUser().equals(user))) {
            return true;
        } else {
            val member = guild.getMember(user);
            val role = guild.getRolesByName(rolePermission.getRoleName(), true).get(0);
            return guild.getMembersWithRoles(role).contains(member);
        }
    }

    protected abstract CommandResult executeCommand(MessageReceivedEvent event);

    protected boolean isCommandLengthCorrect(String content) {
        val commandLength = getCommandLength(content);
        if (checkExactLength) {
            return commandLength == length;
        } else {
            return commandLength >= length;
        }
    }

    protected int getCommandLength(String content) {
        return content.split("\\s+").length;
    }

    protected String getSubcommand(String command) {
        if (getCommandLength(command) >= 3) {
            return command.split("\\s+")[2].toLowerCase();
        } else {
            return "";
        }
    }
}
