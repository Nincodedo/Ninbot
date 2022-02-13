package dev.nincodedo.ninbot.common.supporter;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

public class DefaultSupporterCheck implements SupporterCheck{
    @Override
    public boolean isGitHubSupporter(ShardManager shardManager, User user) {
        return false;
    }

    @Override
    public boolean isPatreonSupporter(ShardManager shardManager, User user) {
        return false;
    }
}
