package dev.nincodedo.nincord.config.app;

import dev.nincodedo.nincord.supporter.SupporterCheck;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nincodedo.support")
public record SupporterConfig(Class<? extends SupporterCheck> checkClass,
                              String patreonServerId) {
}