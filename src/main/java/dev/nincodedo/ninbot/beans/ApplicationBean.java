package dev.nincodedo.ninbot.beans;

import dev.nincodedo.sapconversational.SAPConversationalAIAPI;
import dev.nincodedo.sapconversational.SAPConversationalAIAPIBuilder;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.List;

@Configuration
@ComponentScan(basePackages = {"dev.nincodedo.ninbot"})
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
            DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.create(ninbotToken,
                    Arrays.asList(GatewayIntent.values()));
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
}
