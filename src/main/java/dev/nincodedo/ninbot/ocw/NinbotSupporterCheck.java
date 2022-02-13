package dev.nincodedo.ninbot.ocw;

import dev.nincodedo.ninbot.common.Constants;
import dev.nincodedo.ninbot.common.supporter.SupporterCheck;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

@Slf4j
public class NinbotSupporterCheck implements SupporterCheck {

    @Override
    public boolean isGitHubSupporter(ShardManager shardManager, User user) {
        return false;
    }

    /**
     * Returns true if the user is a Patreon supporter (specifically if they are in the Ninbot Patreon Discord).
     *
     * @param shardManager shardManager
     * @param user         user to check
     * @return true/false
     */
    @Override
    public boolean isPatreonSupporter(ShardManager shardManager, User user) {
        var guild = shardManager.getGuildById(Constants.NINBOT_SUPPORTERS_SERVER_ID);
        if (guild != null && guild.getMembers()
                .stream()
                .anyMatch(member -> member.getId().equals(user.getId()))) {
            log.trace("Made possible by Patreon");
            return true;
        }
        return false;
    }
}
