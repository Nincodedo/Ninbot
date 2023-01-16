package dev.nincodedo.nincord.autoconfigure;

import dev.nincodedo.nincord.config.properties.NincordProperties;
import dev.nincodedo.nincord.release.DefaultReleaseFilter;
import dev.nincodedo.nincord.release.ReleaseFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.InvocationTargetException;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(NincordProperties.class)
public class NincordAutoConfig {

    private NincordProperties nincordProperties;

    public NincordAutoConfig(NincordProperties nincordProperties) {
        this.nincordProperties = nincordProperties;
    }

    @ConditionalOnMissingBean
    @Bean
    public ReleaseFilter releaseFilter() throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        var releaseFilter = nincordProperties.releaseFilterClass()
                == null ? DefaultReleaseFilter.class : nincordProperties.releaseFilterClass();
        return releaseFilter.getDeclaredConstructor().newInstance();
    }
}
