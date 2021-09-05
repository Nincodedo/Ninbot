package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.Constants;
import dev.nincodedo.ninbot.common.RolePermission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public interface SlashCommand {
    String getName();

    default RolePermission getRolePermission() {
        return RolePermission.EVERYONE;
    }

    default String getDescription() {
        return ResourceBundle.getBundle("lang", Locale.ENGLISH).getString("command." + getName() + ".description.text");
    }

    default ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("lang", Locale.ENGLISH);
    }

    List<OptionData> getCommandOptions();

    List<SubcommandData> getSubcommandDatas();

    void execute(SlashCommandEvent slashCommandEvent);

    /**
     * Returns true if the user is a Patreon supporter (specifically if they are in the Ninbot Patreon Discord)
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

