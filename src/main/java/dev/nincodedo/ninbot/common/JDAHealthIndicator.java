package dev.nincodedo.ninbot.common;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JDAHealthIndicator implements HealthIndicator {

    private final ShardManager shardManager;

    public JDAHealthIndicator(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    @Override
    public Health health() {
        var shardStatuses = shardManager.getStatuses();
        var totalShardCount = shardStatuses.values().size();
        var connectedShardsCount = getConnectedShardsCount(shardStatuses);
        if (totalShardCount == connectedShardsCount && connectedShardsCount > 0) {
            return Health.up().status(new Status("UP", getShardTotals(connectedShardsCount, totalShardCount))).build();
        } else if (connectedShardsCount > 0) {
            return Health.up()
                    .status(new Status("Partial service", getShardTotals(connectedShardsCount, totalShardCount)))
                    .build();
        } else {
            return Health.down().build();
        }
    }

    private String getShardTotals(int connectedShardsCount, int totalShardCount) {
        return String.format("%s of %s shards connected", connectedShardsCount, totalShardCount);
    }

    private int getConnectedShardsCount(Map<JDA, JDA.Status> shardStatuses) {
        return (int) shardStatuses.values().stream().filter(status -> status == JDA.Status.CONNECTED).count();
    }
}
