package dev.nincodedo.ninbot.components.stream.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import dev.nincodedo.ninbot.config.properties.TwitchConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TokenRefresh {
    private TwitchIdentityProvider twitchIdentityProvider;
    private TwitchConfig twitchConfig;
    private OAuth2Credential twitchCredential;

    public TokenRefresh(TwitchIdentityProvider twitchIdentityProvider,
            TwitchConfig twitchConfig, OAuth2Credential twitchCredential) {
        this.twitchIdentityProvider = twitchIdentityProvider;
        this.twitchConfig = twitchConfig;
        this.twitchCredential = twitchCredential;
    }

    @Scheduled(timeUnit = TimeUnit.DAYS, fixedRate = 30, initialDelay = 30)
    protected void updateToken() {
        log.trace("Refreshing Twitch token");
        twitchIdentityProvider = new TwitchIdentityProvider(twitchConfig.clientId(), twitchConfig.clientSecret(), null);
        OAuth2Credential updatedCredentials = twitchIdentityProvider.getAppAccessToken();
        twitchCredential.setAccessToken(updatedCredentials.getAccessToken());
        twitchCredential.setRefreshToken(updatedCredentials.getRefreshToken());
        log.trace("Finished refreshing Twitch token");
    }
}
