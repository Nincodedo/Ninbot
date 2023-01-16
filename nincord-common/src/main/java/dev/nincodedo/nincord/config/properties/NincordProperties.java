package dev.nincodedo.nincord.config.properties;

import dev.nincodedo.nincord.release.ReleaseFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nincord")
public record NincordProperties(String discordBotToken,
                                String steamGridDbApiKey,
                                Class<? extends ReleaseFilter> releaseFilterClass,
                                String mainServerId) {
}
