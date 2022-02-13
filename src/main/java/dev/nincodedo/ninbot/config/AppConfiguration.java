package dev.nincodedo.ninbot.config;

import dev.nincodedo.ninbot.common.config.NincodedoAutoConfig;
import dev.nincodedo.ninbot.common.release.DefaultReleaseFilter;
import dev.nincodedo.ninbot.common.release.ReleaseFilter;
import dev.nincodedo.ninbot.common.supporter.DefaultSupporterCheck;
import dev.nincodedo.ninbot.common.supporter.SupporterCheck;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.List;

@Slf4j
@Configuration
public class AppConfiguration {

    private String ninbotToken;
    private NincodedoAutoConfig nincodedoAutoConfig;

    public AppConfiguration(@Value("${ninbotToken}") String ninbotToken, NincodedoAutoConfig nincodedoAutoConfig) {
        this.ninbotToken = ninbotToken;
        this.nincodedoAutoConfig = nincodedoAutoConfig;
    }

    @Autowired
    @Bean
    public ShardManager shardManager(List<ListenerAdapter> listenerAdapters) {
        try {
            return DefaultShardManagerBuilder.create(ninbotToken, EnumSet.of(GatewayIntent.GUILD_EMOJIS,
                            GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_MESSAGE_REACTIONS))
                    .addEventListeners(listenerAdapters.toArray())
                    .setShardsTotal(-1)
                    .build();
        } catch (LoginException e) {
            log.error("Failed to login", e);
        }
        return null;
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
        var supporterCheckClass = nincodedoAutoConfig.supporterCheckClass()
                == null ? DefaultSupporterCheck.class : nincodedoAutoConfig.supporterCheckClass();
        return supporterCheckClass.getDeclaredConstructor().newInstance();
    }
}
