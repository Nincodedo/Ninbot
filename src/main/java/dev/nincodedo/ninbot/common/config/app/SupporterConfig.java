package dev.nincodedo.ninbot.common.config.app;

import dev.nincodedo.ninbot.common.supporter.SupporterCheck;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nincodedo.support")
public record SupporterConfig(Class<? extends SupporterCheck> checkClass,
                              String patreonServerId) {
}
