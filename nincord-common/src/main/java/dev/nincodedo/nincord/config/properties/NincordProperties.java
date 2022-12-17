package dev.nincodedo.nincord.config.properties;

import dev.nincodedo.nincord.release.ReleaseFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nincord")
public record NincordProperties(String ninbotToken,
                                String steamGridDbApiKey,
                                SupporterConfig supporter,
                                InfoCommandConfig info,
                                Class<? extends ReleaseFilter> releaseFilterClass,
                                String mainServerId,
                                TwitchConfig twitch,
                                ActuatorConfig actuator) {
}
