package dev.nincodedo.nincord.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nincodedo.twitch")
public record TwitchConfig(String clientId, String clientSecret) {
}
