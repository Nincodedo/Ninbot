package dev.nincodedo.ninbot;

import dev.nincodedo.nincord.Schedulable;
import dev.nincodedo.nincord.logging.FormatLogObject;
import dev.nincodedo.nincord.stats.StatManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class NinbotCommandLineRunner implements CommandLineRunner {

    private final ShardManager shardManager;
    private final List<Schedulable<?, ?>> schedulableList;
    private final StatManager statManager;

    public NinbotCommandLineRunner(ShardManager shardManager, List<Schedulable<?, ?>> schedulableList,
            StatManager statManager) {
        this.shardManager = shardManager;
        this.schedulableList = schedulableList;
        this.statManager = statManager;
    }

    @Override
    public void run(String... args) {
        var shards = shardManager.getShards();
        log.info("Starting Ninbot with {} shard(s)", shards.size());
        shardManager.getShards().forEach(this::waitForShardStartup);
        schedulableList.forEach(schedule -> schedule.scheduleAll(shardManager));
    }

    private void waitForShardStartup(JDA jda) {
        try {
            jda.awaitReady();
            log.info("Shard ID {}: Connected to {} server(s)", jda.getShardInfo().getShardId(), jda.getGuilds().size());
            statManager.upsertCount("Connected Servers", "server", null, jda.getGuilds().size());
            jda.getGuilds()
                    .forEach(guild -> log.info("Shard ID {} connected to {}", jda.getShardInfo()
                            .getShardId(), FormatLogObject.guildInfo(guild)));
        } catch (InterruptedException e) {
            log.error("Failed to wait for shard to start", e);
        }
    }
}
