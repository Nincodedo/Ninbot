package dev.nincodedo.ninbot.common.supporter;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

public interface SupporterCheck {
    /**
     * Returns true if the user is a supporter of the implemented methods.
     *
     * @param shardManager JDA {@link ShardManager} to check for Discord server membership
     * @param user         the JDA {@link User} to check against
     * @return true/false
     */
    default boolean isSupporter(ShardManager shardManager, User user) {
        if (isPatreonSupporter(shardManager, user)) {
            return true;
        }
        if (isGitHubSupporter(shardManager, user)) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the user is a supporter through GitHub.
     *
     * @param shardManager JDA {@link ShardManager} to check for Discord server membership
     * @param user         the JDA {@link User} to check against
     * @return true/false
     */
    boolean isGitHubSupporter(ShardManager shardManager, User user);

    /**
     * Returns true if the user is a supporter through Patreon.
     *
     * @param shardManager JDA {@link ShardManager} to check for Discord server membership
     * @param user         the JDA {@link User} to check against
     * @return true/false
     */
    boolean isPatreonSupporter(ShardManager shardManager, User user);
}
