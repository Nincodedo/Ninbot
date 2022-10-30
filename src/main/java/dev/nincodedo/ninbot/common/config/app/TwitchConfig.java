package dev.nincodedo.ninbot.common.config.app;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nincodedo.twitch")
public record TwitchConfig(String clientId, String clientSecret, String token) {
}
