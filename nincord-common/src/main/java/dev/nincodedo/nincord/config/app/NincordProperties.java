package dev.nincodedo.nincord.config.app;

import dev.nincodedo.nincord.release.ReleaseFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nincodedo")
public record NincordProperties(String ninbotToken,
                                String steamGridDbApiKey,
                                SupporterConfig supporter,
                                InfoCommandConfig info,
                                Class<? extends ReleaseFilter> releaseFilterClass,
                                String mainServerId,
                                TwitchConfig twitch) {
}
