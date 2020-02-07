package com.nincraft.ninbot.components.command;

import com.nincraft.ninbot.components.common.MessageAction;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.common.WebhookHelper;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Log4j2
@Data
public abstract class AbstractCommand {

    protected int length;
    protected String name;
    protected RolePermission permissionLevel = RolePermission.EVERYONE;
    protected boolean checkExactLength = true;
    protected String usageText;
    protected List<String> aliases = new ArrayList<>();
    protected ResourceBundle resourceBundle = ResourceBundle.getBundle("lang", Locale.ENGLISH);
    protected WebhookHelper webhookHelper = new WebhookHelper();

    void execute(MessageReceivedEvent event, Locale serverLocale) {
        val message = event.getMessage().getContentStripped();
        resourceBundle = ResourceBundle.getBundle("lang", serverLocale);
        if (event.isFromGuild() && userHasPermission(event.getGuild(), event.getAuthor(), permissionLevel)) {
            log.info("Executing command {} by {} in server {}: {}", name, event.getAuthor()
                    .getName(), event.getGuild().getId(), message);
            if (getSubcommand(message).equalsIgnoreCase("help")) {
                displayHelp(event).executeActions();
            } else {
                executeCommand(event).executeActions();
            }
        } else if (!event.isFromGuild()) {
            log.warn("User executed command from outside of a Guild. Name: {}, Channel ID: {}", event.getAuthor()
                    .getName(), event.getChannel().getId());
            event.getChannel().sendMessage("Ninbot only processes commands on servers").queue();
        } else {
            log.debug("User {} does not have permission to run {} on server {}: {}", event.getAuthor()
                    .getName(), name, event.getGuild().getId(), message);
            new MessageAction(event)
                    .addUnsuccessfulReaction()
                    .executeActions();
        }
    }

    protected MessageAction displayHelp(MessageReceivedEvent event) {
        String help = resourceBundle.getString("command.help.description.label") + ": ";
        help += resourceBundle.containsKey(String.format("command.%s.help.text", name)) ? resourceBundle.getString(
                String.format("command.%s.help.text", name)) : getCommandDescription(name);
        if (usageText != null) {
            help += "\n" + resourceBundle.getString("command.help.usage.label") + ": ";
            help += usageText;
        }
        if (!aliases.isEmpty()) {
            help += "\n" + resourceBundle.getString("command.help.alias.label") + ": " + getAliasesString();
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.addField(String.format("%s %s", StringUtils.capitalize(name), resourceBundle.getString("command"
                + ".help.title")), help, false);
        MessageAction messageAction = new MessageAction(event);
        messageAction.addPrivateMessageAction(embedBuilder.build());
        messageAction.addSuccessfulReaction();
        return messageAction;
    }

    public String getCommandDescription(String name) {
        return resourceBundle.getString(String.format("command.%s.description.text", name));
    }

    private String getAliasesString() {
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
        return name + ": " + getCommandDescription(name);
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

    protected abstract MessageAction executeCommand(MessageReceivedEvent event);

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
        return getSubcommand(command, 2);
    }

    protected String getSubcommand(String command, int position) {
        if (getCommandLength(command) >= position + 1) {
            return command.split("\\s+")[position].toLowerCase();
        } else {
            return "";
        }
    }
}
