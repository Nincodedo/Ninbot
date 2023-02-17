package dev.nincodedo.nincord.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nincodedo.info")
public record InfoCommandConfig(String documentationUrl, String githubUrl) {
}
