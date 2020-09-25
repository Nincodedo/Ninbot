package dev.nincodedo.ninbot.components.common;

import net.dv8tion.jda.api.sharding.ShardManager;

public interface Schedulable {
    void scheduleAll(ShardManager shardManager);
}
