package dev.nincodedo.nincord.config.properties;

import dev.nincodedo.nincord.release.ReleaseFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nincodedo")
public record NincordProperties(String ninbotToken,
                                String steamGridDbApiKey,
                                Class<? extends ReleaseFilter> releaseFilterClass,
                                String mainServerId) {
}
