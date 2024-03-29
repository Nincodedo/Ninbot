package dev.nincodedo.nincord.supporter;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

public class DefaultSupporterCheck implements SupporterCheck {
    @Override
    public boolean isGitHubSupporter(ShardManager shardManager, User user) {
        return false;
    }

    @Override
    public boolean isPatreonSupporter(ShardManager shardManager, User user) {
        return false;
    }

    @Override
    public String getPatreonServerId() {
        return null;
    }

    @Override
    public void setPatreonServerId(String patreonServerId) {
        // NO-OP
    }
}
