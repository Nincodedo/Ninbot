package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.RolePermission;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import dev.nincodedo.ninbot.common.release.ReleaseStage;
import dev.nincodedo.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public interface SlashCommand extends ReleaseStage {

    Locale defaultLocale = Locale.ENGLISH;

    String getName();

    default boolean shouldCheckPermissions() {
        return false;
    }

    default RolePermission getRolePermission() {
        return RolePermission.EVERYONE;
    }

    default String getDescription() {
        return getDescription(defaultLocale);
    }

    default String getDescription(Locale locale) {
        return resourceBundle(locale).getString("command." + getName() + ".description.text");
    }

    default ResourceBundle resourceBundle() {
        return resourceBundle(defaultLocale);
    }

    default ResourceBundle resourceBundle(Locale locale) {
        return ResourceBundle.getBundle("lang", locale);
    }

    default String resource(String resourceBundleKey) {
        return resourceBundle().getString(resourceBundleKey);
    }

    default List<OptionData> getCommandOptions() {
        return Collections.emptyList();
    }

    default List<SubcommandData> getSubcommandDatas() {
        return Collections.emptyList();
    }

    default MessageExecutor<SlashCommandEventMessageExecutor> execute(
            @NotNull SlashCommandInteractionEvent slashCommandEvent) {
        if (shouldCheckPermissions() && executePreCommandActions(slashCommandEvent) || !shouldCheckPermissions()) {
            return executeCommandAction(slashCommandEvent);
        } else {
            return new SlashCommandEventMessageExecutor(slashCommandEvent).addEphemeralMessage("You do not have "
                    + "permission to execute this command.");
        }
    }

    MessageExecutor<SlashCommandEventMessageExecutor> executeCommandAction(
            @NotNull SlashCommandInteractionEvent slashCommandEvent);

    default ConfigService configService() {
        return null;
    }


    /**
     * Runs pre command actions, such as permission checks.
     *
     * @param slashCommandEvent the event being executed
     * @return true if the user has permission to run the command, otherwise false.
     */
    default boolean executePreCommandActions(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        var guild = slashCommandEvent.getGuild();
        var member = slashCommandEvent.getMember();
        return userHasPermission(guild, member);
    }

    /**
     * Returns true if the user has the correct permission to run the command.
     *
     * @param guild  the guild the command is run in
     * @param member the member running the command
     * @return true if the user has permission to run the command, otherwise false.
     */
    default boolean userHasPermission(Guild guild, Member member) {
        if (member != null && guild != null && guild.getOwner() != null && member.getId()
                .equals(guild.getOwner().getId())) {
            return true;
        }
        switch (getRolePermission()) {
            case EVERYONE -> {
                return true;
            }
            case ADMIN, MODS -> {
                if (guild == null) {
                    return false;
                }
                var configuredRole = configService().getSingleValueByName(guild.getId(),
                        "roleRank-" + getRolePermission().getRoleName());
                var roles =
                        configuredRole.map(configuredRoleId ->
                                        Collections.singletonList(guild.getRoleById(configuredRoleId)))
                                .orElseGet(() -> guild.getRolesByName(getRolePermission().getRoleName(), true));
                return guild.getMembersWithRoles(roles).contains(member);
            }
        }
        return false;
    }
}
