package dev.nincodedo.ninbot;

import dev.nincodedo.ninbot.common.Schedulable;
import dev.nincodedo.ninbot.common.logging.FormatLogObject;
import dev.nincodedo.ninbot.components.stats.StatManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@Slf4j
@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableJpaAuditing
@EnableFeignClients
@ConfigurationPropertiesScan("dev.nincodedo.ninbot")
public class NinbotRunner {

    private final ShardManager shardManager;

    private final List<Schedulable> schedulableList;

    private final StatManager statManager;

    public NinbotRunner(ShardManager shardManager, List<Schedulable> schedulableList, StatManager statManager) {
        this.shardManager = shardManager;
        this.schedulableList = schedulableList;
        this.statManager = statManager;
    }

    public static void main(String[] args) {
        SpringApplication.run(NinbotRunner.class, args);
    }

    private void waitForShardStartup(JDA jda) {
        try {
            jda.awaitReady();
            log.info("Shard ID {}: Connected to {} server(s)", jda.getShardInfo().getShardId(), jda.getGuilds().size());
            statManager.upsertCount("Connected Servers", "server", null, jda.getGuilds().size());
            jda.getGuilds().forEach(guild -> log.info(FormatLogObject.guildInfo(guild)));
        } catch (InterruptedException e) {
            log.error("Failed to wait for shard to start", e);
        }
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext context) {
        return args -> {
            var shards = shardManager.getShards();
            log.info("Starting Ninbot with {} shard(s)", shards.size());
            shardManager.getShards().forEach(this::waitForShardStartup);
            schedulableList.forEach(schedule -> schedule.scheduleAll(shardManager));
        };
    }
}
