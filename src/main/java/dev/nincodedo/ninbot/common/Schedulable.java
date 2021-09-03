package dev.nincodedo.ninbot.common;

import net.dv8tion.jda.api.sharding.ShardManager;

public interface Schedulable {
    void scheduleAll(ShardManager shardManager);
}
