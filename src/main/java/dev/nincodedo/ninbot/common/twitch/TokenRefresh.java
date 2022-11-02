package dev.nincodedo.ninbot.common.twitch;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
class TokenRefresh {
    private CredentialManager credentialManager;
    private TwitchIdentityProvider twitchIdentityProvider;

    protected TokenRefresh(CredentialManager credentialManager, TwitchIdentityProvider twitchIdentityProvider) {
        this.credentialManager = credentialManager;
        this.twitchIdentityProvider = twitchIdentityProvider;
    }

    @Scheduled(timeUnit = TimeUnit.DAYS, fixedRate = 1, initialDelay = 1)
    protected void updateToken() {
        var oauthOptional = credentialManager.getOAuth2IdentityProviderByName("twitch");
        oauthOptional.ifPresent(oAuth2IdentityProvider -> oAuth2IdentityProvider.getAppAccessToken()
                .updateCredential(twitchIdentityProvider.getAppAccessToken()));
    }
}
