package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.Constants;
import dev.nincodedo.ninbot.common.RolePermission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public interface SlashCommand {

    String getName();

    default boolean shouldCheckPermissions() {
        return true;
    }

    default RolePermission getRolePermission() {
        return RolePermission.EVERYONE;
    }

    default String getDescription() {
        return ResourceBundle.getBundle("lang", Locale.ENGLISH).getString("command." + getName() + ".description.text");
    }

    default ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("lang", Locale.ENGLISH);
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

    default void execute(SlashCommandEvent slashCommandEvent) {
        var permissionGranted = executePreCommandActions(slashCommandEvent);
        if (shouldCheckPermissions()) {
            if (permissionGranted) {
                executeCommandAction(slashCommandEvent);
            }
        } else if (shouldCheckPermissions() && !permissionGranted) {
            slashCommandEvent.reply("You do not have permission to execute this command.").setEphemeral(true).queue();
        }
    }

    void executeCommandAction(SlashCommandEvent slashCommandEvent);


    /**
     * Runs pre command actions, such as permission checks.
     * @param slashCommandEvent the event being executed
     * @return true if the user has permission to run the command, otherwise false.
     */
    default boolean executePreCommandActions(SlashCommandEvent slashCommandEvent) {
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
        if (member != null && guild != null && member.equals(guild.getOwner())) {
            return true;
        }
        switch (getRolePermission()) {
            case EVERYONE -> {
                return true;
            }
            case ADMIN, MODS -> {
                //TODO implement config service lookup
                return false;
            }
        }
        return false;
    }


    /**
     * Returns true if the user is a Patreon supporter (specifically if they are in the Ninbot Patreon Discord).
     *
     * @param shardManager shardManager
     * @param user         user to check
     * @return true/false
     */
    default boolean isUserNinbotSupporter(ShardManager shardManager, User user) {
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

