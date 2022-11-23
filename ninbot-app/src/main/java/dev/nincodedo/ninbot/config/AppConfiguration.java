package dev.nincodedo.ninbot.config;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.storage.TemporaryStorageBackend;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import dev.nincodedo.nincord.config.app.NincodedoAutoConfig;
import dev.nincodedo.nincord.config.app.SupporterConfig;
import dev.nincodedo.nincord.release.DefaultReleaseFilter;
import dev.nincodedo.nincord.release.ReleaseFilter;
import dev.nincodedo.nincord.supporter.DefaultSupporterCheck;
import dev.nincodedo.nincord.supporter.SupporterCheck;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
public class AppConfiguration {

    private NincodedoAutoConfig nincodedoAutoConfig;

    public AppConfiguration(NincodedoAutoConfig nincodedoAutoConfig) {
        this.nincodedoAutoConfig = nincodedoAutoConfig;
    }

    @Autowired
    @Bean
    public ShardManager shardManager(List<ListenerAdapter> listenerAdapters) {
        return DefaultShardManagerBuilder.create(nincodedoAutoConfig.ninbotToken(),
                        EnumSet.of(GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                                GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES,
                                GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGES,
                                GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.MESSAGE_CONTENT))
                .addEventListeners(listenerAdapters.toArray())
                .setShardsTotal(-1)
                .build();
    }

    @Bean
    public ReleaseFilter releaseFilter() throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        var releaseFilterClass = nincodedoAutoConfig.releaseFilterClass()
                == null ? DefaultReleaseFilter.class : nincodedoAutoConfig.releaseFilterClass();
        return releaseFilterClass.getDeclaredConstructor().newInstance();
    }

    @Bean
    public SupporterCheck supporterCheck() throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        SupporterConfig supporterConfig = nincodedoAutoConfig.supporter();
        var supporterCheckClass = supporterConfig.checkClass()
                == null ? DefaultSupporterCheck.class : supporterConfig.checkClass();
        var supporterCheck = supporterCheckClass.getDeclaredConstructor().newInstance();
        supporterCheck.setPatreonServerId(supporterConfig.patreonServerId());
        return supporterCheck;
    }

    @Bean
    public ExecutorService commandParserThreadPool() {
        return Executors.newCachedThreadPool(new NamedThreadFactory("command-parser"));
    }

    @Bean
    public ExecutorService schedulerThreadPool() {
        return Executors.newCachedThreadPool(new NamedThreadFactory("scheduler"));
    }

    @Bean
    public ExecutorService listenerThreadPool() {
        return Executors.newCachedThreadPool(new NamedThreadFactory("listeners"));
    }

    @Bean
    public ExecutorService statCounterThreadPool() {
        return Executors.newCachedThreadPool(new NamedThreadFactory("stat-counter"));
    }

    @Bean
    public TwitchIdentityProvider twitchIdentityProvider() {
        return new TwitchIdentityProvider(nincodedoAutoConfig.twitch().clientId(),
                nincodedoAutoConfig.twitch().clientSecret(), null);
    }

    @Bean
    public CredentialManager credentialManager(TwitchIdentityProvider twitchIdentityProvider) {
        var credentialManager = CredentialManagerBuilder.builder()
                .withStorageBackend(new TemporaryStorageBackend())
                .build();
        credentialManager.registerIdentityProvider(twitchIdentityProvider);
        return credentialManager;
    }

    @Bean
    public TwitchClient twitchClient(TwitchIdentityProvider twitchIdentityProvider,
            CredentialManager credentialManager) {
        return TwitchClientBuilder.builder()
                .withCredentialManager(credentialManager)
                .withDefaultAuthToken(twitchIdentityProvider.getAppAccessToken())
                .withEnableHelix(true)
                .build();
    }
}