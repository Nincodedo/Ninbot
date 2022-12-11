package dev.nincodedo.ninbot.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JDAMetricsConfiguration {
    @Bean
    public MeterBinder jdaServers(ShardManager shardManager) {
        return registry -> {
            Gauge.builder("jda.cache.shards", shardManager.getShardCache()::size)
                    .baseUnit("shards")
                    .description("The number of cached JDA bound to this ShardManager")
                    .register(registry);
            Gauge.builder("jda.cache.guilds", shardManager.getGuildCache()::size)
                    .baseUnit("guilds")
                    .description("The number of cached Guilds bound to this ShardManager")
                    .register(registry);
            Gauge.builder("jda.cache.users", shardManager.getUserCache()::size)
                    .baseUnit("users")
                    .description("The number of cached Users visible to this ShardManager")
                    .register(registry);
        };
    }
}
