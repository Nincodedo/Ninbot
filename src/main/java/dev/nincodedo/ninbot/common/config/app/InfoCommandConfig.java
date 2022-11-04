package dev.nincodedo.ninbot.common.config.app;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nincodedo.info")
public record InfoCommandConfig(String documentationUrl, String githubUrl) {
}
