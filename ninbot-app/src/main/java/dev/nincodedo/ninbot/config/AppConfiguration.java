package dev.nincodedo.ninbot.config;

import dev.nincodedo.ninbot.ocw.NinbotSupporterCheck;
import dev.nincodedo.nincord.config.properties.NincordProperties;
import dev.nincodedo.nincord.config.properties.SupporterConfig;
import dev.nincodedo.nincord.supporter.SupporterCheck;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
@EnableConfigurationProperties({NincordProperties.class, SupporterConfig.class})
public class AppConfiguration {

    private NincordProperties nincordProperties;
    private SupporterConfig supporterConfig;

    public AppConfiguration(NincordProperties nincordProperties, SupporterConfig supporterConfig) {
        this.nincordProperties = nincordProperties;
        this.supporterConfig = supporterConfig;
    }

    @Bean
    public ShardManager shardManager(List<ListenerAdapter> listenerAdapters) {
        return DefaultShardManagerBuilder.create(nincordProperties.ninbotToken(),
                        EnumSet.of(GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                                GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES,
                                GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGES,
                                GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.MESSAGE_CONTENT))
                .addEventListeners(listenerAdapters.toArray())
                .setShardsTotal(-1)
                .build();
    }

    @Bean
    public SupporterCheck supporterCheck() {
        var check = new NinbotSupporterCheck();
        check.setPatreonServerId(supporterConfig.patreonServerId());
        return check;
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
}
