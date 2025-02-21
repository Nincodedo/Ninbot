package dev.nincodedo.nincord.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nincodedo")
public record NincordProperties(String ninbotToken,
                                String steamGridDbApiKey,
                                String mainServerId) {
}
