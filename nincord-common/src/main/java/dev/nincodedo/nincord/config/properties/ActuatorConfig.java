package dev.nincodedo.nincord.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nincodedo.actuator")
public record ActuatorConfig(boolean jdaHealthEnabled) {
}
