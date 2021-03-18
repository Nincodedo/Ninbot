package dev.nincodedo.ninbot.components.command;

import dev.nincodedo.ninbot.components.common.Constants;
import dev.nincodedo.ninbot.components.common.LocaleService;
import dev.nincodedo.ninbot.components.common.RolePermission;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import dev.nincodedo.ninbot.components.common.message.WebhookHelper;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.stats.StatCategory;
import dev.nincodedo.ninbot.components.stats.StatManager;
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

public abstract class AbstractCommand {

    private static final org.apache.logging.log4j.Logger log =
            org.apache.logging.log4j.LogManager.getLogger(AbstractCommand.class);
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

    public AbstractCommand() {
    }

    void execute(MessageReceivedEvent event, Locale serverLocale) {
        String message = event.getMessage().getContentStripped();
        resourceBundle = LocaleService.getResourceBundleOrDefault(serverLocale);
        if (event.isFromGuild() && userHasPermission(event.getGuild(), event.getAuthor(), permissionLevel)) {
            log.trace("Executing command {} by {} in server {}: {}", name, event.getAuthor().getId(), event.getGuild()
                    .getId(), message);
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
            new MessageAction(event).addUnsuccessfulReaction().executeActions();
        }
    }

    protected MessageAction displayHelp(MessageReceivedEvent event) {
        String help = resourceBundle.getString("command.help.description.label") + ": ";
        help += resourceBundle.containsKey(String.format("command.%s.help.text", name)) ?
                resourceBundle.getString(String
                .format("command.%s.help.text", name)) : getCommandDescription(name);
        if (usageText != null) {
            help += "\n" + resourceBundle.getString("command.help.usage.label") + ": ";
            help += usageText;
        }
        if (!aliases.isEmpty()) {
            help += "\n" + resourceBundle.getString("command.help.alias.label") + ": " + getAliasesString();
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.addField(String.format("%s %s", StringUtils.capitalize(name), resourceBundle.getString(
                "command" + ".help.title")), help, false).setColor(Color.BLUE);
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
            final net.dv8tion.jda.api.entities.Member member = guild.getMember(user);
            final java.util.Optional<java.lang.String> configuredRole =
                    configService.getSingleValueByName(guild.getId(),
                    "roleRank-" + rolePermission.getRoleName());
            final java.util.List<net.dv8tion.jda.api.entities.Role> roles =
                    configuredRole.map(configuredRoleId -> Collections
                    .singletonList(guild.getRoleById(configuredRoleId)))
                    .orElseGet(() -> guild.getRolesByName(rolePermission.getRoleName(), true));
            return guild.getMembersWithRoles(roles).contains(member);
        }
    }

    protected abstract MessageAction executeCommand(MessageReceivedEvent event);

    protected boolean isCommandLengthCorrect(String content) {
        final int commandLength = getCommandLength(content);
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
        final net.dv8tion.jda.api.entities.Guild guild =
                shardManager.getGuildById(Constants.NINBOT_SUPPORTERS_SERVER_ID);
        if (guild != null) {
            return guild.getMembers().stream().anyMatch(member -> member.getId().equals(user.getId()));
        } else {
            return false;
        }
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(final int length) {
        this.length = length;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public RolePermission getPermissionLevel() {
        return this.permissionLevel;
    }

    public void setPermissionLevel(final RolePermission permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public boolean isCheckExactLength() {
        return this.checkExactLength;
    }

    public void setCheckExactLength(final boolean checkExactLength) {
        this.checkExactLength = checkExactLength;
    }

    public String getUsageText() {
        return this.usageText;
    }

    public void setUsageText(final String usageText) {
        this.usageText = usageText;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public void setAliases(final List<String> aliases) {
        this.aliases = aliases;
    }

    public ResourceBundle getResourceBundle() {
        return this.resourceBundle;
    }

    public void setResourceBundle(final ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public WebhookHelper getWebhookHelper() {
        return this.webhookHelper;
    }

    public void setWebhookHelper(final WebhookHelper webhookHelper) {
        this.webhookHelper = webhookHelper;
    }

    public ConfigService getConfigService() {
        return this.configService;
    }

    public void setConfigService(final ConfigService configService) {
        this.configService = configService;
    }

    public StatManager getStatManager() {
        return this.statManager;
    }

    public void setStatManager(final StatManager statManager) {
        this.statManager = statManager;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof AbstractCommand)) return false;
        final AbstractCommand other = (AbstractCommand) o;
        if (!other.canEqual(this)) return false;
        if (this.getLength() != other.getLength()) return false;
        if (this.isCheckExactLength() != other.isCheckExactLength()) return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final java.lang.Object this$permissionLevel = this.getPermissionLevel();
        final java.lang.Object other$permissionLevel = other.getPermissionLevel();
        if (this$permissionLevel == null ?
                other$permissionLevel != null : !this$permissionLevel.equals(other$permissionLevel)) return false;
        final java.lang.Object this$usageText = this.getUsageText();
        final java.lang.Object other$usageText = other.getUsageText();
        if (this$usageText == null ? other$usageText != null : !this$usageText.equals(other$usageText)) return false;
        final java.lang.Object this$aliases = this.getAliases();
        final java.lang.Object other$aliases = other.getAliases();
        if (this$aliases == null ? other$aliases != null : !this$aliases.equals(other$aliases)) return false;
        final java.lang.Object this$resourceBundle = this.getResourceBundle();
        final java.lang.Object other$resourceBundle = other.getResourceBundle();
        if (this$resourceBundle == null ?
                other$resourceBundle != null : !this$resourceBundle.equals(other$resourceBundle)) return false;
        final java.lang.Object this$webhookHelper = this.getWebhookHelper();
        final java.lang.Object other$webhookHelper = other.getWebhookHelper();
        if (this$webhookHelper == null ? other$webhookHelper != null : !this$webhookHelper.equals(other$webhookHelper))
            return false;
        final java.lang.Object this$configService = this.getConfigService();
        final java.lang.Object other$configService = other.getConfigService();
        if (this$configService == null ? other$configService != null : !this$configService.equals(other$configService))
            return false;
        final java.lang.Object this$statManager = this.getStatManager();
        final java.lang.Object other$statManager = other.getStatManager();
        return this$statManager == null ? other$statManager == null : this$statManager.equals(other$statManager);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof AbstractCommand;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getLength();
        result = result * PRIME + (this.isCheckExactLength() ? 79 : 97);
        final java.lang.Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final java.lang.Object $permissionLevel = this.getPermissionLevel();
        result = result * PRIME + ($permissionLevel == null ? 43 : $permissionLevel.hashCode());
        final java.lang.Object $usageText = this.getUsageText();
        result = result * PRIME + ($usageText == null ? 43 : $usageText.hashCode());
        final java.lang.Object $aliases = this.getAliases();
        result = result * PRIME + ($aliases == null ? 43 : $aliases.hashCode());
        final java.lang.Object $resourceBundle = this.getResourceBundle();
        result = result * PRIME + ($resourceBundle == null ? 43 : $resourceBundle.hashCode());
        final java.lang.Object $webhookHelper = this.getWebhookHelper();
        result = result * PRIME + ($webhookHelper == null ? 43 : $webhookHelper.hashCode());
        final java.lang.Object $configService = this.getConfigService();
        result = result * PRIME + ($configService == null ? 43 : $configService.hashCode());
        final java.lang.Object $statManager = this.getStatManager();
        result = result * PRIME + ($statManager == null ? 43 : $statManager.hashCode());
        return result;
    }
}
