package dev.nincodedo.ninbot.common.supporter;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.Nullable;

public interface SupporterCheck {

    /**
     * Returns true if the user is a supporter of the implemented methods.
     *
     * @param shardManager JDA {@link ShardManager} to check for Discord server membership
     * @param user         the JDA {@link User} to check against
     * @return true/false
     */
    default boolean isSupporter(ShardManager shardManager, User user) {
        return isPatreonSupporter(shardManager, user) || isGitHubSupporter(shardManager, user);
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
    default boolean isPatreonSupporter(ShardManager shardManager, User user) {
        if (getPatreonServerId() == null) {
            return false;
        }
        var guild = shardManager.getGuildById(getPatreonServerId());
        return guild != null && guild.getMembers()
                .stream()
                .anyMatch(member -> member.getId().equals(user.getId()));
    }

    /**
     * Returns the Discord server id of the Patreon server. May be null.
     *
     * @return A possibly null String Discord server id
     */
    @Nullable
    String getPatreonServerId();

    /**
     * Set the Discord server id of the Patreon server.
     *
     * @param patreonServerId Discord server id
     */
    void setPatreonServerId(String patreonServerId);
}
