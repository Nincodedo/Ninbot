package dev.nincodedo.ninbot.common.config;

import dev.nincodedo.ninbot.common.release.ReleaseFilter;
import dev.nincodedo.ninbot.common.supporter.SupporterCheck;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nincodedo")
public record NincodedoAutoConfig(String ninbotToken,
                                  Class<? extends SupporterCheck> supporterCheckClass,
                                  Class<? extends ReleaseFilter> releaseFilterClass) {
}
