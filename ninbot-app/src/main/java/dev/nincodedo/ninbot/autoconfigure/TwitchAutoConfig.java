package dev.nincodedo.ninbot.autoconfigure;

import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.credentialmanager.storage.TemporaryStorageBackend;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import dev.nincodedo.ninbot.components.stream.twitch.TokenRefresh;
import dev.nincodedo.ninbot.config.properties.TwitchConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "nincodedo.twitch", name = {"clientId", "clientSecret"})
@EnableConfigurationProperties(TwitchConfig.class)
public class TwitchAutoConfig {

    private final TwitchConfig twitchConfig;

    public TwitchAutoConfig(TwitchConfig twitchConfig) {
        this.twitchConfig = twitchConfig;
    }

    @Bean
    public TokenRefresh tokenRefresh(TwitchIdentityProvider twitchIdentityProvider, TwitchConfig twitchConfig,
            OAuth2Credential twitchCredential) {
        return new TokenRefresh(twitchIdentityProvider, twitchConfig, twitchCredential);
    }

    @Bean
    public TwitchIdentityProvider twitchIdentityProvider() {
        return new TwitchIdentityProvider(twitchConfig.clientId(), twitchConfig.clientSecret(), null);
    }

    @Bean
    public OAuth2Credential twitchCredentials(TwitchIdentityProvider twitchIdentityProvider) {
        return twitchIdentityProvider.getAppAccessToken();
    }

    @Bean
    public TwitchClient twitchClient(TwitchIdentityProvider twitchIdentityProvider,
            OAuth2Credential twitchCredentials) {
        var credentialManager = CredentialManagerBuilder.builder()
                .withStorageBackend(new TemporaryStorageBackend())
                .build();
        credentialManager.registerIdentityProvider(twitchIdentityProvider);
        return TwitchClientBuilder.builder()
                .withCredentialManager(credentialManager)
                .withDefaultAuthToken(twitchCredentials)
                .withEnableHelix(true)
                .build();
    }
}
