package dev.nincodedo.nincord.autoconfigure;

import dev.nincodedo.nincord.config.properties.SupporterConfig;
import dev.nincodedo.nincord.supporter.DefaultSupporterCheck;
import dev.nincodedo.nincord.supporter.SupporterCheck;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.InvocationTargetException;

@AutoConfiguration
@EnableConfigurationProperties(SupporterConfig.class)
public class SupporterAutoConfig {

    private SupporterConfig supporterConfig;

    public SupporterAutoConfig(SupporterConfig supporterConfig) {
        this.supporterConfig = supporterConfig;
    }

    @ConditionalOnMissingBean
    @Bean
    public SupporterCheck supporterCheck() throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        var supporterCheckClass = supporterConfig == null || supporterConfig.checkClass()
                == null ? DefaultSupporterCheck.class : supporterConfig.checkClass();
        var supporterCheck = supporterCheckClass.getDeclaredConstructor().newInstance();
        if (supporterConfig != null) {
            supporterCheck.setPatreonServerId(supporterConfig.patreonServerId());
        }
        return supporterCheck;
    }
}
