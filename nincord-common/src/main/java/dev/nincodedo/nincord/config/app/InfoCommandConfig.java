package dev.nincodedo.nincord.config.app;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nincodedo.info")
public record InfoCommandConfig(String documentationUrl, String githubUrl) {
}
