package dev.nincodedo.ninbot.common.config.app;

import dev.nincodedo.ninbot.common.release.ReleaseFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nincodedo")
public record NincodedoAutoConfig(String ninbotToken,
                                  String steamGridDbApiKey,
                                  SupporterConfig supporter,
                                  InfoCommandConfig info,
                                  Class<? extends ReleaseFilter> releaseFilterClass,
                                  String mainServerId) {
}
