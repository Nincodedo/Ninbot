package dev.nincodedo.ninbot.ocw;

import dev.nincodedo.nincord.supporter.SupporterCheck;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

@Slf4j
public class NinbotSupporterCheck implements SupporterCheck {

    @Getter
    @Setter
    private String patreonServerId = null;

    @Override
    public boolean isGitHubSupporter(ShardManager shardManager, User user) {
        return false;
    }
}
