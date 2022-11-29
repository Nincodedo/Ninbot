package dev.nincodedo.nincord.config.app;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import dev.nincodedo.nincord.release.DefaultReleaseFilter;
import dev.nincodedo.nincord.release.ReleaseFilter;
import dev.nincodedo.nincord.supporter.DefaultSupporterCheck;
import dev.nincodedo.nincord.supporter.SupporterCheck;
import dev.nincodedo.nincord.twitch.TokenRefresh;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

    @ConditionalOnProperty(prefix = "nincodedo.twitch", name = "clientId")
    @Bean
    public TokenRefresh tokenRefresh(CredentialManager credentialManager,
            TwitchIdentityProvider twitchIdentityProvider) {
        return new TokenRefresh(credentialManager, twitchIdentityProvider);
    }

}
