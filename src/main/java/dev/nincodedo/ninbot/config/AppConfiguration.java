package dev.nincodedo.ninbot.config;

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
import java.util.EnumSet;
import java.util.List;

@Slf4j
@Configuration
public class AppConfiguration {

    private String ninbotToken;

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
}
