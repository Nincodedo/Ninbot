package dev.nincodedo.ninbot.components.command;

import dev.nincodedo.ninbot.components.common.Constants;
import dev.nincodedo.ninbot.components.common.LocaleService;
import dev.nincodedo.ninbot.components.common.RolePermission;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import dev.nincodedo.ninbot.components.common.message.WebhookHelper;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.stats.StatCategory;
import dev.nincodedo.ninbot.components.stats.StatManager;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.List;
import java.util.*;

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
    @Autowired
    protected ConfigService configService;
    @Autowired
    protected StatManager statManager;

    void execute(MessageReceivedEvent event, Locale serverLocale) {
        var message = event.getMessage().getContentStripped();
        resourceBundle = LocaleService.getResourceBundleOrDefault(serverLocale);
        if (event.isFromGuild() && userHasPermission(event.getGuild(), event.getAuthor(), permissionLevel)) {
            log.trace("Executing command {} by {} in server {}: {}", name, event.getAuthor()
                    .getId(), event.getGuild().getId(), message);
            if (getSubcommand(message).equalsIgnoreCase("help")) {
                statManager.addOneCount(name, StatCategory.COMMAND_HELP, event.getGuild().getId());
                displayHelp(event).executeActions();
            } else {
                statManager.addOneCount(name, StatCategory.COMMAND, event.getGuild().getId());
                executeCommand(event).executeActions();
            }
        } else if (!event.isFromGuild()) {
            log.warn("User executed command from outside of a Guild. Name: {}, Channel ID: {}", event.getAuthor()
                    .getId(), event.getChannel().getId());
            event.getChannel().sendMessage("Ninbot only processes commands on servers").queue();
        } else {
            log.debug("User {} does not have permission to run {} on server {}: {}", event.getAuthor()
                    .getId(), name, event.getGuild().getId(), message);
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
                + ".help.title")), help, false)
                .setColor(Color.BLUE);
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
            var member = guild.getMember(user);
            var configuredRole = configService.getSingleValueByName(guild.getId(),
                    "roleRank-" + rolePermission.getRoleName());
            var roles =
                    configuredRole.map(configuredRoleId ->
                            Collections.singletonList(guild.getRoleById(configuredRoleId)))
                            .orElseGet(() -> guild.getRolesByName(rolePermission.getRoleName(), true));
            return guild.getMembersWithRoles(roles).contains(member);
        }
    }

    protected abstract MessageAction executeCommand(MessageReceivedEvent event);

    protected boolean isCommandLengthCorrect(String content) {
        var commandLength = getCommandLength(content);
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

    /**
     * Returns a lowercase subcommand based on array index position, starts at 0
     *
     * @param command  full command message
     * @param position index position from command
     * @return lowercase subcommand
     */
    protected String getSubcommand(String command, int position) {
        if (getCommandLength(command) >= position + 1) {
            return command.split("\\s+")[position].toLowerCase();
        } else {
            return "";
        }
    }

    /**
     * Returns a subcommand based on array index position, starts at 0
     *
     * @param command  full command message
     * @param position index position from command
     * @return subcommand
     */
    protected String getSubcommandNoTransform(String command, int position) {
        if (getCommandLength(command) >= position + 1) {
            return command.split("\\s+")[position];
        } else {
            return "";
        }
    }

    /**
     * Returns true if the user is a Patreon supporter (specifically if they are in the Ninbot Patreon Discord)
     *
     * @param shardManager shardManager
     * @param user         user to check
     * @return true/false
     */
    protected boolean isUserNinbotSupporter(ShardManager shardManager, User user) {
        var guild = shardManager.getGuildById(Constants.NINBOT_SUPPORTERS_SERVER_ID);
        if (guild != null) {
            return guild.getMembers()
                    .stream()
                    .anyMatch(member -> member.getId().equals(user.getId()));
        } else {
            return false;
        }
    }
}
