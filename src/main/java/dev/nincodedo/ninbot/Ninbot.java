package dev.nincodedo.ninbot;

import dev.nincodedo.ninbot.components.common.Schedulable;
import dev.nincodedo.ninbot.components.stats.StatCategory;
import dev.nincodedo.ninbot.components.stats.StatManager;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Log4j2
@ComponentScan({"dev.nincodedo.ninbot"})
public class Ninbot {

    @Autowired
    private ShardManager shardManager;

    @Autowired
    private StatManager statManager;

    @Autowired
    private List<Schedulable> schedulableList;

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext context) {
        return args -> {
            val shards = shardManager.getShards();
            log.info("Starting Ninbot with {} shard(s)", shards.size());
            shardManager.getShards()
                    .forEach(jda -> {
                        try {
                            jda.awaitReady();
                            log.info("Shard ID {}: Connected to {} server(s)", jda.getShardInfo()
                                    .getShardId(), jda.getGuilds().size());
                            for (Guild guild : jda.getGuilds()) {
                                log.info("Server ID: {}, Server Name: {}, Owner ID: {}, Owner Name: {}",
                                        guild.getId(), guild
                                                .getName(), guild.getOwnerId(), guild.getOwner().getEffectiveName());
                                statManager.recordCount("serverMemberCount", StatCategory.SERVER, guild.getId(),
                                        guild.getMemberCount());
                            }
                        } catch (InterruptedException e) {
                            log.error("Failed to wait for shard to start", e);
                        }
                    });
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
        shardManager.setActivity(Activity.playing("say \"@Ninbot help\" for list of commands"));
    }
}
