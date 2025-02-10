package dev.nincodedo.nincord.autoconfigure;

import dev.nincodedo.nincord.config.properties.NincordProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(NincordProperties.class)
public class NincordAutoConfig {

    private NincordProperties nincordProperties;

    public NincordAutoConfig(NincordProperties nincordProperties) {
        this.nincordProperties = nincordProperties;
    }
}
