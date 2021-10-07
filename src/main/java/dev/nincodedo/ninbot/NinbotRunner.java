package dev.nincodedo.ninbot;

import dev.nincodedo.ninbot.common.Schedulable;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Slf4j
@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableJpaAuditing
public class NinbotRunner {

    private final ShardManager shardManager;

    private final List<Schedulable> schedulableList;

    public NinbotRunner(ShardManager shardManager, List<Schedulable> schedulableList) {
        this.shardManager = shardManager;
        this.schedulableList = schedulableList;
    }

    public static void main(String[] args) {
        SpringApplication.run(NinbotRunner.class, args);
    }

    private static void waitForShardStartup(JDA jda) {
        try {
            jda.awaitReady();
            log.info("Shard ID {}: Connected to {} server(s)", jda.getShardInfo()
                    .getShardId(), jda.getGuilds().size());
            jda.getGuilds().forEach(guild ->
                    log.info("Server ID: {}, Server Name: {}, Owner ID: {}, Owner Name: {}", guild.getId(),
                            guild.getName(), guild.getOwnerId(), guild.getOwner()
                                    .getEffectiveName()));
        } catch (InterruptedException e) {
            log.error("Failed to wait for shard to start", e);
        }
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext context) {
        return args -> {
            var shards = shardManager.getShards();
            log.info("Starting Ninbot with {} shard(s)", shards.size());
            shardManager.getShards().forEach(NinbotRunner::waitForShardStartup);
            schedulableList.forEach(schedule -> schedule.scheduleAll(shardManager));
            setNinbotActivity();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                shardManager.setActivity(Activity.playing("Shutting down, be back in a few!"));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    log.error("Failed to sleep before shutdown", e);
                }
            }));
        };
    }


    @Scheduled(fixedRate = 3600000)
    private void setNinbotActivity() {
        shardManager.setActivity(Activity.playing("Check out my fancy new slash commands!"));
    }
}
