package dev.nincodedo.ninbot.autoconfigure;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.storage.TemporaryStorageBackend;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import dev.nincodedo.ninbot.components.stream.twitch.TokenRefresh;
import dev.nincodedo.ninbot.config.properties.TwitchConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "nincord.twitch", name = {"clientId", "clientSecret"})
@EnableConfigurationProperties(TwitchConfig.class)
public class TwitchAutoConfig {

    private final TwitchConfig twitchConfig;

    public TwitchAutoConfig(TwitchConfig twitchConfig) {
        this.twitchConfig = twitchConfig;
    }

    @Bean
    public TokenRefresh tokenRefresh(CredentialManager credentialManager,
            TwitchIdentityProvider twitchIdentityProvider) {
        return new TokenRefresh(credentialManager, twitchIdentityProvider);
    }

    @Bean
    public TwitchIdentityProvider twitchIdentityProvider() {
        return new TwitchIdentityProvider(twitchConfig.clientId(), twitchConfig.clientSecret(), null);
    }

    @Bean
    public CredentialManager credentialManager(TwitchIdentityProvider twitchIdentityProvider) {
        var credentialManager = CredentialManagerBuilder.builder()
                .withStorageBackend(new TemporaryStorageBackend())
                .build();
        credentialManager.registerIdentityProvider(twitchIdentityProvider);
        return credentialManager;
    }
}