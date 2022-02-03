package dev.nincodedo.ninbot.common;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

@UtilityClass
public class CommonUtils {
    /**
     * Returns true if the user is a Patreon supporter (specifically if they are in the Ninbot Patreon Discord).
     *
     * @param shardManager shardManager
     * @param user         user to check
     * @return true/false
     */
    public static boolean isUserNinbotSupporter(ShardManager shardManager, User user) {
        var guild = shardManager.getGuildById(Constants.NINBOT_SUPPORTERS_SERVER_ID);
        return guild != null && guild.getMembers()
                .stream()
                .anyMatch(member -> member.getId().equals(user.getId()));
    }
}
