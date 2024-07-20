package dev.nincodedo.ninbot.components.stream.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TokenRefresh {
    private TwitchIdentityProvider twitchIdentityProvider;
    private OAuth2Credential twitchCredential;

    public TokenRefresh(TwitchIdentityProvider twitchIdentityProvider, OAuth2Credential twitchCredential) {
        this.twitchIdentityProvider = twitchIdentityProvider;
        this.twitchCredential = twitchCredential;
    }

    @Scheduled(timeUnit = TimeUnit.DAYS, fixedRate = 1, initialDelay = 30)
    protected void updateToken() {
        twitchIdentityProvider.getAdditionalCredentialInformation(twitchCredential).ifPresent(oAuth2Credential -> {
            var secondsToExpire = oAuth2Credential.getExpiresIn();
            if (secondsToExpire <= 0) {
                twitchIdentityProvider.refreshCredential(oAuth2Credential)
                        .ifPresent(oAuth2Credential::updateCredential);
            }
        });
    }
}
