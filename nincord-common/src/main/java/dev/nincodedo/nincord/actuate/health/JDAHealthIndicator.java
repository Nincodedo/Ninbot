package dev.nincodedo.nincord.actuate.health;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import java.util.Map;

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
        Health.Builder healthBuilder;
        if (connectedShardsCount > 0) {
            healthBuilder = Health.up();
        } else {
            healthBuilder = Health.down();
        }
        return healthBuilder.withDetail("connectedShardCount", connectedShardsCount)
                .withDetail("totalShardCount", totalShardCount)
                .build();
    }

    private int getConnectedShardsCount(Map<JDA, JDA.Status> shardStatuses) {
        return (int) shardStatuses.values().stream().filter(status -> status == JDA.Status.CONNECTED).count();
    }
}
