package com.nincraft.ninbot.beans;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.TwitchHelixBuilder;
import com.nincodedo.sapconversational.SAPConversationalAIAPI;
import com.nincodedo.sapconversational.SAPConversationalAIAPIBuilder;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;
import java.util.List;

@Configuration
@ComponentScan(basePackages = {"com.nincraft.ninbot"})
@Log4j2
public class ApplicationBean {

    @Value("${ninbotToken}")
    private String ninbotToken;

    @Value("${sapToken}")
    private String sapToken;

    @Value("${twitchClientId}")
    private String twitchClientId;

    @Value("${twitchClientSecret}")
    private String twitchClientSecret;

    @Autowired
    @Bean
    public ShardManager shardManager(List<ListenerAdapter> listenerAdapters) {
        try {
            DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder(ninbotToken);
            builder.addEventListeners(listenerAdapters.toArray());
            builder.setShardsTotal(-1);
            return builder.build();
        } catch (LoginException e) {
            log.error("Failed to login", e);
        }
        return null;
    }

    @Bean
    public SAPConversationalAIAPI sapConversationalAIAPI() {
        return new SAPConversationalAIAPIBuilder(sapToken).build();
    }

    @Bean
    public TwitchHelix twitchHelix() {
        return TwitchHelixBuilder.builder().withClientId(twitchClientId).withClientSecret(twitchClientSecret).build();
    }
}
