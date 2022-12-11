package dev.nincodedo.nincord.autoconfigure;

import dev.nincodedo.nincord.config.app.NincordProperties;
import dev.nincodedo.nincord.release.DefaultReleaseFilter;
import dev.nincodedo.nincord.release.ReleaseFilter;
import dev.nincodedo.nincord.supporter.DefaultSupporterCheck;
import dev.nincodedo.nincord.supporter.SupporterCheck;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@AutoConfiguration
@EnableConfigurationProperties(NincordProperties.class)
public class NincordAutoConfig {

    private NincordProperties nincordProperties;

    public NincordAutoConfig(NincordProperties nincordProperties) {
        this.nincordProperties = nincordProperties;
    }

    @Bean
    public ReleaseFilter releaseFilter() throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        var releaseFilter = nincordProperties.releaseFilterClass()
                == null ? DefaultReleaseFilter.class : nincordProperties.releaseFilterClass();
        return releaseFilter.getDeclaredConstructor().newInstance();
    }

    @Bean
    public SupporterCheck supporterCheck() throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        var supporterCheckClass = nincordProperties.supporter().checkClass()
                == null ? DefaultSupporterCheck.class : nincordProperties.supporter().checkClass();
        var supporterCheck = supporterCheckClass.getDeclaredConstructor().newInstance();
        supporterCheck.setPatreonServerId(nincordProperties.supporter().patreonServerId());
        return supporterCheck;
    }

    @ConditionalOnMissingBean(name = "commandParserThreadPool")
    @Bean
    public ExecutorService commandParserThreadPool() {
        return Executors.newCachedThreadPool(new NamedThreadFactory("command-parser"));
    }
}
