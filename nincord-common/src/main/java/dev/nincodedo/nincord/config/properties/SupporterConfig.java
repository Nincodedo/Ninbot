package dev.nincodedo.nincord.config.properties;

import dev.nincodedo.nincord.supporter.SupporterCheck;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nincord.support")
public record SupporterConfig(Class<? extends SupporterCheck> checkClass,
                              String patreonServerId) {
}
