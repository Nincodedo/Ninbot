package com.nincraft.ninbot;

import com.nincraft.ninbot.components.common.Schedulable;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@Log4j2
@ComponentScan({"com.nincraft.ninbot"})
public class Ninbot {

    @Autowired
    private ShardManager shardManager;

    @Autowired
    private List<Schedulable> schedulableList;

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext context) {
        return args -> {
            val shards = shardManager.getShards();
            log.info("Starting Ninbot with {} shard(s)", shards.size());
            shardManager.getShards()
                    .forEach(jda -> log.info("Shard ID {}: Connected to {} server(s)", jda.getShardInfo()
                            .getShardId(), jda.getGuilds().size()));
            schedulableList.forEach(schedule -> schedule.scheduleAll(shardManager));
            shardManager.setActivity(Activity.playing("say \"@Ninbot help\" for list of commands"));
        };
    }
}
