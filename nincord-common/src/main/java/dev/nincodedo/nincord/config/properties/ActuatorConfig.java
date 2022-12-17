package dev.nincodedo.nincord.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nincord.actuator")
public record ActuatorConfig(boolean jdaHealthEnabled) {
}
