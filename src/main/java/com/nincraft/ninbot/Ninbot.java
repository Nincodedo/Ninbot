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
import org.springframework.scheduling.annotation.Scheduled;

import java.text.NumberFormat;
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
                    .forEach(jda -> {
                        try {
                            jda.awaitReady();
                            log.info("Shard ID {}: Connected to {} server(s)", jda.getShardInfo()
                                    .getShardId(), jda.getGuilds().size());
                            jda.getGuilds()
                                    .forEach(guild -> {
                                        double botPercentage = guild.getMembers()
                                                .stream()
                                                .filter(member -> member.getUser().isBot())
                                                .count() / (double) guild.getMembers().size();
                                        log.info("Server ID: {}, Name: {}, Owner: {}, Member Count: {}, Bot ratio: {}",
                                                guild.getId(), guild
                                                        .getName(), guild.getOwner()
                                                        .getUser()
                                                        .getName(), guild.getMembers()
                                                        .size(), NumberFormat.getPercentInstance()
                                                        .format(botPercentage));
                                    });
                        } catch (InterruptedException e) {
                            log.error("Failed to wait for shard to start", e);
                        }
                    });
            schedulableList.forEach(schedule -> schedule.scheduleAll(shardManager));
            setNinbotActivity();
        };
    }


    @Scheduled(fixedRate = 3600000)
    private void setNinbotActivity() {
        shardManager.setActivity(Activity.playing("say \"@Ninbot help\" for list of commands"));
    }
}
