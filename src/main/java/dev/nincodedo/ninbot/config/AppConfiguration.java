package dev.nincodedo.ninbot.config;

import dev.nincodedo.ninbot.common.release.DefaultReleaseFilter;
import dev.nincodedo.ninbot.common.release.ReleaseFilter;
import dev.nincodedo.ninbot.common.supporter.DefaultSupporterCheck;
import dev.nincodedo.ninbot.common.supporter.SupporterCheck;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.apache.commons.lang3.StringUtils;
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
    @Value("${nincodedo.supporter-check.classname:}")
    private String supporterCheckClassName;
    @Value("${nincodedo.release-filter.classname:}")
    private String releaseFilterClassName;

    public AppConfiguration(@Value("${ninbotToken}") String ninbotToken) {
        this.ninbotToken = ninbotToken;
    }

    @Autowired
    @Bean
    public ShardManager shardManager(List<ListenerAdapter> listenerAdapters) {
        try {
            return DefaultShardManagerBuilder.create(ninbotToken,
                            EnumSet.of(GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_MEMBERS,
                                    GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_PRESENCES,
                                    GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS))
                    .addEventListeners(listenerAdapters.toArray())
                    .setShardsTotal(-1).build();
        } catch (LoginException e) {
            log.error("Failed to login", e);
        }
        return null;
    }

    @Bean
    public ReleaseFilter releaseFilter() throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        if (StringUtils.isBlank(releaseFilterClassName)) {
            return new DefaultReleaseFilter();
        }
        return (ReleaseFilter) Class.forName(releaseFilterClassName).getDeclaredConstructor().newInstance();
    }

    @Bean
    public SupporterCheck supporterCheck() throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        if (StringUtils.isBlank(supporterCheckClassName)) {
            return new DefaultSupporterCheck();
        }
        return (SupporterCheck) Class.forName(supporterCheckClassName).getDeclaredConstructor().newInstance();
    }
}
