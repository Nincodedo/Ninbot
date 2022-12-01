package dev.nincodedo.nincord.config.app;

import dev.nincodedo.nincord.release.DefaultReleaseFilter;
import dev.nincodedo.nincord.release.ReleaseFilter;
import dev.nincodedo.nincord.supporter.DefaultSupporterCheck;
import dev.nincodedo.nincord.supporter.SupporterCheck;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ConditionalAutoConfig {

    @ConditionalOnMissingBean
    @Bean
    public SupporterCheck supporterCheck() {
        return new DefaultSupporterCheck();
    }

    @ConditionalOnMissingBean
    @Bean
    public ReleaseFilter releaseFilter() {
        return new DefaultReleaseFilter();
    }

    @ConditionalOnMissingBean
    @Bean
    public ExecutorService commandParserThreadPool() {
        return Executors.newCachedThreadPool(new NamedThreadFactory("command-parser"));
    }
}
