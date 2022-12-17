package dev.nincodedo.nincord.autoconfigure;

import dev.nincodedo.nincord.actuate.health.JDAHealthIndicator;
import dev.nincodedo.nincord.config.properties.ActuatorConfig;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(ActuatorConfig.class)
public class ActuatorAutoConfig {

    @ConditionalOnProperty(prefix = "nincord.actuator", name = "jdaHealthEnabled", havingValue = "true")
    @Bean
    public JDAHealthIndicator jdaHealthIndicator(ShardManager shardManager) {
        return new JDAHealthIndicator(shardManager);
    }
}
